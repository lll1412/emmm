package me.mathyj.ast.statement;

import me.mathyj.ast.expression.Expression;

public class ExpressionStatement extends Statement {
    public final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "%s".formatted(expression);
    }
}
