package me.mathyj.exception;

import me.mathyj.object.ObjectType;

public class UnsupportedArgumentException extends RuntimeException {
    private ObjectType excepted;
    private final ObjectType actual;
    private final String name;

    public UnsupportedArgumentException(ObjectType excepted, ObjectType actual, String name) {
        this.excepted = excepted;
        this.actual = actual;
        this.name = name;
    }
    public UnsupportedArgumentException(ObjectType actual, String name) {
        this.actual = actual;
        this.name = name;
    }

    @Override
    public String getMessage() {
        if (excepted == null) {
            return "unsupported argument type %s for %s".formatted(actual, name);
        } else {
            return "unsupported argument type for %s, expected %s, got %s".formatted(name, excepted, actual);
        }
    }
}
