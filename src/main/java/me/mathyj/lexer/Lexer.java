package me.mathyj.lexer;

import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

public class Lexer {
    public static final char EOF = '\0';
    // 输入字符数组
    private final char[] input;

    // 游标位置，指向待读取的字符
    private int cursor = -1;

    // 读取字符
    private char ch;

    public Lexer(String input) {
        this.input = input.toCharArray();
        // 光标移向待读取字符
        readChar();
    }


    public Token nextToken() {
        skipWhiteSpace();
        // 先读取单字符 获取普通Token
        Token token;
        // 如果不在普通Token中
        switch (ch) {
            case '!':
                // maybe "!="
                if (peekChar('=')) {
                    token = Token.build(TokenType.NE);
                    readChar();
                } else {
                    token = Token.build(TokenType.BANG);
                }
                break;
            case '=':
                // maybe "=="
                if (peekChar('=')) {
                    token = Token.build(TokenType.EQ);
                    readChar();
                } else {
                    token = Token.build(TokenType.ASSIGN);
                }
                break;
            case '+':
                if (peekChar('+')) {
                    readChar();
                    token = Token.build(TokenType.INC);
                } else if (peekChar('=')) {
                    readChar();
                    token = Token.build(TokenType.PLUS_ASSIGN);
                } else {
                    token = Token.build(TokenType.PLUS);
                }
                break;
            case '-':
                if (peekChar('-')) {
                    readChar();
                    token = Token.build(TokenType.DEC);
                } else if (peekChar('=')) {
                    readChar();
                    token = Token.build(TokenType.MINUS_ASSIGN);
                } else {
                    token = Token.build(TokenType.MINUS);
                }
                break;
            case '*':
                if (peekChar('=')) {
                    readChar();
                    token = Token.build(TokenType.ASTERISK_ASSIGN);
                } else {
                    token = Token.build(TokenType.ASTERISK);
                }
                break;
            case '/':
                if (peekChar('=')) {
                    readChar();
                    token = Token.build(TokenType.SLASH_ASSIGN);
                } else {
                    token = Token.build(TokenType.SLASH);
                }
                break;
            case '>':
                if (peekChar('=')) {
                    readChar();
                    token = Token.build(TokenType.GE);
                } else {
                    token = Token.build(TokenType.GT);
                }
                break;
            case '<':
                if (peekChar('=')) {
                    readChar();
                    token = Token.build(TokenType.LE);
                } else {
                    token = Token.build(TokenType.LT);
                }
                break;
            case '&':
                if (peekChar('&')) {
                    readChar();
                    token = Token.build(TokenType.AND);
                } else {
                    token = Token.build(TokenType.BIT_AND);
                }
                break;
            case '|':
                if (peekChar('|')) {
                    readChar();
                    token = Token.build(TokenType.OR);
                } else {
                    token = Token.build(TokenType.BIT_OR);
                }
                break;
            case ';':
                token = Token.build(TokenType.SEMICOLON);
                break;
            case ':':
                token = Token.build(TokenType.COLON);
                break;
            case ',':
                token = Token.build(TokenType.COMMA);
                break;
            case '(':
                token = Token.build(TokenType.LPAREN);
                break;
            case ')':
                token = Token.build(TokenType.RPAREN);
                break;
            case '{':
                token = Token.build(TokenType.LBRACE);
                break;
            case '}':
                token = Token.build(TokenType.RBRACE);
                break;
            case '[':
                token = Token.build(TokenType.LBRACKET);
                break;
            case ']':
                token = Token.build(TokenType.RBRACKET);
                break;
            case '\0':
                token = Token.build(TokenType.EOF);
                break;
            default:
                // 这几种情况读取完后，不读取下一个字符，直接返回
                // 因为内部的最后一次循环 已经指向了待读取的字符
                if (isLetter(ch)) {// 字符串
                    var ident = readIdentifier();
                    return Token.IDENT_OR_KEYWORD(ident);
                } else if (isDigit(ch)) {// 数字
                    var digit = readNumber();
                    return Token.INT(digit);
                } else if (ch == '\'' || ch == '"') {
                    var str = readString();
                    return Token.STRING(str);
                } else {
                    return Token.ILLEGAL(ch);
                }
        }
        // 光标移向待读取字符
        readChar();
        return token;
    }

    private String readString() {
        var endCh = ch;
        int count = 0;
        var offset = cursor + 1;// 忽略引号
        readChar();// 跳过开头的引号 '\'' / '"'
        do {
            readChar();
            count++;
        } while (ch != endCh && ch != EOF);
        readChar();// 跳过结尾的引号
        return new String(input, offset, count);
    }

    /*
        辅助方法
     */

    /**
     * 是否是数字
     */
    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
//        return ch >= '0' && ch <= '9';
    }

    /**
     * 跳过空格
     */
    private void skipWhiteSpace() {
        while (Character.isWhitespace(ch)) readChar();
//        while (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') readChar();
    }

    /**
     * 是否是字符
     */
    private boolean isLetter(char ch) {
        return Character.isLetter(ch);
//        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_';
    }

    /**
     * 读取标识符
     */
    private String readIdentifier() {
        var startPos = cursor;// 记忆起始位置
        int len = 0;
        do {
            // 在循环外必定判断出当前是字符了，所以第一次放心执行
            readChar();
            len++;
        } while (isLetter(ch) || isDigit(ch));
        // 当前cursor已经指向下一个待解析字节
        return new String(input, startPos, len);
    }

    /**
     * 读取数字
     */
    private int readNumber() {
        var startPos = cursor;
        int len = 0;
        do {
            // 在循环外必定判断出当前是字符了，所以第一次放心执行
            readChar();
            len++;
        } while (isDigit(ch));
        // 当前cursor已经指向下一个待解析字节
        String numStr = new String(input, startPos, len);
        return Integer.parseInt(numStr);
    }

    /**
     * 读取字符
     */
    private void readChar() {
        cursor++;
        if (cursor >= input.length) {
            ch = EOF;
        } else {
            ch = input[cursor];
        }
    }

    private char peekChar() {
        if (cursor + 1 >= input.length) {
            return EOF;
        } else {
            return input[cursor + 1];
        }
    }

    private boolean peekChar(char c) {
        return c == peekChar();
    }

}
