package me.mathyj.ast.expression;

import me.mathyj.object.Environment;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;

public class IntegerLiteral extends Expression {
    private final int value;

    public IntegerLiteral(String value) {
        this(Integer.parseInt(value));
    }

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Object eval(Environment env) {
        return IntegerObject.valueOf(value);
    }
}
