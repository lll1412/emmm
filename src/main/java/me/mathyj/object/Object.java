package me.mathyj.object;

public interface Object {
    Object NULL = NullObject.NULL;
    Object TRUE = BooleanObject.TRUE;
    Object FALSE = BooleanObject.FALSE;

    ObjectType type();

    String value();
}
