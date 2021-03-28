package me.mathyj.ast.statement;

import me.mathyj.ast.expression.Expression;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;

public class ExpressionStatement extends Statement {
    public final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "%s".formatted(expression);
    }

    @Override
    public Object eval(Environment env) {
        return expression.eval(env);
    }
}
