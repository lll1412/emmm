package me.mathyj.ast.expression;

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
}
