package me.mathyj.compiler;

import me.mathyj.code.Opcode;

public class Instructions {
    public char[] bytes;// byte 是[-128, 127]，但这里需要[0, 255]，所以用char来代替

    public Instructions(Opcode op, char... bytes) {
        if (bytes == null) {
            this.bytes = new char[]{op.identity};
        } else {
            this.bytes = new char[bytes.length + 1];
            this.bytes[0] = op.identity;
            System.arraycopy(bytes, 0, this.bytes, 1, bytes.length);
        }
    }

    public Instructions() {
        this.bytes = new char[0];
    }

    public Instructions(char... bytes) {
        this.bytes = bytes;
    }
//    public void
    // 创建指令 格式例： [opcode(2), operand1, operand2]
    public static Instructions make(Opcode op, int... operands) {
        if (op.operandsWidth == null) return new Instructions(op);
        var instructionLen = 1;// 初始为1是操作码本身占一个长度
        for (var w : op.operandsWidth) {// 加上所有操作数的长度得到指令长度
            instructionLen += w;
        }
        var bytes = new char[instructionLen];
        bytes[0] = op.identity;
        var offset = 1;// 从1开始
        for (var i = 0; i < operands.length; i++) {
            var operand = operands[i];// 第i个操作数的值
            var width = op.operandsWidth[i];// 第i个操作数的宽度
            switch (width) {
                case 2 -> writeTwoByteBE(bytes, offset, operand);
            }
            offset += width;
        }
        return new Instructions(bytes);
    }

    public static Instructions makeConst(int constant) {
        return make(Opcode.CONSTANT, constant);
    }

    public static Instructions makePop() {
        return make(Opcode.POP);
    }

    /**
     * 大端序 offset位置开始 往后写入2字节数据
     */
    public static void writeTwoByteBE(char[] bytes, int offset, int operand) {
        var high = (char) (operand >> 8 & 0xff);// 获取第[8,15]位
        var low = (char) (operand & 0xff);// 获取第[0,7]位
        bytes[offset] = high;
        bytes[offset + 1] = low;
    }

    /**
     * 合并多个指令流
     */
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

    /**
     * 读取2字节数据
     */
    public static int readTwoByte(Instructions ins, int offset) {
        var high = ins.bytes[offset];
        var low = ins.bytes[offset + 1];
        return high << 8 | low;
    }

    public int size() {
        return this.bytes.length;
    }

    /**
     * 指令以16进制输出
     */
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

    /**
     * 将指令打印为字符串
     */
    public String print() {
        var sb = new StringBuilder();
        for (var offset = 0; offset < bytes.length; ) {
            var id = bytes[offset];
            var op = Opcode.lookup(id);// 操作码
            sb.append(String.format("%04d %s", offset, op));
            offset++;// 跳过操作码
            for (var width : op.operandsWidth) {// 操作数的位数, w个字节
                switch (width) {
                    case 2 -> sb.append(" ").append(Instructions.readTwoByte(this, offset));
                }
                offset += width;
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
