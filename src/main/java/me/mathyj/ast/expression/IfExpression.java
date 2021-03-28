package me.mathyj.ast.expression;

import me.mathyj.ast.statement.BlockStatement;
import me.mathyj.object.Environment;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;

public class IfExpression extends Expression {
    // if (condition) {
    //    consequence
    // } else {
    //    alternative
    // }
    private final Expression condition;
    private final BlockStatement consequence;
    private final BlockStatement alternative;

    public IfExpression(Expression condition, BlockStatement consequence) {
        this(condition, consequence, null);
    }

    public IfExpression(Expression condition, BlockStatement consequence, BlockStatement alternative) {
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }

    @Override
    public String toString() {
        String formatted;
        if (alternative != null) {
            formatted = """
                    if %s {
                        %s
                    } else {
                        %s
                    }
                    """.formatted(condition, consequence, alternative);
        } else {
            formatted = """
                    if %s {
                        %s
                    }
                    """.formatted(condition, consequence);
        }
        return formatted;
    }

    @Override
    public Object eval(Environment env) {
        var cond = condition.eval(env);
        var ret = Object.NULL;
        if (isTruthy(cond)) {
            if (consequence != null) ret = consequence.eval(env);
        } else {
            if (alternative != null) ret = alternative.eval(env);
        }
        return ret;
    }

    private boolean isTruthy(Object obj) {
        return obj != null
               && !obj.equals(IntegerObject.valueOf(0))
               && !obj.equals(Object.FALSE);
    }
}
