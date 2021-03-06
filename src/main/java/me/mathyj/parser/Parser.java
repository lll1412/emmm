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
        // ????????????????????????????????????
        var expr = parseExpression();
        // ???????????????????????????
        // ?????????token???????????????????????????????????????
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        // ??????????????????????????????????????????
        return expr;
    }

    private ReturnStatement parseReturnStatement() {
        // ???????????? return,

        // ??????????????????';'???'}',????????????
        if (peekTokenIs(TokenType.SEMICOLON) || peekTokenIs(TokenType.RBRACE)) {
            return new ReturnStatement();
        }
        // ??????return ??????????????????
        nextToken();
        var retVal = parseExpression();
        // ???????????? ????????????
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
//        lineNumberInc();
        return new ReturnStatement(retVal);
    }

    private LetStatement parseLetStatement() {
        // ???????????? let??????????????????????????????
        expectPeekIs(TokenType.IDENT);
        var name = new Identifier(curToken.literal());
        // ???????????? ??????????????????????????????????????????'='
        expectPeekIs(TokenType.ASSIGN);
        // ???????????? '='
        nextToken();
        var value = parseExpression();
        if (value instanceof FunctionLiteral fl) {
            fl.identifier = name;
        }
        // ???????????? ????????????
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
//        lineNumberInc();
        return new LetStatement(name, value);
    }

    private Expression parseExpression(Precedence precedence) {
        // ??????????????????????????????token
        var leftExpr = unaryFn().get();
        // ?????????token???????????? ?????? ?????????????????????????????????token??? ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        while (peekToken.type() != TokenType.SEMICOLON && precedence.lt(Precedence.from(peekToken))) {
            //????????????????????????????????????????????????
            nextToken();
            // ???????????????????????????
            leftExpr = binaryFn().apply(leftExpr);
        }
        return leftExpr;
    }

    private Expression parseExpression() {
        return parseExpression(Precedence.LOWEST);
    }

    // ?????????????????????????????????
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
     * ?????????????????????????????????
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
     * ?????????????????????
     * []
     * [a]
     * [a  ,b  ,c  ,d]  ','??????????????????????????????
     */
    private ArrayLiteral parseArrayLiteral() {
        // curToken: '['
        List<Expression> elements = parseExpressionList(TokenType.RBRACKET);
        // curToken: ']'
        return new ArrayLiteral(elements);
    }

    private FunctionLiteral parseFunctionLiteral() {
        // ???????????? Token('fn')
        Identifier name = null;
        if (peekTokenIs(TokenType.IDENT)) {// ??????????????????
            nextToken();
            name = new Identifier(curToken.literal());
        }
        expectPeekIs(TokenType.LPAREN);
        List<Identifier> params = parseExpressionList(TokenType.RPAREN);
        // ????????????  Token(')')
        expectPeekIs(TokenType.LBRACE);
        var body = parseBlockStatement();
        // ???????????? Token('}')
        return new FunctionLiteral(name, params, body);
    }

    /**
     * ?????????????????????
     */
    @SuppressWarnings("all")
    private <T> List<T> parseExpressionList(TokenType endToken) {
        List<T> elements = new ArrayList<>();
        if (peekTokenNot(endToken)) {// ??????endToken????????????
            nextToken();
            elements.add((T) parseExpression());
            while (peekTokenIs(TokenType.COMMA)) {// ??????????????? ??????????????? ????????????
                nextToken();// skip curToken
                nextToken();// skip delimiter
                elements.add((T) parseExpression());
            }
        }
        expectPeekIs(endToken);
        return elements;
    }

    /**
     * ??????If?????????
     */
    private IfExpression parseIfExpression() {
        // ???????????? Token('if')
        expectPeekIs(TokenType.LPAREN);
        // ???????????? Token('(')
        var cond = parseExpression();
        // ???????????? Token(')')
        expectPeekIs(TokenType.LBRACE);
        BlockStatement conseq = parseBlockStatement();
        //???????????? Token('}')
        BlockStatement alter = null;
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();
            expectPeekIs(TokenType.LBRACE);
            alter = parseBlockStatement();
            // ???????????? Token('}')
        }
        return new IfExpression(cond, conseq, alter);
    }

    /**
     * ??????????????????
     */
    private BlockStatement parseBlockStatement() {
        // ???????????? Token('{')
        nextToken();
        List<Statement> statements = new ArrayList<>();
        while (curTokenNot(TokenType.RBRACE)) {
            var statement = parseStatement();
            statements.add(statement);
            nextToken();
        }
        // ???????????? Token('}')
        return new BlockStatement(statements);
    }

    /**
     * ?????????????????????
     */
    private Expression parseGroupExpression() {
        // ????????????'('
        nextToken();
        var expr = parseExpression();
        expectPeekIs(TokenType.RPAREN);// ??????????????????')'
        return expr;
    }

    /**
     * ???????????????
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
     * ?????????????????????
     */
    private UnaryExpression parseUnaryExpression() {
        //???????????????????????????
        var operator = UnaryOperator.from(curToken);
        nextToken();
        //?????????????????????(?????????)
        var rightExpr = parseExpression(Precedence.PREFIX);
        return new UnaryExpression(operator, rightExpr);
    }

    // ?????????????????????????????????
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
        //?????????????????????
        var operator = BinaryOperator.from(curToken);
        var precedence = Precedence.from(curToken);
        nextToken();
        //????????????????????????
        var right = parseExpression(precedence);
        return new BinaryExpression(left, operator, right);
    }

    /**
     * ??????Token???????????????????????????
     */
    private void expectPeekIs(TokenType type) {
        if (peekToken.type() == type) {
            nextToken();
        } else {
            throw new UnexpectedTokenException(type, peekToken);
        }
    }

    /**
     * ??????Token???????????????????????????
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
     * ?????????token
     */
    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    /**
     * ??????????????????????????????????????????
     */
    private void skipLine() {
        while (curTokenNot(TokenType.SEMICOLON) && curTokenNot(TokenType.EOF)) nextToken();
    }
}
