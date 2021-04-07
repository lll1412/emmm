package me.mathyj.object;

public record ClosureObject(CompiledFunctionObject fn,
                            Object... free) implements Object {
    @Override
    public ObjectType type() {
        return ObjectType.CLOSURE;
    }

    @Override
    public String value() {
        return "CLOSURE(%s)".formatted(fn);
    }
}
