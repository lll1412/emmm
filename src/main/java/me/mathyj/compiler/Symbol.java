package me.mathyj.compiler;

public record Symbol(String name, Scope scope, int index) {

    enum Scope {
        GLOBAL
    }
}
