package me.mathyj.object;

import me.mathyj.code.Bytecode;
import me.mathyj.code.Instructions;

public class CompiledFunctionObject implements Object {
    public final Instructions instructions;
    public final int numLocals;

    public CompiledFunctionObject(int numLocals, Instructions... instructions) {
        if (instructions.length == 1) this.instructions = instructions[0];
        else this.instructions = Instructions.concat(instructions);
        this.numLocals = numLocals;
    }

    public CompiledFunctionObject(Bytecode bytecode) {
        this(bytecode.symbolTable.numDefinitions(), bytecode.currentInstructions());
    }

    public CompiledFunctionObject(Instructions... instructions) {
        this(0, instructions);
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
        return "FUNCTION(%s)".formatted(instructions.print());
    }
}
