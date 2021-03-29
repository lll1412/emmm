package me.mathyj.object;

public class StringObject implements Object {
    private final String val;

    public StringObject(String val) {
        this.val = val;
    }

    public static StringObject valueOf(String val) {
        return new StringObject(val);
    }

    @Override
    public ObjectType type() {
        return ObjectType.STRING;
    }

    @Override
    public String value() {
        return val;
    }

    @Override
    public String toString() {
        return value();
    }
}
