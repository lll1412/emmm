package me.mathyj.ast.expression;

import me.mathyj.object.Environment;
import me.mathyj.object.Object;

public class Identifier extends Expression {
    public final String identifier;

    public Identifier(String value) {
        this.identifier = value;
    }

    @Override
    public String toString() {
        return identifier == null ? "" : identifier;
    }

    @Override
    public Object eval(Environment env) {
        return env.get(identifier);
    }
}
