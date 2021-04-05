package me.mathyj.object;

import me.mathyj.code.Bytecode;
import me.mathyj.code.Instructions;

public class CompiledFunctionObject implements Object {
    public final Instructions instructions;
    public final int numLocals;// 局部变量个数
    public final int numParams;// 形参个数

    public CompiledFunctionObject(int numLocals, int numParams, Instructions... instructions) {
        if (instructions.length == 1) this.instructions = instructions[0];
        else this.instructions = Instructions.concat(instructions);
        this.numLocals = numLocals;
        this.numParams = numParams;
    }

    public CompiledFunctionObject(Bytecode bytecode) {
        this(bytecode.symbolTable.numDefinitions(), 0, bytecode.currentInstructions());
    }

    public CompiledFunctionObject(Instructions... instructions) {
        this(0, 0, instructions);
    }

    @Override
    public ObjectType type() {
        return ObjectType.COMPILED_FUNCTION;
    }

    @Override
    public String value() {
        return instructions.toString();
    }

    @Override
    public String toString() {
        return "FUNCTION(\n%s)".formatted(instructions.print());
    }
}
