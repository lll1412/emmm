package me.mathyj.object;

public class ClosureObject implements Object {
    public final Object[] free;
    public final CompiledFunctionObject fn;

    public ClosureObject(CompiledFunctionObject fn, Object... free) {
        this.fn = fn;
        this.free = free;
    }

    @Override
    public ObjectType type() {
        return ObjectType.CLOSURE;
    }

    @Override
    public String value() {
        return "CLOSURE(%s)".formatted(fn);
    }
}
