package me.mathyj.exception.runtime;

import me.mathyj.compiler.Opcode;
import me.mathyj.object.Object;

public class UnsupportedBinaryOperation extends RuntimeException {
    private final Object left;
    private final Object right;
    private final Opcode op;

    public UnsupportedBinaryOperation(Object left, Object right, Opcode op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public String getMessage() {
        return "unsupported type for binary operation: %s(%s) %s %s(%s)".formatted(left.type(), left.value(), op, right.type(), right.value());
    }
}
