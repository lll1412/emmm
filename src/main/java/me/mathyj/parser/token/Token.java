package me.mathyj.parser.token;

import java.util.HashMap;
import java.util.Map;

public record Token(TokenType type, String literal) {
    // 关键字
    private static final Map<String, Token> keywords = new HashMap<>() {{
        put("let", build(TokenType.LET));
        put("fn", build(TokenType.FUNCTION));
        put("if", build(TokenType.IF));
        put("else", build(TokenType.ELSE));
        put("true", build(TokenType.TRUE));
        put("false", build(TokenType.FALSE));
        put("return", build(TokenType.RETURN));
        put("for", build(TokenType.FOR));
    }};

    /*
        方便构建Token的方法
     */

    public static Token IDENT(String literal) {
        return build(TokenType.IDENT, literal);
    }

    public static Token IDENT_OR_KEYWORD(String literal) {
        return keywords.getOrDefault(literal, IDENT(literal));
    }

    public static Token INT(int literal) {
        return build(TokenType.INT, literal + "");
    }

    public static Token STRING(String val) {
        return build(TokenType.STRING, val);
    }

    public static Token ILLEGAL(char ch) {
        return build(TokenType.ILLEGAL, "char: " + ch);
    }

    private static Token build(TokenType type, String literal) {
        return new Token(type, literal);
    }

    public static Token build(TokenType type) {
        return build(type, type.literal);
    }

    @Override
    public String toString() {
        var format = switch (type) {
            case INT -> "%s(%s)";
            default -> "%s('%s')";
        };
        return format.formatted(type, literal);
    }

}
