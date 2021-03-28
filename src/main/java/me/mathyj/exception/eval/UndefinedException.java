package me.mathyj.exception.eval;

public class UndefinedException extends EvalException {
    private final String name;

    public UndefinedException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "undefined: %s".formatted(name);
    }
}
