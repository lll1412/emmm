package me.mathyj.exception.runtime;

public class WrongArgumentsNumber extends RuntimeException {
    private final int expected;
    private final int actual;

    public WrongArgumentsNumber(int expected, int actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "wrong number of arguments, expected: %d, got: %d".formatted(expected, actual);
    }
}
