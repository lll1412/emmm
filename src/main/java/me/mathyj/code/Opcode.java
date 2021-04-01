package me.mathyj.code;

import me.mathyj.compiler.Instructions;
import me.mathyj.exception.code.OpcodeUndefinedException;

import java.util.EnumSet;

public enum Opcode {
    CONSTANT((byte) 2);// 常量 操作2字节的操作数  例如：const 0xaa 0xbb

    public byte[] operandsWidth;

    Opcode(byte... operandsWidth) {
        this.operandsWidth = operandsWidth;
    }

    public static Opcode lookup(byte op) {
        for (var opcode : EnumSet.allOf(Opcode.class)) {
            if (opcode.ordinal() == op) return opcode;
        }
        throw new OpcodeUndefinedException(op);
    }
}
