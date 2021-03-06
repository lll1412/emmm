package me.mathyj.code;

import me.mathyj.exception.code.OpcodeUndefinedException;
import me.mathyj.exception.compile.UnknownOperatorException;
import me.mathyj.parser.ast.expression.BooleanLiteral;
import me.mathyj.parser.ast.operator.BinaryOperator;
import me.mathyj.parser.ast.operator.UnaryOperator;

public enum Opcode {
    // 空值
    NULL,
    // 常量 操作2字节的操作数  例如：const 0xaa 0xbb
    CONSTANT(2),

    /* 二元运算 */

    // +
    ADD,
    // -
    SUB,
    // *
    MUL,
    // /
    DIV,
    /* 一元运算 */
    // !x
    NOT,
    // -x
    NEG,
    /* 布尔值 */

    // true
    TRUE,
    // false
    FALSE,

    /* 布尔运算 */

    // ==
    EQ,
    // !=
    NE,
    // >
    GT,
    // <
    LT,

    AND,
    OR,

    /* 跳转指令 */
    // 条件跳转
    JUMP_IF_NOT_TRUTHY(2),
    // 无条件跳转
    JUMP_ALWAYS(2),


    // 函数调用(参数个数，最多2^8个)
    CALL(1),
    // 无返回值
    RETURN,
    // 返回值
    RETURN_VALUE,


    // 数组
    ARRAY(2),
    // 散列表
    HASH(2),
    // 索引
    INDEX,
    // 全局变量
    GET_GLOBAL(2),// 最多容纳2字节（2^16个全局变量）,
    SET_GLOBAL(2),

    // 局部变量
    GET_LOCAL(1),// 最多容纳1字节（2^8个局部变量）
    SET_LOCAL(1),

    // 内置函数
    GET_BUILTIN(1),

    // 闭包
    CLOSURE(2, 1),// 函数在常量池中的索引 和 自由变量个数
    CURRENT_CLOSURE,
    // 自由变量
    GET_FREE(1),

    // 出栈
    POP,
    ;
    public final int[] operandsWidth;// 指令每个操作数的宽度

    // 下面这两个虽然可以直接调用方法获取 ，但不确定要用现在的方式，可能会改
    public final char identity;// 操作码的唯一标识符, 暂时用ordinal()
//    public final String name;// 打印时显示的名字，暂时用name()

    Opcode(int... operandsWidth) {
        this.identity = (char) ordinal();
//        this.name = name();
        this.operandsWidth = operandsWidth == null ? new int[0] : operandsWidth;
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

    public static Opcode from(BinaryOperator binOp) {
        return switch (binOp) {
            case ADD -> Opcode.ADD;
            case SUBTRACT -> Opcode.SUB;
            case MULTIPLY -> Opcode.MUL;
            case DIVIDE -> Opcode.DIV;
            case EQUALS -> Opcode.EQ;
            case NOT_EQUALS -> Opcode.NE;
            case GREATER_THEN -> Opcode.GT;
            case LESS_THEN -> Opcode.LT;
            case OR -> Opcode.OR;
            default -> throw new UnknownOperatorException(binOp);
        };
    }

    public static Opcode from(BooleanLiteral booleanLiteral) {
        return booleanLiteral.equals(BooleanLiteral.TRUE) ? Opcode.TRUE : Opcode.FALSE;
    }

    public static Opcode from(UnaryOperator unOp) {
        return switch (unOp) {
            case NOT -> Opcode.NOT;
            case NEG -> Opcode.NEG;
        };
    }
}
