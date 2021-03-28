package me.mathyj.exception.parse;

import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

public class UnexpectedTokenException extends ParseException {
    private final TokenType expected;
    private final Token actual;

    public UnexpectedTokenException(TokenType expected, Token actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "unexpected token, expected %s, got %s".formatted(expected, actual);
    }
}
