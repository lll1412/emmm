package me.mathyj.exception.runtime;

public class UndefinedVariable extends RuntimeException {
    private final String value;

    public UndefinedVariable(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "undefined variable %s".formatted(value);
    }
}
