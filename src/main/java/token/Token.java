package token;

public record Token(TokenType type, String literal) {
    public static final Token LET = build(TokenType.LET, "let");
    public static final Token FUNCTION = build(TokenType.FUNCTION, "fn");
    public static final Token EOF = build(TokenType.EOF, "");

    public static final Token ASSIGN = build(TokenType.ASSIGN);
    public static final Token PLUS = build(TokenType.PLUS);

    public static final Token COMMA = build(TokenType.COMMA);
    public static final Token SEMICOLON = build(TokenType.SEMICOLON);

    public static final Token LPAREN = build(TokenType.LPAREN);
    public static final Token RPAREN = build(TokenType.RPAREN);
    public static final Token LBRACE = build(TokenType.LBRACE);
    public static final Token RBRACE = build(TokenType.RBRACE);

    /*
        辅助构建Token的方法
     */
    public static Token build(TokenType type, String literal) {
        return new Token(type, literal);
    }

    public static Token IDENT(String literal) {
        return build(TokenType.IDENT, literal);
    }

    public static Token INT(int literal) {
        return build(TokenType.INT, literal + "");
    }

    public static Token ILLEGAL(String ch) {
        return build(TokenType.ILLEGAL, ch);
    }
    public static Token ILLEGAL(byte ch) {
        return build(TokenType.ILLEGAL, String.valueOf(ch));
    }

    private static Token build(TokenType type) {
        return build(type, type.literal);
    }
}
