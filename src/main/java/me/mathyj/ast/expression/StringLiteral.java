package me.mathyj.ast.expression;

import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.object.StringObject;

public class StringLiteral extends Expression {
    private final String val;

    public StringLiteral(String val) {
        this.val = val;
    }

    @Override
    public Object eval(Environment env) {
        return new StringObject(val);
    }

    @Override
    public String toString() {
        return val;
    }
}
