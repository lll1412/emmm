package me.mathyj.vm;

import me.mathyj.code.Instructions;
import me.mathyj.object.CompiledFunctionObject;

public class Frame {
    private final CompiledFunctionObject fn;
    int ip;


    Frame(CompiledFunctionObject fn) {
        this.fn = fn;
    }

    Instructions instructions() {
        return fn.instructions;
    }

    int instructionsSize() {
        return fn.instructions.size();
    }
}
