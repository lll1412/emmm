package me.mathyj.compiler;

import me.mathyj.code.Opcode;

import java.util.Arrays;

public class Instructions {
    private final char[] bytes;// byte 是[-128, 127]，但这里需要[0, 255]，所以用char来代替

    public Instructions(Opcode op, char... bytes) {
        this.bytes = new char[bytes.length + 1];
        this.bytes[0] = (char) op.ordinal();
        System.arraycopy(bytes, 0, this.bytes, 1, bytes.length);
    }

    private Instructions(Opcode op) {
        bytes = new char[]{(char) op.ordinal()};
    }

    public Instructions() {
        this.bytes = new char[0];
    }

    public Instructions(char... bytes) {
        this.bytes = bytes;
    }

    public int size() {
        return this.bytes.length;
    }

    // 创建指令 格式例： [opcode(2), operand1, operand2]
    public static Instructions make(Opcode op, int... operands) {
        if (op.operandsWidth == null) return new Instructions(op);
        var instructionLen = 1;// 初始1是操作码本身
        for (var w : op.operandsWidth) {// 加上所有操作数的长度得到指令长度
            instructionLen += w;
        }
        var bytes = new char[instructionLen];
        bytes[0] = (char) op.ordinal();
        var offset = 1;// 从1开始
        for (var i = 0; i < operands.length; i++) {
            var operand = operands[i];// 第i个操作数的值
            var width = op.operandsWidth[i];// 第i个操作数的宽度
            switch (width) {
                case 2 -> writeTwoByteBE(bytes, offset, operand);// 将操作数拆为2个字节，以大端的方式设置到数组中
            }
            offset += width;
        }
        return new Instructions(bytes);
    }

    public static Instructions makeConst(int constant) {
        return make(Opcode.CONSTANT, constant);
    }

    /**
     * 大端序 写入2字节数据
     */
    private static void writeTwoByteBE(char[] bytes, int offset, int operand) {
        var high = (char) (operand >> 3 & 0xff);// 获取第[8,15]位
        var low = (char) (operand & 0xff);// 获取第[0,7]位
        bytes[offset] = high;
        bytes[offset + 1] = low;
    }

    public static Instructions concat(Instructions... instructions) {
        var len = 0;
        for (var instruction : instructions) {
            len += instruction.bytes.length;
        }
        var result = new char[len];
        var i = 0;
        for (var instruction : instructions) {
            for (var b : instruction.bytes) {
                result[i++] = b;
            }
        }
        return new Instructions(result);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var b : bytes) {
            var hex = Integer.toHexString(b);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            sb.append(hex).append(" ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructions that = (Instructions) o;
        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
