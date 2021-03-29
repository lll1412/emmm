package me.mathyj.ast;

import me.mathyj.ast.expression.Expression;
import me.mathyj.exception.UnimplementedException;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Node {

    default Object eval(Environment env) {
        throw new UnimplementedException(getClass().getName());
    }

    default String ifNull(java.lang.Object o) {
        if (o == null) return "";
        return o.toString();
    }

    default String toString(List<Expression> params) {
        return params == null ? "" : params.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", "));
    }
}
