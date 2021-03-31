package me.mathyj.code;

import me.mathyj.exception.code.OpcodeUndefinedException;

import java.util.EnumSet;

public enum Opcode {
    CONSTANT((byte) 2);// 常量 操作2字节的操作数  例如：const 0xaa 0xbb

    byte[] operandsWidth;

    Opcode(byte... operandsWidth) {
        this.operandsWidth = operandsWidth;
    }

    public static Opcode lookup(byte op) {
        for (var opcode : EnumSet.allOf(Opcode.class)) {
            if (opcode.ordinal() == op) return opcode;
        }
        throw new OpcodeUndefinedException(op);
    }

    // 创建指令 格式例： [opcode(2), operand1, operand2]
    public static byte[] make(Opcode op, int... operands) {
        if (op.operandsWidth == null) return new byte[]{(byte) op.ordinal()};
        var instructionLen = 1;// 初始1是操作码本身
        for (var w : op.operandsWidth) {// 加上所有操作数的长度得到指令长度
            instructionLen += w;
        }
        var instructions = new byte[instructionLen];
        instructions[0] = (byte) op.ordinal();
        var offset = 1;// 从1开始
        for (var i = 0; i < operands.length; i++) {
            var operand = operands[i];// 第i个操作数的值
            var width = op.operandsWidth[i];// 第i个操作数的宽度
            switch (width) {
                case 2 -> writeTwoByteBE(instructions, offset, operand);// 将操作数拆为2个字节，以大端的方式设置到数组中
            }
            offset += width;
        }
        return instructions;
    }

    /**
     * 大端序 写入2字节数据
     */
    public static void writeTwoByteBE(byte[] instructions, int offset, int operand) {
        byte high = (byte) (operand >> 3 & 0xff);// 获取第[8,15]位
        byte low = (byte) (operand & 0xff);// 获取第[0,7]位
        instructions[offset] = high;
        instructions[offset + 1] = low;
    }
}
