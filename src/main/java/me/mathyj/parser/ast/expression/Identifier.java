package me.mathyj.parser.ast.expression;

import me.mathyj.object.Environment;
import me.mathyj.object.Object;

public class Identifier extends Expression {
    public final String value;

    public Identifier(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value == null ? "" : value;
    }

    @Override
    public Object eval(Environment env) {
        return env.get(value);
    }
}
