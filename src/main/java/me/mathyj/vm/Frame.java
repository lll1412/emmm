package me.mathyj.vm;

import me.mathyj.code.Instructions;
import me.mathyj.object.ClosureObject;

public class Frame {
    // 函数指令
    public final ClosureObject closure;
    // 指令指针
    int ip;
    // 函数基址
    int bp;

    Frame(ClosureObject closure, int basePoint) {
        this.closure = closure;
        this.bp = basePoint;
    }

    Frame(ClosureObject closure) {
        this(closure, 0);
    }

    Instructions instructions() {
        return closure.fn.instructions;
    }

    int instructionsSize() {
        return closure.fn.instructions.size();
    }
}
