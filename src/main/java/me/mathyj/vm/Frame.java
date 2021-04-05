package me.mathyj.vm;

import me.mathyj.code.Instructions;
import me.mathyj.object.CompiledFunctionObject;
import me.mathyj.object.Object;

public class Frame {
    public static final int LOCAL_SIZE = 255;// todo 这里应该由编译时计算出来，暂时写死
    public final Object[] locals;
    private final CompiledFunctionObject fn;
    int ip;


    Frame(CompiledFunctionObject fn) {
        this.fn = fn;
        locals = new Object[LOCAL_SIZE];
    }

    Instructions instructions() {
        return fn.instructions;
    }

    int instructionsSize() {
        return fn.instructions.size();
    }
}
