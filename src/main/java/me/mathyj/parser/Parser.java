package me.mathyj.parser;

import me.mathyj.parser.ast.Program;
import me.mathyj.parser.ast.expression.*;
import me.mathyj.parser.ast.operator.BinaryOperator;
import me.mathyj.parser.ast.operator.UnaryOperator;
import me.mathyj.parser.ast.statement.*;
import me.mathyj.exception.parse.NoBinaryParseException;
import me.mathyj.exception.parse.NoUnaryParseException;
import me.mathyj.exception.parse.ParseException;
import me.mathyj.exception.parse.UnexpectedTokenException;
import me.mathyj.parser.lexer.Lexer;
import me.mathyj.parser.token.Token;
import me.mathyj.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parser {
    private final Lexer lexer;
    private Token curToken;
    private Token peekToken;

    public Parser(String inputs) {
        this.lexer = new Lexer(inputs);
        nextToken();
        nextToken();
    }


    public Program parseProgram() {
        var program = new Program();
        while (curToken.type() != TokenType.EOF) {
            try {
                var statement = parseStatement();
                program.addStatement(statement);
            } catch (ParseException e) {
                program.addError(e);
                skipLine();
            }
            nextToken();
        }
        return program;
    }

    private Statement parseStatement() {
        return switch (curToken.type()) {
            case LET -> parseLetStatement();
            case RETURN -> parseReturnStatement();
            case FOR -> parseForStatement();
            case FUNCTION -> parseFunctionLiteral();
            default -> parseExpressionStatement();
        };
    }

    private ForStatement parseForStatement() {
        var forStatement = new ForStatement();
        // cur: for
        nextToken();
        if (peekTokenIs(TokenType.LET)) {
            nextToken();
            forStatement.setInitial(parseLetStatement());
        }
        // cur: ';'
        if (peekTokenNot(TokenType.SEMICOLON)) {
            nextToken();
            forStatement.setCondition(parseExpression());
        }
        expectPeekIs(TokenType.SEMICOLON);
        if (peekTokenNot(TokenType.RPAREN)) {
            nextToken();
            forStatement.setLast(parseExpression());
        }
        expectPeekIs(TokenType.RPAREN);
        // cur: ')'
        expectPeekIs(TokenType.LBRACE);
        // cur: '{'
        forStatement.setBlock(parseBlockStatement());
        return forStatement;
    }

    private Expression parseExpressionStatement() {
        // 当前指向语句的第一个符号
        var expr = parseExpression();
        // 当前指向表达式结尾
        // 下一个token是否是分号，是的话指向分号
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        // 当前指向结尾分号（语句末尾）
        return expr;
    }

    private ReturnStatement parseReturnStatement() {
        // 当前指向 return,

        // 如果下一个是';'或'}',无返回值
        if (peekTokenIs(TokenType.SEMICOLON) || peekTokenIs(TokenType.RBRACE)) {
            return new ReturnStatement();
        }
        // 跳过return ，指向表达式
        nextToken();
        var retVal = parseExpression();
        // 当前指向 结尾分号
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
//        lineNumberInc();
        return new ReturnStatement(retVal);
    }

    private LetStatement parseLetStatement() {
        // 当前指向 let，期望下一个是标识符
        expectPeekIs(TokenType.IDENT);
        var name = new Identifier(curToken.literal());
        // 当前指向 标识符，期望下一个是赋值符号'='
        expectPeekIs(TokenType.ASSIGN);
        // 当前指向 '='
        nextToken();
        var value = parseExpression();
        if (value instanceof FunctionLiteral fl) {
            fl.identifier = name;
        }
        // 当前指向 结尾分号
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
//        lineNumberInc();
        return new LetStatement(name, value);
    }

    private Expression parseExpression(Precedence precedence) {
        // 当前指向表达式第一个token
        var leftExpr = unaryFn().get();
        // 下一个token不是分号 并且 当前的优先级低于下一个token的 则优先计算后面的表达式（如果当前优先级高，则当前就已经是一个完整的表达式了）
        while (peekToken.type() != TokenType.SEMICOLON && precedence.lt(Precedence.from(peekToken))) {
            //当前指向前面一元表达式解析的结尾
            nextToken();
            // 当前指向二元操作符
            leftExpr = binaryFn().apply(leftExpr);
        }
        return leftExpr;
    }

    private Expression parseExpression() {
        return parseExpression(Precedence.LOWEST);
    }

    // 一元函数（一个操作数）
    private Supplier<Expression> unaryFn() {
        return switch (curToken.type()) {
            case IDENT -> this::parseIdentifier;
            case INT -> this::parseIntegerLiteral;
            case STRING -> this::parseStringLiteral;
            case BANG, MINUS -> this::parseUnaryExpression;
            case INC, DEC -> this::parseUnaryAssignExpression;
            case TRUE, FALSE -> this::parseBooleanLiteral;
            case LPAREN -> this::parseGroupExpression;
            case LBRACKET -> this::parseArrayLiteral;
            case LBRACE -> this::parseHashLiteral;
            case IF -> this::parseIfExpression;
            case FUNCTION -> this::parseFunctionLiteral;
            default -> throw new NoUnaryParseException(curToken);
        };
    }

    /**
     * 一元操作符的赋值表达式
     */
    private AssignExpression parseUnaryAssignExpression() {
        // cur: '++' or '--'
        var op = curToken.type();
        nextToken();
        var expr = parseIdentifier();
        return op == TokenType.INC ? AssignExpression.INC(expr) : AssignExpression.DEC(expr);
    }

    // example: {key: val, key2, val2}
    private HashLiteral parseHashLiteral() {
        // cur: '{'
        List<HashLiteral.Pair> pairs = new ArrayList<>();
        if (peekTokenNot(TokenType.RBRACE)) {
            nextToken();
            // cur: key
            var key = parseExpression();
            nextToken();
            nextToken();
            // cur: ':'
            var val = parseExpression();
            pairs.add(HashLiteral.Pair.of(key, val));
            // cur: ',' or '}'
            while (peekTokenIs(TokenType.COMMA)) {
                nextToken();
                // cur: ','
                nextToken();
                // cur: key
                key = parseExpression();
                nextToken();
                nextToken();
                // cur: ':'
                val = parseExpression();
                pairs.add(HashLiteral.Pair.of(key, val));
            }
        }
        expectPeekIs(TokenType.RBRACE);
        return new HashLiteral(pairs);
    }

    /**
     * 解析数组字面量
     * []
     * [a]
     * [a  ,b  ,c  ,d]  ','号来识别是否还有参数
     */
    private ArrayLiteral parseArrayLiteral() {
        // curToken: '['
        List<Expression> elements = parseExpressionList(TokenType.RBRACKET);
        // curToken: ']'
        return new ArrayLiteral(elements);
    }

    private FunctionLiteral parseFunctionLiteral() {
        // 当前指向 Token('fn')
        Identifier name = null;
        if (peekTokenIs(TokenType.IDENT)) {// 如果有函数名
            nextToken();
            name = new Identifier(curToken.literal());
        }
        expectPeekIs(TokenType.LPAREN);
        List<Identifier> params = parseExpressionList(TokenType.RPAREN);
        // 当前指向  Token(')')
        expectPeekIs(TokenType.LBRACE);
        var body = parseBlockStatement();
        // 当前指向 Token('}')
        return new FunctionLiteral(name, params, body);
    }

    /**
     * 解析表达式列表
     */
    @SuppressWarnings("all")
    private <T> List<T> parseExpressionList(TokenType endToken) {
        List<T> elements = new ArrayList<>();
        if (peekTokenNot(endToken)) {// 不是endToken说明有值
            nextToken();
            elements.add((T) parseExpression());
            while (peekTokenIs(TokenType.COMMA)) {// 如果是逗号 说明还有值 继续解析
                nextToken();// skip curToken
                nextToken();// skip delimiter
                elements.add((T) parseExpression());
            }
        }
        expectPeekIs(endToken);
        return elements;
    }

    /**
     * 解析If表达式
     */
    private IfExpression parseIfExpression() {
        // 当前指向 Token('if')
        expectPeekIs(TokenType.LPAREN);
        // 当前指向 Token('(')
        var cond = parseExpression();
        // 当前指向 Token(')')
        expectPeekIs(TokenType.LBRACE);
        BlockStatement conseq = parseBlockStatement();
        //当前指向 Token('}')
        BlockStatement alter = null;
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();
            expectPeekIs(TokenType.LBRACE);
            alter = parseBlockStatement();
            // 当前指向 Token('}')
        }
        return new IfExpression(cond, conseq, alter);
    }

    /**
     * 解析表达式块
     */
    private BlockStatement parseBlockStatement() {
        // 当前指向 Token('{')
        nextToken();
        List<Statement> statements = new ArrayList<>();
        while (curTokenNot(TokenType.RBRACE)) {
            var statement = parseStatement();
            statements.add(statement);
            nextToken();
        }
        // 当前指向 Token('}')
        return new BlockStatement(statements);
    }

    /**
     * 解析分组表达式
     */
    private Expression parseGroupExpression() {
        // 当前指向'('
        nextToken();
        var expr = parseExpression();
        expectPeekIs(TokenType.RPAREN);// 确保下一个是')'
        return expr;
    }

    /**
     * 解析标识符
     */
    private Identifier parseIdentifier() {
        return new Identifier(curToken.literal());
    }

    private IntegerLiteral parseIntegerLiteral() {
        return new IntegerLiteral(curToken.literal());
    }

    private BooleanLiteral parseBooleanLiteral() {
        return BooleanLiteral.valueOf(curToken);
    }

    private StringLiteral parseStringLiteral() {
        return new StringLiteral(curToken.literal());
    }

    /**
     * 解析一元表达式
     */
    private UnaryExpression parseUnaryExpression() {
        //当前指向一元操作符
        var operator = UnaryOperator.from(curToken);
        nextToken();
        //当前指向操作数(表达式)
        var rightExpr = parseExpression(Precedence.PREFIX);
        return new UnaryExpression(operator, rightExpr);
    }

    // 二元函数（二个操作数）
    private Function<Expression, Expression> binaryFn() {
        return switch (curToken.type()) {
            case PLUS, MINUS, ASTERISK, SLASH, EQ, NE, LT, GT, LE, GE, AND, OR -> this::parseBinaryExpression;
            case ASSIGN -> this::parseAssignExpression;
            case PLUS_ASSIGN, MINUS_ASSIGN, ASTERISK_ASSIGN, SLASH_ASSIGN-> this::parseBinaryAssignExpression;
            case LPAREN -> this::parseCallExpression;
            case LBRACKET -> this::parseIndexExpression;
            default -> throw new NoBinaryParseException(curToken);
        };
    }

    private AssignExpression parseBinaryAssignExpression(Expression left) {
        var op = curToken.type();
        nextToken();
        var right = parseExpression();
        return new AssignExpression((Identifier) left, op, right);
    }

    private AssignExpression parseAssignExpression(Expression left) {
        nextToken();
        var right = parseExpression();
        return new AssignExpression(((Identifier) left), right);
    }

    private IndexExpression parseIndexExpression(Expression left) {
        // cur: '['
        nextToken();
        var index = parseExpression();
        nextToken();
        return new IndexExpression(left, index);
    }

    private CallExpression parseCallExpression(Expression left) {
        List<Expression> arguments = parseExpressionList(TokenType.RPAREN);
        return new CallExpression(left, arguments);
    }

    private BinaryExpression parseBinaryExpression(Expression left) {
        //当前指向操作符
        var operator = BinaryOperator.from(curToken);
        var precedence = Precedence.from(curToken);
        nextToken();
        //当前指向右表达式
        var right = parseExpression(precedence);
        return new BinaryExpression(left, operator, right);
    }

    /**
     * 预读Token类型是否为期待类型
     */
    private void expectPeekIs(TokenType type) {
        if (peekToken.type() == type) {
            nextToken();
        } else {
            throw new UnexpectedTokenException(type, peekToken);
        }
    }

    /**
     * 当前Token类型是否为期待类型
     */
    private boolean curTokenIs(TokenType type) {
        return curToken.type() == type;
    }

    private boolean curTokenNot(TokenType type) {
        return !curTokenIs(type);
    }

    private boolean peekTokenIs(TokenType type) {
        return peekToken.type() == type;
    }

    private boolean peekTokenNot(TokenType type) {
        return !peekTokenIs(type);
    }

    /**
     * 下一个token
     */
    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    /**
     * 跳过当前行（由分号结尾的行）
     */
    private void skipLine() {
        while (curTokenNot(TokenType.SEMICOLON) && curTokenNot(TokenType.EOF)) nextToken();
    }
}
