package me.mathyj.ast.expression;

import java.util.List;
import java.util.stream.Collectors;

public class CallArguments extends Expression {
    private List<Expression> arguments;

    public CallArguments(List<Expression> arguments) {
        this.arguments = arguments;
    }

    public CallArguments() {
    }

    @Override
    public String toString() {
        if (arguments == null || arguments.isEmpty()) return "";
        var s = arguments.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(", "));
        return "%s".formatted(s);
    }
}
