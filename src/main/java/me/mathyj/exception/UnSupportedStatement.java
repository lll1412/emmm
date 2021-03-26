package me.mathyj.exception;

import me.mathyj.token.Token;

public class UnSupportedStatement extends RuntimeException {
    private final Token token;
    private final int lineNumber;

    public UnSupportedStatement(Token token, int lineNumber) {
        this.token = token;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage() {
        return "line: %d, unsupported statement with token: %s".formatted(lineNumber, token);
    }
}
