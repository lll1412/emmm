package me.mathyj.vm;

import me.mathyj.code.Bytecode;
import me.mathyj.code.Instructions;
import me.mathyj.code.Opcode;
import me.mathyj.exception.runtime.UnsupportedBinaryOperation;
import me.mathyj.exception.runtime.UnsupportedIndexOpcode;
import me.mathyj.exception.runtime.UnsupportedOpcode;
import me.mathyj.exception.runtime.UnsupportedUnaryOperation;
import me.mathyj.object.Object;
import me.mathyj.object.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Vm {
    public static final int MAX_FRAMES = 1024;
    public static final int GLOBALS_SIZE = 65535;
    // 常量池
    private final List<Object> constantsPool;

    // 全局对象
    private final Object[] globals;
    private final Frame[] frames;

    private int frameIndex;

    public Vm(Bytecode bytecode, Object[] globals) {
        var mainFn = new CompiledFunctionObject(bytecode.currentInstructions());
        var mainFrame = new Frame(mainFn);

        this.frames = new Frame[MAX_FRAMES];
        frames[0] = mainFrame;
        frameIndex = 1;

        this.constantsPool = bytecode.constantsPool;

        this.globals = globals;
    }

    public Vm(Bytecode bytecode) {
        this(bytecode, new Object[GLOBALS_SIZE]);
    }

    public void run() {
//        var ip = 0;// instruction pointer
        while (currentFrame().ip < currentFrame().instructionsSize()) {
            // 取指令
            var opcode = fetchOpcode();
            currentFrame().ip++;
            // 读操作数
            var operands = readOperands(opcode);
            // 执行
            switch (opcode) {
                case CONSTANT -> {
                    var constIndex = operands.first();
                    var constObject = constantsPool.get(constIndex);
                    currentFrame().pushStack(constObject);
                    currentFrame().ip += operands.offset();
                }
                case ADD, SUB, MUL, DIV, EQ, NE, GT, LT -> executeBinaryOperation(opcode);
                case TRUE -> currentFrame().pushStack(Object.TRUE);
                case FALSE -> currentFrame().pushStack(Object.FALSE);
                case NOT, NEG -> executeUnaryOperation(opcode);
                case POP -> currentFrame().popStack();
                case NULL -> currentFrame().pushStack(Object.NULL);
                case JUMP_IF_NOT_TRUTHY -> {
                    var cond = currentFrame().popStack();
                    if (!isTruthy(cond)) {
                        // 跳转
                        currentFrame().ip = operands.first();
                    } else {
                        // 正常执行
                        currentFrame().ip += operands.offset();
                    }
                }
                case JUMP_ALWAYS -> currentFrame().ip = operands.first();
                case SET_GLOBAL -> {
                    var globalIndex = operands.first();
                    globals[globalIndex] = currentFrame().popStack();
                    currentFrame().ip += operands.offset();
                }
                case GET_GLOBAL -> {
                    var globalIndex = operands.first();
                    currentFrame().pushStack(globals[globalIndex]);
                    currentFrame().ip += operands.offset();
                }
                case ARRAY -> {
                    var arrayLength = operands.first();
                    var array = buildArray(currentFrame().sp - arrayLength, currentFrame().sp);
                    currentFrame().sp -= arrayLength;
                    currentFrame().pushStack(array);
                    currentFrame().ip += operands.offset();
                }
                case HASH -> {
                    var hashLength = operands.first();
                    var hash = buildHash(currentFrame().sp - hashLength * 2, currentFrame().sp);
                    currentFrame().sp -= hashLength * 2;
                    currentFrame().pushStack(hash);
                    currentFrame().ip += operands.offset();
                }
                case INDEX -> {
                    executeIndexOperation();
                }
                case CALL -> {
                    var fn = currentFrame().popStack();
                    var fnFrame = new Frame(((CompiledFunctionObject) fn));
                    currentFrame().pushStack(fn);// 当前调用函数入栈
                    pushFrame(fnFrame);
                }
                case RETURN_VALUE -> {
                    var retValue = currentFrame().popStack();//弹出函数返回值
                    popFrame();// 从函数中退出到调用点
                    currentFrame().popStack();// 当前函数出栈
                    currentFrame().pushStack(retValue);
                }
                case RETURN -> {
                    popFrame();// 从函数中退出到调用点
                    currentFrame().popStack();// 当前函数出栈
                    currentFrame().pushStack(Object.NULL);
                }
                default -> throw new UnsupportedOpcode(opcode);
            }
        }
    }

    private Frame currentFrame() {
        return frames[frameIndex - 1];
    }

    private void pushFrame(Frame frame) {
        frames[frameIndex++] = frame;
    }

    private Frame popFrame() {
        var frame = frames[--frameIndex];
        frames[frameIndex] = null;// gc
        return frame;
    }


    /**
     * 取指令
     */
    private Opcode fetchOpcode() {
        var ip = currentFrame().ip;
        var c = currentInstructions().bytes[ip];
        var opcode = Opcode.lookup(c);
        return opcode;
    }

    /**
     * 从指令中读取操作数
     */
    private Operands readOperands(Opcode op) {
        var start = currentFrame().ip;
        var offset = 0;
        var operandsWidth = op.operandsWidth;
        var operands = new int[operandsWidth.length];
        for (int i = 0; i < operandsWidth.length; i++) {
            int w = operandsWidth[i];
            switch (w) {
                case 2 -> operands[i] = Instructions.readTwoByte(currentInstructions(), start);
            }
            offset += w;
        }
        return new Operands(offset, operands);
    }

    private Instructions currentInstructions() {
        return currentFrame().instructions();
    }

    private void executeIndexOperation() {
        var indexObj = currentFrame().popStack();
        var object = currentFrame().popStack();
        if (object instanceof ArrayObject && indexObj instanceof IntegerObject) {
            var arrayObject = (ArrayObject) object;
            var index = (IntegerObject) indexObj;
            currentFrame().pushStack(arrayObject.get(index.value));
        } else if (object instanceof HashObject) {
            var hashObject = (HashObject) object;
            currentFrame().pushStack(hashObject.get(indexObj));
        } else {
            throw new UnsupportedIndexOpcode(object, indexObj);
        }
    }

    private HashObject buildHash(int start, int end) {
        var hash = new LinkedHashMap<Object, Object>();
        for (int i = start; i < end; i += 2) {
            hash.put(currentFrame().getFromStack(i), currentFrame().getFromStack(i + 1));
        }
        return new HashObject(hash);
    }

    private ArrayObject buildArray(int start, int end) {
        var list = new ArrayList<Object>(end - start);
        for (int i = start; i < end; i++) {
            list.add(currentFrame().getFromStack(i));
        }
        return new ArrayObject(list);
    }

    private void executeUnaryOperation(Opcode opcode) {
        var right = currentFrame().popStack();
        var val = switch (opcode) {
            case NOT -> executeUnaryNotOperation(right);
            case NEG -> executeUnaryNegOperation(right);
            default -> throw new UnsupportedUnaryOperation(right, opcode);
        };
        currentFrame().pushStack(val);
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
        var right = currentFrame().popStack();
        var left = currentFrame().popStack();
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
        currentFrame().pushStack(val);
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

    private boolean isTruthy(Object obj) {
        return obj != null
               && obj != Object.NULL
               && !obj.equals(IntegerObject.valueOf(0))
               && !obj.equals(Object.FALSE);
    }

    public Object lastPopped() {
        return currentFrame().lastPopped();
    }

    /**
     * 操作数的一个封装，offset 是操作数占用的字节数, operands是操作数数组
     */
    private record Operands(int offset, int... operands) {
        public int first() {
            return operands[0];
        }
    }
}
