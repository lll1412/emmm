package me.mathyj.exception.parse;

import me.mathyj.token.Token;

public class NoBinaryParseException extends ParseException {
    private final Token token;

    public NoBinaryParseException(Token token) {
        this.token = token;
    }

    @Override
    public String getMessage() {
        return "no binary parse function for token: %s".formatted(token);
    }
}
