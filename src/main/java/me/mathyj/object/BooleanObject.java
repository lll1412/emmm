package me.mathyj.object;

public class BooleanObject implements Object {
    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);
    public final boolean value;
    public final String valueStr;

    private BooleanObject(boolean value) {
        this.value = value;
        this.valueStr = String.valueOf(value);
    }

    public static BooleanObject valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN;
    }

    @Override
    public String value() {
        return valueStr;
    }
}
