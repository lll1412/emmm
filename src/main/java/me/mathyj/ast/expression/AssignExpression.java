package me.mathyj.ast.expression;

import me.mathyj.exception.eval.UndefinedException;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;

public class AssignExpression extends Expression {
    private final Identifier left;
    private final Expression right;

    public AssignExpression(Identifier left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Object eval(Environment env) {
        var leftObj = env.get(left.identifier);
        if (leftObj != null) {
            return env.set(left.identifier, right.eval(env));
        } else {
            throw new UndefinedException(left.identifier);
        }
    }

    @Override
    public String toString() {
        return "%s = %s".formatted(ifNull(left), ifNull(right));
    }
}
