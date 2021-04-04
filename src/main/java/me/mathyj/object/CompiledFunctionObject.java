package me.mathyj.object;

import me.mathyj.code.Instructions;

public class CompiledFunctionObject implements Object {
    public final Instructions instructions;

    public CompiledFunctionObject(Instructions... instructions) {
        if (instructions.length == 1) this.instructions = instructions[0];
        else this.instructions = Instructions.concat(instructions);
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
        return "FUNCTION(%s)".formatted(value());
    }
}
