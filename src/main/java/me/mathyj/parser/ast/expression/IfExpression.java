package me.mathyj.parser.ast.expression;

import me.mathyj.parser.ast.statement.BlockStatement;
import me.mathyj.object.Environment;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;

public class IfExpression extends Expression {
    // if (condition) {
    //    consequence
    // } else {
    //    alternative
    // }
    public final Expression condition;
    public final BlockStatement consequence;
    public final BlockStatement alternative;

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
               && obj != Object.NULL
               && !obj.equals(IntegerObject.valueOf(0))
               && !obj.equals(Object.FALSE);
    }
}
