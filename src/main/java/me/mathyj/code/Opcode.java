package me.mathyj.code;

import me.mathyj.exception.code.OpcodeUndefinedException;

public enum Opcode {
    CONSTANT((char) 2),// 常量 操作2字节的操作数  例如：const 0xaa 0xbb
    ADD,

    ;
    public final char[] operandsWidth;// 操作数宽度
    public final char identity;// 操作码的唯一标识符, 暂时用ordinal

    Opcode(char... operandsWidth) {
        this.identity = (char) ordinal();
        this.operandsWidth = operandsWidth == null ? new char[0] : operandsWidth;
    }

    /**
     * 根据指令唯一数字标识获取枚举对象
     */
    public static Opcode lookup(int id) {
        for (var opcode : Opcode.values()) {
            if (opcode.identity == id) {
                return opcode;
            }
        }
        throw new OpcodeUndefinedException(id);
    }
}
