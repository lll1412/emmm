package me.mathyj.exception;

import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

public class UnexpectedToken extends RuntimeException {
    private final TokenType expected;
    private final Token actual;
    private final int lineNumber;

    public UnexpectedToken(TokenType expected, Token actual, int lineNumber) {
        this.expected = expected;
        this.actual = actual;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage() {
        return "line %d, expected %s, got %s".formatted(lineNumber, expected, actual);
    }
}
