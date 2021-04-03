package me.mathyj.exception.parse;

import me.mathyj.parser.token.Token;

public class UnSupportedStatementException extends ParseException {
    private final Token token;
    private final int lineNumber;

    public UnSupportedStatementException(Token token, int lineNumber) {
        this.token = token;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage() {
        return "line: %d, unsupported statement with token: %s".formatted(lineNumber, token);
    }
}
