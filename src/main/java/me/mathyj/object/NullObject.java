package me.mathyj.object;

public class NullObject implements Object {
    public static final NullObject NULL = new NullObject();

    private NullObject() {
    }

    @Override

    public ObjectType type() {
        return ObjectType.NULL;
    }

    @Override
    public String value() {
        return "null";
    }

    @Override
    public String toString() {
        return value();
    }
}
