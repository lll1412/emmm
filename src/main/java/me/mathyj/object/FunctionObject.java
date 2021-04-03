package me.mathyj.object;

import me.mathyj.parser.ast.expression.Expression;
import me.mathyj.parser.ast.expression.Identifier;
import me.mathyj.parser.ast.statement.BlockStatement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionObject implements Object {
    private static final String TAB = "  ";
    // 形参
    public final List<Expression> params;
    private final Environment localEnv;
    private final BlockStatement body;

    public FunctionObject(List<Expression> params, BlockStatement body, Environment parent) {
        this.localEnv = new Environment(parent);
        this.params = params;
        this.body = body;
    }

    public FunctionObject() {
        this(List.of(), BlockStatement.emptyBlock(), null);
    }

    public Object apply(List<Expression> arguments) {
        for (int i = 0; i < arguments.size(); i++) {
            var expr = arguments.get(i);
            var val = expr.eval(localEnv);
            var param = params.get(i);
            localEnv.set(((Identifier) param).value, val);
        }
        return body.eval(localEnv);
    }

    @Override
    public ObjectType type() {
        return ObjectType.FUNCTION;
    }

    @Override
    public String value() {
        return Object.NULL.value();
    }

    @Override
    public String toString() {
        var paramsStr = params == null ? "" : params.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", "));
        return """
                fn(%s) {
                %s%s
                }
                """.formatted(paramsStr, TAB, body);
    }
}
