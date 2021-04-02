package me.mathyj.vm;

import me.mathyj.code.Opcode;
import me.mathyj.compiler.Bytecode;
import me.mathyj.compiler.Instructions;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;

public class Vm {
    private static final int STACK_SIZE = 2048;
    private final Bytecode bytecode;
    private final Object[] stack;
    private int sp;// stack pointer

    public Vm(Bytecode bytecode) {
        this.bytecode = bytecode;
        this.stack = new Object[STACK_SIZE];
        sp = 0;
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
                    var constIndex = operands.operands()[0];
                    var constObject = constantsPool.get(constIndex);
                    pushStack(constObject);
                    ip += operands.offset();
                }
                case ADD -> {
                    var obj2 = popStack();
                    var obj1 = popStack();
                    if (obj1 instanceof IntegerObject && obj2 instanceof IntegerObject) {
                        var val1 = ((IntegerObject) obj1).value;
                        var val2 = ((IntegerObject) obj2).value;
                        var r = IntegerObject.valueOf(val1 + val2);
                        pushStack(r);
                    } else {
                        // Exception
                    }
                }
            }
        }
    }

    /**
     * 取指令
     */
    private Opcode fetchOpcode(int ip) {
        var c = bytecode.instructions.bytes[ip];
        var opcode = Opcode.lookup(c);
        return opcode;
    }

    // 从指令中读取操作数
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
     * 栈相关操作
     */
    public Object stackPop() {
        return peekStack();
    }

    private void pushStack(Object obj) {
        stack[sp++] = obj;
    }

    private Object peekStack() {
        return stack[sp - 1];
    }

    private Object popStack() {
        return stack[--sp];
    }

    private Object getFromStack(int index) {
        return stack[index];
    }

    /**
     * 操作数的一个封装，offset 是操作数占用的字节数, operands是操作数数组
     */
    private record Operands(int offset, int... operands) {
    }
}
