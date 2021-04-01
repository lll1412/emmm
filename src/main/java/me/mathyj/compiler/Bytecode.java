package me.mathyj.compiler;

import me.mathyj.object.Object;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public int addConst(Object constant) {
        constantsPool.add(constant);
        return constantsPool.size() - 1;
    }

    /**
     * 添加指令，并返回添加的位置
     */
    public int addInstructions(Instructions ins) {
        // 添加新指令时的长度，也就是当前指令在指令列表中的位置
        var posNewInstruction = instructions.size();
        instructions = Instructions.concat(instructions, ins);
        return posNewInstruction;
    }

    @Override
    public String toString() {
        return "constantsPool: %s, \ninstructions:\n%s".formatted(constantsPool, instructions);
    }
}
