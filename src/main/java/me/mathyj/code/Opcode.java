package me.mathyj.code;

import me.mathyj.exception.code.OpcodeUndefinedException;

public enum Opcode {
    // 常量 操作2字节的操作数  例如：const 0xaa 0xbb
    CONSTANT((char) 2),
    // 二元运算
    ADD,
    SUB,
    MUL,
    DIV,
    // 布尔值
    TRUE,
    FALSE,
    // 出栈
    POP,

    ;
    public final char[] operandsWidth;// 指令每个操作数的宽度

    // 下面这两个虽然可以直接调用方法获取 ，但不确定要用现在的方式，可能会改
    public final char identity;// 操作码的唯一标识符, 暂时用ordinal()
//    public final String name;// 打印时显示的名字，暂时用name()

    Opcode(char... operandsWidth) {
        this.identity = (char) ordinal();
//        this.name = name();
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
