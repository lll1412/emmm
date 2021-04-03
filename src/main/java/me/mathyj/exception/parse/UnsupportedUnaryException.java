package me.mathyj.exception.parse;

import me.mathyj.parser.token.Token;

public class UnsupportedUnaryException extends ParseException {
    private final Token token;

    public UnsupportedUnaryException(Token token) {
        this.token = token;
    }

    @Override
    public String getMessage() {
        return "unsupported unary operator: %s".formatted(token);
    }
}
