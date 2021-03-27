package me.mathyj.ast.expression;

import me.mathyj.ast.operator.BinaryOperator;

public class BinaryExpression extends Expression{
    private final Expression left;
    private final BinaryOperator operator;
    private final Expression right;

    public BinaryExpression(Expression left, BinaryOperator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(%s %s %s)".formatted(left, operator, right);
    }
}
