package me.mathyj.vm;

import me.mathyj.code.Instructions;
import me.mathyj.object.CompiledFunctionObject;

public class Frame {
    // 函数指令
    private final CompiledFunctionObject fn;
    // 指令指针
    int ip;
    // 函数基址
    int bp;

    Frame(CompiledFunctionObject fn, int basePoint) {
        this.fn = fn;
        this.bp = basePoint;
    }

    Frame(CompiledFunctionObject fn) {
        this(fn, 0);
    }

    Instructions instructions() {
        return fn.instructions;
    }

    int instructionsSize() {
        return fn.instructions.size();
    }
}
