package me.mathyj.vm;

import me.mathyj.code.Instructions;
import me.mathyj.object.CompiledFunctionObject;
import me.mathyj.object.Object;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    private final CompiledFunctionObject fn;
    // 操作数栈
    private final List<Object> stack;
    int ip;
    // 栈指针
    int sp;// stack pointer

    Frame(CompiledFunctionObject fn) {
        this.stack = new ArrayList<>();
        this.fn = fn;
    }

    Instructions instructions() {
        return fn.instructions;
    }

    int instructionsSize() {
        return fn.instructions.size();
    }


    /**
     * 最后一次出栈的元素
     */
    public Object lastPopped() {
        return stack.get(sp);
    }

    /**
     * 栈相关操作
     */
    void pushStack(Object obj) {
        if (sp < stack.size()) {
            stack.set(sp, obj);
        } else {
            stack.add(obj);
        }
        sp++;
    }

    Object popStack() {
        return stack.get(--sp);
    }

    Object getFromStack(int index) {
        return stack.get(index);
    }
}
