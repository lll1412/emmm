package me.mathyj.exception.runtime;

import me.mathyj.compiler.Opcode;

public class UnsupportedOpcode extends RuntimeException {
    private final Opcode opcode;

    public UnsupportedOpcode(Opcode opcode) {
        this.opcode = opcode;
    }

    @Override
    public String getMessage() {
        return "unsupported opcode: %s".formatted(opcode);
    }
}
