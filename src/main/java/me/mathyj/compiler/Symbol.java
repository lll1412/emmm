package me.mathyj.compiler;

public record Symbol(String name, Scope scope, int index) {

    public enum Scope {
        LOCAL,
        FREE,
        GLOBAL,
        BUILTIN,
        FUNCTION,
    }
}
