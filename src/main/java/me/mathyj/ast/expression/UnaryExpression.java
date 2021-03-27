package me.mathyj.ast.expression;

import me.mathyj.ast.operator.UnaryOperator;

public class UnaryExpression extends Expression {
    private final UnaryOperator operator;
    private final Expression right;

    public UnaryExpression(UnaryOperator operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(%s%s)".formatted(operator, right);
    }

}
