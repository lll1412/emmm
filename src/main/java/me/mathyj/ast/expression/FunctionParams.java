package me.mathyj.ast.expression;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionParams {
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

    public int size() {
        return params == null ? 0 : params.size();
    }
    public String getParam(int i) {
        return params.get(i).identifier;
    }
}
