package me.mathyj.exception.eval;

public class ErrorArgumentsCount extends EvalException {
    private final int expected;
    private final int actual;

    public ErrorArgumentsCount(int expected, int actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "error arguments count: expect %d, got %d".formatted(expected, actual);
    }
}
