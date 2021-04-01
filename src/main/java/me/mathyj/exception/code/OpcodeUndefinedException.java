package me.mathyj.exception.code;

public class OpcodeUndefinedException extends RuntimeException {
    private final int op;

    public OpcodeUndefinedException(int op) {
        this.op = op;
    }

    @Override
    public String getMessage() {
        return "opcode %d undefined ".formatted(op);
    }
}
