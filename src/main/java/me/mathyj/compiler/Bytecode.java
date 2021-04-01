package me.mathyj.compiler;

import me.mathyj.object.Object;

import java.util.Arrays;
import java.util.Objects;

public class Bytecode {
    public final Instructions instructions;// 生成的字节码指令
    public final Object[] constantsPool;// 常量池

    public Bytecode(Object[] constantsPool, Instructions... instructions) {
        this.instructions = Instructions.concat(instructions);
        this.constantsPool = constantsPool;
    }

    public Bytecode() {
        this.instructions = new Instructions();
        this.constantsPool = new Object[]{};
    }

    @Override
    public String toString() {
//        var sb = new StringBuilder();
//        for (var i = 0; i < instructions.size(); i++) {
//
//        }
        return "instructions=%s, constantsPool=%s".formatted(instructions, Arrays.toString(constantsPool));
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bytecode bytecode = (Bytecode) o;
        return Objects.equals(instructions, bytecode.instructions) && Arrays.equals(constantsPool, bytecode.constantsPool);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(instructions);
        result = 31 * result + Arrays.hashCode(constantsPool);
        return result;
    }
}
