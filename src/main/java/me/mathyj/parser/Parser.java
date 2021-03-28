package me.mathyj.parser;

import me.mathyj.ast.Program;
import me.mathyj.ast.expression.*;
import me.mathyj.ast.operator.BinaryOperator;
import me.mathyj.ast.operator.UnaryOperator;
import me.mathyj.ast.statement.*;
import me.mathyj.exception.parse.NoBinaryParseException;
import me.mathyj.exception.parse.NoUnaryParseException;
import me.mathyj.exception.parse.ParseException;
import me.mathyj.exception.parse.UnexpectedTokenException;
import me.mathyj.lexer.Lexer;
import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

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
        while (curToken != Token.EOF) {
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
            default -> parseExpressionStatement();
        };
    }

    private Statement parseExpressionStatement() {
        // 当前指向语句的第一个符号
        var expr = parseExpression();
        // 当前指向表达式结尾
        var statement = new ExpressionStatement(expr);
        // 下一个token是否是分号，是的话指向分号
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        // 当前指向结尾分号（语句末尾）
        return statement;
    }

    private Statement parseReturnStatement() {
        // 当前指向 return,
        nextToken();
        var retVal = parseExpression();
        // 当前指向 结尾分号
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
//        lineNumberInc();
        return new ReturnStatement(retVal);
    }

    private Statement parseLetStatement() {
        // 当前指向 let，期望下一个是标识符
        expectPeekIs(TokenType.IDENT);
        var name = new Identifier(curToken.literal());
        // 当前指向 标识符，期望下一个是赋值符号'='
        expectPeekIs(TokenType.ASSIGN);
        // 当前指向 '='
        nextToken();
        var value = parseExpression();
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
        while (peekToken != Token.SEMICOLON && precedence.lt(Precedence.from(peekToken))) {
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
            case TRUE, FALSE -> this::parseBooleanLiteral;
            case LPAREN -> this::parseGroupExpression;
            case IF -> this::parseIfExpression;
            case FUNCTION -> this::parseFunctionLiteral;
            default -> throw new NoUnaryParseException(curToken);
        };
    }

    private Expression parseFunctionLiteral() {
        // 当前指向 Token('fn')
        Identifier name = null;
        if (peekTokenIs(TokenType.IDENT)) {// 如果有函数名
            nextToken();
            name = new Identifier(curToken.literal());
        }
        expectPeekIs(TokenType.LPAREN);
        var params = parseFunctionParameters();
        // 当前指向  Token(')')
        expectPeekIs(TokenType.LBRACE);
        var body = parseBlockStatement();
        // 当前指向 Token('}')
        return new FunctionLiteral(name, params, body);
    }

    /**
     * 解析参数列表 (a, b, c)
     */
    private FunctionParams parseFunctionParameters() {
        // cur: '('
        List<Identifier> params = new ArrayList<>();
        if (peekTokenNot(TokenType.RPAREN)) {//不是右括号说明有参数
            nextToken();//skip '('
            // cur: ident   peek: ',' or ')'
            while (peekTokenIs(TokenType.COMMA)) {
                params.add(new Identifier(curToken.literal()));
                // cur: ident   peek: ','
                nextToken();
                // cur: ','   peek: ident
                nextToken();
            }
            params.add(new Identifier(curToken.literal()));
        }
        expectPeekIs(TokenType.RPAREN);
        // cur: ')'
        return new FunctionParams(params);
    }

    /**
     * 解析If表达式
     */
    private Expression parseIfExpression() {
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
    private Expression parseIdentifier() {
        return new Identifier(curToken.literal());
    }

    private Expression parseIntegerLiteral() {
        return new IntegerLiteral(curToken.literal());
    }

    private Expression parseBooleanLiteral() {
        return new BooleanLiteral(curToken);
    }

    private Expression parseStringLiteral() {
        return new StringLiteral(curToken.literal());
    }

    /**
     * 解析一元表达式
     */
    private Expression parseUnaryExpression() {
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
            case PLUS, MINUS, ASTERISK, SLASH, EQ, NE, LT, GT -> this::parseBinaryExpression;
            case LPAREN -> this::parseCallExpression;
            default -> throw new NoBinaryParseException(curToken);
        };
    }

    private Expression parseCallExpression(Expression expression) {
        var identifier = ((Identifier) expression);
        var arguments = parseCallArguments();
        return new CallExpression(identifier, arguments);
    }

    private CallArguments parseCallArguments() {
        // cur: '('
        List<Expression> args = new ArrayList<>();
        if (peekTokenNot(TokenType.RPAREN)) {//不是右括号说明有参数
            nextToken();//skip '('
            args.add(parseExpression());
            // cur: ident   peek: ',' or ')'
            while (peekTokenIs(TokenType.COMMA)) {
                nextToken();
                nextToken();
                args.add(parseExpression());
            }
        }
        expectPeekIs(TokenType.RPAREN);
        // cur: ')'
        return new CallArguments(args);
    }

    private Expression parseBinaryExpression(Expression left) {
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
