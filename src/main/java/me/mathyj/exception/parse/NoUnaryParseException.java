package me.mathyj.exception.parse;

import me.mathyj.token.Token;

public class NoUnaryParseException extends ParseException {
    private final Token token;

    public NoUnaryParseException(Token token) {
        this.token = token;
    }

    @Override
    public String getMessage() {
        return "no unary parse function for token: %s".formatted(token);
    }
}
