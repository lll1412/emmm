package me.mathyj.ast.expression;

import me.mathyj.object.Environment;
import me.mathyj.object.Object;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallArguments {
    public final List<Expression> arguments;

    public CallArguments(List<Expression> arguments) {
        this.arguments = arguments;
    }

    public CallArguments() {
        this.arguments = new ArrayList<>();
    }

    @Override
    public String toString() {
        if (arguments == null || arguments.isEmpty()) return "";
        var s = arguments.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(", "));
        return "%s".formatted(s);
    }

    public int size() {
        return arguments.size();
    }

    public List<Object> eval(Environment env) {
        return arguments.stream()
                .map(arg -> arg.eval(env))
                .collect(Collectors.toList());
    }
}
