package me.mathyj.code;

import me.mathyj.exception.code.OpcodeUndefinedException;

import java.util.EnumSet;

public enum Opcode {
    CONSTANT((char) 2);// 常量 操作2字节的操作数  例如：const 0xaa 0xbb

    public char[] operandsWidth;// 操作数宽度
    public char identity;// 操作码的唯一标识符, 暂时用ordinal

    Opcode(char... operandsWidth) {
        this.identity = (char) ordinal();
        this.operandsWidth = operandsWidth == null ? new char[0] : operandsWidth;
    }

    public static Opcode lookup(int op) {
        for (var opcode : EnumSet.allOf(Opcode.class)) {
            if (opcode.ordinal() == op) return opcode;
        }
        throw new OpcodeUndefinedException(op);
    }
}
