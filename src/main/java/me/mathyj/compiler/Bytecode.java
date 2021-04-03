package me.mathyj.compiler;

import me.mathyj.code.Opcode;
import me.mathyj.object.Object;

import java.util.ArrayList;
import java.util.List;

public class Bytecode {
    public List<Object> constantsPool;// 常量池
    public Instructions instructions;// 生成的字节码指令

    public Bytecode(List<Object> constantsPool, Instructions... instructions) {
        this.constantsPool = constantsPool;
        this.instructions = Instructions.concat(instructions);
    }

    public Bytecode() {
        this.constantsPool = new ArrayList<>();
        this.instructions = new Instructions();
    }

    /**
     * 添加常量，并返回在常量池中的索引
     */
    private int addConst(Object constant) {
        constantsPool.add(constant);
        return constantsPool.size() - 1;
    }

    /**
     * 添加指令，并返回添加的位置
     */
    private int addInstructions(Instructions ins) {
        // 添加新指令时的长度，也就是当前指令在指令列表中的位置
        var posNewInstruction = instructions.size();
        instructions = Instructions.concat(instructions, ins);
        return posNewInstruction;
    }

    // 生成指令
    public int emit(Opcode op, int... operands) {
        var ins = Instructions.make(op, operands);
        var pos = this.addInstructions(ins);
        return pos;
    }

    // 生成常量指令
    public int emitConst(Object obj) {
        var index = addConst(obj);
        var ins = Instructions.make(Opcode.CONSTANT, index);// 添加到常量池，返回索引
        var pos = this.addInstructions(ins);
        return pos;
    }

    @Override
    public String toString() {
        return "constantsPool: %s, \ninstructions:\n%s".formatted(constantsPool, instructions.print());
    }
}
