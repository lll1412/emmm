package me.mathyj.exception.parse;

public class UnsupportedBinaryOperator extends ParseException {
    private final java.lang.Object token;

    public UnsupportedBinaryOperator(java.lang.Object token) {
        this.token = token;
    }

    @Override
    public String getMessage() {
        return "unsupported binary operator: %s".formatted(token);
    }
}
