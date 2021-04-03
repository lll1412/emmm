package me.mathyj.vm;

import me.mathyj.compiler.Bytecode;
import me.mathyj.compiler.Instructions;
import me.mathyj.compiler.Opcode;
import me.mathyj.exception.runtime.UnsupportedBinaryOperation;
import me.mathyj.exception.runtime.UnsupportedOpcode;
import me.mathyj.exception.runtime.UnsupportedUnaryOperation;
import me.mathyj.object.Object;
import me.mathyj.object.*;

import java.util.ArrayList;

public class Vm {
    public static final int STACK_SIZE = 2048;
    public static final int GLOBALS_SIZE = 65535;
    // 字节码
    private final Bytecode bytecode;
    // 虚拟机栈
    private final Object[] stack;
    // 全局对象
    private final Object[] globals;
    // 栈指针
    private int sp;// stack pointer

    public Vm(Bytecode bytecode, Object[] globals) {
        this.bytecode = bytecode;
        this.stack = new Object[STACK_SIZE];
        this.sp = 0;
        this.globals = globals;
    }

    public Vm(Bytecode bytecode) {
        this(bytecode, new Object[GLOBALS_SIZE]);
    }

    public void run() {
        var instructions = bytecode.instructions;
        var constantsPool = bytecode.constantsPool;
        var ip = 0;// instruction pointer
        while (ip < instructions.size()) {
            // 取指令
            var opcode = fetchOpcode(ip);
            ip++;
            // 读操作数
            var operands = readOperands(opcode, instructions, ip);
            // 执行
            switch (opcode) {
                case CONSTANT -> {
                    var constIndex = operands.first();
                    var constObject = constantsPool.get(constIndex);
                    pushStack(constObject);
                    ip += operands.offset();
                }
                case ADD, SUB, MUL, DIV, EQ, NE, GT, LT -> executeBinaryOperation(opcode);
                case TRUE -> pushStack(Object.TRUE);
                case FALSE -> pushStack(Object.FALSE);
                case NOT, NEG -> executeUnaryOperation(opcode);
                case POP -> popStack();
                case NULL -> pushStack(Object.NULL);
                case JUMP_IF_NOT_TRUTHY -> {
                    var cond = popStack();
                    if (!isTruthy(cond)) {
                        // 跳转
                        ip = operands.first();
                    } else {
                        // 正常执行
                        ip += operands.offset();
                    }
                }
                case JUMP_ALWAYS -> ip = operands.first();
                case SET_GLOBAL -> {
                    var globalIndex = operands.first();
                    globals[globalIndex] = popStack();
                    ip += operands.offset();
                }
                case GET_GLOBAL -> {
                    var globalIndex = operands.first();
                    pushStack(globals[globalIndex]);
                    ip += operands.offset();
                }
                case ARRAY -> {
                    var arrayLength = operands.first();
                    var array = buildArray(sp - arrayLength, sp);
                    sp -= arrayLength;
                    pushStack(array);
                    ip += operands.offset();
                }
                default -> throw new UnsupportedOpcode(opcode);
            }
        }
    }

    private ArrayObject buildArray(int start, int end) {
        var list = new ArrayList<Object>(end - start);
        for (int i = start; i < end; i++) {
            list.add(getFromStack(i));
        }
        return new ArrayObject(list);
    }

    private void executeUnaryOperation(Opcode opcode) {
        var right = popStack();
        var val = switch (opcode) {
            case NOT -> executeUnaryNotOperation(right);
            case NEG -> executeUnaryNegOperation(right);
            default -> throw new UnsupportedUnaryOperation(right, opcode);
        };
        pushStack(val);
    }

    private Object executeUnaryNegOperation(Object right) {
        if (right instanceof IntegerObject) {
            var value = ((IntegerObject) right).value;
            return IntegerObject.valueOf(-value);
        } else {
            throw new UnsupportedUnaryOperation(right, Opcode.NEG);
        }
    }

    private Object executeUnaryNotOperation(Object right) {
        if (right.equals(Object.TRUE)) {
            return Object.FALSE;
        } else if (right.equals(Object.FALSE)) {
            return Object.TRUE;
        } else if (right.equals(Object.NULL)) {
            return Object.TRUE;
        } else {
            return Object.FALSE;
        }
    }

    /**
     * 二元算术运算
     */
    private void executeBinaryOperation(Opcode opcode) {
        var right = popStack();
        var left = popStack();
        Object val;
        if (left instanceof IntegerObject && right instanceof IntegerObject) {
            val = executeBinaryIntegerOperation((IntegerObject) left, (IntegerObject) right, opcode);
        } else if (left instanceof BooleanObject && right instanceof BooleanObject) {
            val = executeBinaryBooleanOperation(((BooleanObject) left), ((BooleanObject) right), opcode);
        } else if (left instanceof StringObject && opcode.equals(Opcode.ADD)) {
            val = StringObject.valueOf(left.value() + right.toString());
        } else {
            throw new UnsupportedBinaryOperation(left, right, opcode);
        }
        pushStack(val);
    }

    /**
     * 布尔值二元运算
     */
    private Object executeBinaryBooleanOperation(BooleanObject left, BooleanObject right, Opcode opcode) {
        var leftVal = left.value;
        var rightVal = right.value;
        return switch (opcode) {
            case EQ -> BooleanObject.valueOf(leftVal == rightVal);
            case NE -> BooleanObject.valueOf(leftVal != rightVal);
            default -> throw new UnsupportedBinaryOperation(left, right, opcode);
        };
    }

    /**
     * 整数二元运算
     */
    private Object executeBinaryIntegerOperation(IntegerObject left, IntegerObject right, Opcode opcode) {
        var leftVal = left.value;
        var rightVal = right.value;
        return switch (opcode) {
            case ADD -> IntegerObject.valueOf(leftVal + rightVal);
            case SUB -> IntegerObject.valueOf(leftVal - rightVal);
            case MUL -> IntegerObject.valueOf(leftVal * rightVal);
            case DIV -> IntegerObject.valueOf(leftVal / rightVal);
            case EQ -> BooleanObject.valueOf(leftVal == rightVal);
            case NE -> BooleanObject.valueOf(leftVal != rightVal);
            case GT -> BooleanObject.valueOf(leftVal > rightVal);
            case LT -> BooleanObject.valueOf(leftVal < rightVal);
            default -> throw new UnsupportedBinaryOperation(left, right, opcode);
        };
    }

    /**
     * 取指令
     */
    private Opcode fetchOpcode(int ip) {
        var c = bytecode.instructions.bytes[ip];
        var opcode = Opcode.lookup(c);
        return opcode;
    }

    /**
     * 从指令中读取操作数
     */
    private Operands readOperands(Opcode op, Instructions ins, int start) {
        var offset = 0;
        var operandsWidth = op.operandsWidth;
        var operands = new int[operandsWidth.length];
        for (int i = 0; i < operandsWidth.length; i++) {
            int w = operandsWidth[i];
            switch (w) {
                case 2 -> operands[i] = Instructions.readTwoByte(ins, start);
            }
            offset += w;
        }
        return new Operands(offset, operands);
    }

    /**
     * 最后一次出栈的元素
     */
    public Object lastPopped() {
        return stack[sp];
    }

    /**
     * 栈相关操作
     */
    private void pushStack(Object obj) {
        stack[sp++] = obj;
    }

    private Object peekStack() {
        return stack[sp - 1];
    }

    private void putStack(int index, Object el) {
        stack[index] = el;
    }

    private Object popStack() {
        return stack[--sp];
    }

    private Object getFromStack(int index) {
        return stack[index];
    }

    private boolean isTruthy(Object obj) {
        return obj != null
               && obj != Object.NULL
               && !obj.equals(IntegerObject.valueOf(0))
               && !obj.equals(Object.FALSE);
    }

    /**
     * 操作数的一个封装，offset 是操作数占用的字节数, operands是操作数数组
     */
    private record Operands(int offset, int... operands) {
        int first() {
            return operands[0];
        }
    }
}
