package me.mathyj.exception;

import me.mathyj.object.ObjectType;

public class UnsupportedArgumentException extends RuntimeException {
    private final ObjectType excepted;
    private final ObjectType actual;
    private final String name;

    public UnsupportedArgumentException(ObjectType excepted, ObjectType actual, String name) {
        this.excepted = excepted;
        this.actual = actual;
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "unsupported argument type for %s, expected %s, got %s".formatted(name, excepted, actual);
    }
}
