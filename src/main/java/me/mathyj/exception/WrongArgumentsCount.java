package me.mathyj.exception;

import me.mathyj.exception.eval.EvalException;

public class WrongArgumentsCount extends EvalException {
    private final int excepted;
    private final int actual;
    private final String name;

    public WrongArgumentsCount(int excepted, int actual, String name) {
        this.excepted = excepted;
        this.actual = actual;
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "wrong arguments count for: %s, excepted: %d, got %d".formatted(name, excepted, actual);
    }
}
