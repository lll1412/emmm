package me.mathyj.exception.code;

public class OpcodeUndefinedException extends RuntimeException {
    private final byte op;

    public OpcodeUndefinedException(byte op) {
        this.op = op;
    }

    @Override
    public String getMessage() {
        return "opcode %d undefined ".formatted(op);
    }
}
