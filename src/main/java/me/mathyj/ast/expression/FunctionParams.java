package me.mathyj.ast.expression;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionParams extends Expression {
    private List<Identifier> params;

    public FunctionParams(List<Identifier> params) {
        this.params = params;
    }

    public FunctionParams() {
    }

    @Override
    public String toString() {
        if (params == null || params.isEmpty()) return "";
        var s = params.stream()
                .map(Identifier::toString)
                .collect(Collectors.joining(", "));
        return "%s".formatted(s);
    }
}
