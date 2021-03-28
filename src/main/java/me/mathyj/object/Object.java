package me.mathyj.object;

import me.mathyj.exception.WrongArgumentsCount;

public interface Object {
    Object NULL = NullObject.NULL;
    Object TRUE = BooleanObject.TRUE;
    Object FALSE = BooleanObject.FALSE;

    ObjectType type();

    String value();
}
