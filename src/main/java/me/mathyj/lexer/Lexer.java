package me.mathyj.lexer;

import me.mathyj.token.Token;

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
                    token = Token.NE;
                    readChar();
                } else {
                    token = Token.BANG;
                }
                break;
            case '=':
                // maybe "=="
                if (peekChar('=')) {
                    token = Token.EQ;
                    readChar();
                } else {
                    token = Token.ASSIGN;
                }
                break;
            case '+':
                token = Token.PLUS;
                break;
            case '-':
                token = Token.MINUS;
                break;
            case '*':
                token = Token.ASTERISK;
                break;
            case '/':
                token = Token.SLASH;
                break;
            case '>':
                token = Token.GT;
                break;
            case '<':
                token = Token.LT;
                break;
            case ';':
                token = Token.SEMICOLON;
                break;
            case ',':
                token = Token.COMMA;
                break;
            case '(':
                token = Token.LPAREN;
                break;
            case ')':
                token = Token.RPAREN;
                break;
            case '{':
                token = Token.LBRACE;
                break;
            case '}':
                token = Token.RBRACE;
                break;
            case '\0':
                token = Token.EOF;
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
                } else {
                    return Token.ILLEGAL(ch);
                }
        }
        // 光标移向待读取字符
        readChar();
        return token;
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