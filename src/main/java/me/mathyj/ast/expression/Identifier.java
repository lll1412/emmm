package me.mathyj.ast.expression;

public class Identifier extends Expression {
    public final String identifier;

    public Identifier(String value) {
        this.identifier = value;
    }

    @Override
    public String toString() {
        return identifier == null ? "" : identifier;
    }

}
