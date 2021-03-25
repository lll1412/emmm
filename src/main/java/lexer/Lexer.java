package lexer;

import token.Token;

import java.nio.charset.StandardCharsets;

public class Lexer {
    public static final char EOF = '\0';
    // 输入字符
    private final byte[] input;
    // 当前游标位置（待读取）
    private int cursor = -1;
    // 读取字节
    private byte ch;

    public Lexer(String input) {
        this.input = input.getBytes(StandardCharsets.UTF_8);
        readChar();
    }


    public Token nextToken() {
        var token = switch (ch) {
            case '=' -> Token.ASSIGN;
            case ';' -> Token.SEMICOLON;
            case '(' -> Token.LPAREN;
            case ')' -> Token.RPAREN;
            case ',' -> Token.COMMA;
            case '+' -> Token.PLUS;
            case '{' -> Token.LBRACE;
            case '}' -> Token.RBRACE;
            case '\0' -> Token.EOF;
            default -> {
                if (isLetter(ch)) {
                    var ident = readIdentifier();
                    yield Token.IDENT(ident);
                } else {
                    yield Token.ILLEGAL(ch);
                }
            }
        };
        readChar();
        return token;
    }

    /*
        辅助方法
     */

    private boolean isLetter(byte ch) {
//        return return Character.isLetter(ch);
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_';
    }

    private String readIdentifier() {
        var position = cursor;// 记忆起始位置
        int len = 0;
        // 在循环外必定判断出当前是字符了，所以第一次放心执行
        do {
            readChar();
            len++;
        } while (isLetter(ch));
        return new String(input, position, len);
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
}
