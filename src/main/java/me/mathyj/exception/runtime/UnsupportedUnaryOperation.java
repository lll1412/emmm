package me.mathyj.exception.runtime;

import me.mathyj.code.Opcode;
import me.mathyj.object.Object;

public class UnsupportedUnaryOperation extends RuntimeException {
    private final Object right;
    private final Opcode opcode;

    public UnsupportedUnaryOperation(Object right, Opcode opcode) {
        this.right = right;
        this.opcode = opcode;
    }

    @Override
    public String getMessage() {
        return "unsupported unary operation: %s %s".formatted(opcode, right);
    }
}
