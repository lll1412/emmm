package me.mathyj.exception;

import me.mathyj.token.Token;

public class UnsupportedBinaryOperator extends ParseException {
    private final Token token;

    public UnsupportedBinaryOperator(Token token) {
        this.token = token;
    }

    @Override
    public String getMessage() {
        return "unsupported binary operator: %s".formatted(token);
    }
}
