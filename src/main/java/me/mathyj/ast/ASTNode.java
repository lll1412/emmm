package me.mathyj.ast;

import me.mathyj.ast.expression.Expression;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface ASTNode {

    Object eval(Environment env);

//    Bytecode compile();

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
