package me.mathyj.exception.runtime;

public class UnimplementedException extends RuntimeException {
    private final String name;

    public UnimplementedException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "class %s unimplemented!".formatted(name);
    }
}
