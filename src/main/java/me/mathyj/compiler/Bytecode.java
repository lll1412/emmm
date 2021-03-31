package me.mathyj.compiler;

import me.mathyj.object.Object;

import java.util.Arrays;

public class Bytecode {
    public final byte[] instructions;// 生成的字节码指令
    public final Object[] constantsPool;// 常量池

    Bytecode(Object[] constantsPool, byte[] instructions) {
        this.instructions = instructions;
        this.constantsPool = constantsPool;
    }

    Bytecode(Object[] constantsPool, byte[]... instructions) {
        this.instructions = concat(instructions);
        this.constantsPool = constantsPool;
    }

    Bytecode() {
        this.instructions = new byte[]{};
        this.constantsPool = new Object[]{};
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bytecode bytecode = (Bytecode) o;

        if (!Arrays.equals(instructions, bytecode.instructions)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(constantsPool, bytecode.constantsPool);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(instructions);
        result = 31 * result + Arrays.hashCode(constantsPool);
        return result;
    }

    private byte[] concat(byte[]... instructions) {
        var len = 0;
        for (var bytes : instructions) {
            len += bytes.length;
        }
        var result = new byte[len];
        var i = 0;
        for (var bytes : instructions) {
            for (var b : bytes) {
                result[i++] = b;
            }
        }
        return result;
    }
}
