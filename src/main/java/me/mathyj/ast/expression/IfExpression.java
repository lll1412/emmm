package me.mathyj.ast.expression;

import me.mathyj.ast.statement.BlockStatement;

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
}
