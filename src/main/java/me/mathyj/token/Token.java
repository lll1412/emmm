package me.mathyj.token;

import java.util.HashMap;
import java.util.Map;

public record Token(TokenType type, String literal) {
    public static final Token EOF = build(TokenType.EOF);

    public static final Token LET = build(TokenType.LET);
    public static final Token FUNCTION = build(TokenType.FUNCTION);
    public static final Token IF = build(TokenType.IF);
    public static final Token ELSE = build(TokenType.ELSE);
    public static final Token FOR = build(TokenType.FOR);
    public static final Token TRUE = build(TokenType.TRUE);
    public static final Token FALSE = build(TokenType.FALSE);
    public static final Token RETURN = build(TokenType.RETURN);

    public static final Token ASSIGN = build(TokenType.ASSIGN);
    public static final Token PLUS = build(TokenType.PLUS);
    public static final Token MINUS = build(TokenType.MINUS);
    public static final Token ASTERISK = build(TokenType.ASTERISK);
    public static final Token SLASH = build(TokenType.SLASH);
    public static final Token BANG = build(TokenType.BANG);
    public static final Token LT = build(TokenType.LT);
    public static final Token GT = build(TokenType.GT);
    public static final Token EQ = build(TokenType.EQ);
    public static final Token NE = build(TokenType.NE);

    public static final Token COMMA = build(TokenType.COMMA);
    public static final Token SEMICOLON = build(TokenType.SEMICOLON);
    public static final Token COLON = build(TokenType.COLON);

    public static final Token LPAREN = build(TokenType.LPAREN);
    public static final Token RPAREN = build(TokenType.RPAREN);
    public static final Token LBRACE = build(TokenType.LBRACE);
    public static final Token RBRACE = build(TokenType.RBRACE);
    public static final Token LBRACKET = build(TokenType.LBRACKET);
    public static final Token RBRACKET = build(TokenType.RBRACKET);

    // 关键字
    private static final Map<String, Token> keywords = new HashMap<>() {{
        put("let", LET);
        put("fn", FUNCTION);
        put("if", IF);
        put("else", ELSE);
        put("true", TRUE);
        put("false", FALSE);
        put("return", RETURN);
        put("for", FOR);
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

    private static Token build(TokenType type) {
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
