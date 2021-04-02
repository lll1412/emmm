package me.mathyj.exception.compile;

public class UnknownOperatorException extends CompileException {
    private final Object op;

    public UnknownOperatorException(Object op) {
        this.op = op;
    }

    @Override
    public String getMessage() {
        return "unknown operator %s".formatted(op);
    }
}
