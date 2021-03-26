package me.mathyj.parser;

import me.mathyj.ast.*;
import me.mathyj.exception.UnSupportedStatement;
import me.mathyj.exception.UnexpectedToken;
import me.mathyj.lexer.Lexer;
import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

public class Parser {
    private final Lexer lexer;
    private int lineNumber = 1;

    private Token curToken;
    private Token peekToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        nextToken();
        nextToken();
    }

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
            } catch (RuntimeException e) {
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
            default -> throw new UnSupportedStatement(curToken, lineNumber);
        };
    }

    private Statement parseReturnStatement() {
        // 当前指向 return,
        var retVal = parseExpression();
        return new ReturnStatement(retVal);
    }

    private Statement parseLetStatement() {
        // 当前指向 let，期望下一个是标识符
        expectPeekIs(TokenType.IDENT);
        var name = new Identifier(curToken.literal());
        // 当前指向 标识符，期望下一个是赋值符号'='
        expectPeekIs(TokenType.ASSIGN);
        // 解析表达式
        var value = parseExpression();
        // 当前指向 结尾分号
        lineNumberInc();
        return new LetStatement(name, value);
    }

    private Expression parseExpression() {
        // todo 暂时跳过表达式

        // 当前指向表达式的最后一个token
        while (curTokenNot(TokenType.SEMICOLON)) {
            nextToken();
        }
        return null;
    }

    /**
     * 预读Token类型是否为期待类型
     */
    private void expectPeekIs(TokenType type) {
        if (peekToken.type() == type) {
            nextToken();
        } else {
            throw new UnexpectedToken(type, peekToken, lineNumber);
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

    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    private void skipLine() {
        while (curTokenNot(TokenType.SEMICOLON)) nextToken();
        lineNumberInc();
    }

    private void lineNumberInc(int c) {
        lineNumber += c;
    }

    private void lineNumberInc() {
        lineNumberInc(1);
    }
}
