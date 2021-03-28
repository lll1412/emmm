package me.mathyj.object;

import me.mathyj.ast.expression.CallArguments;
import me.mathyj.ast.expression.FunctionParams;
import me.mathyj.ast.statement.BlockStatement;

public class FunctionObject implements Object {
    private static final String TAB = "  ";
    // 形参
    public final FunctionParams params;
    public final Environment localEnv;
    private final BlockStatement body;

    public FunctionObject(FunctionParams params, BlockStatement body, Environment parent) {
        this.localEnv = new Environment(parent);
        this.params = params;
        this.body = body;
    }

    public FunctionObject(Environment parent) {
        this(new FunctionParams(), BlockStatement.emptyBlock(), parent);
    }

    public FunctionObject() {
        this(new FunctionParams(), BlockStatement.emptyBlock(), null);
    }

    public Object apply(CallArguments arguments) {
        for (int i = 0; i < arguments.arguments.size(); i++) {
            var expr = arguments.arguments.get(i);
            var val = expr.eval(localEnv);
            var param = params.getParam(i);
            localEnv.set(param, val);
//            if (val.type().equals(ObjectType.RETURN)) return val;
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
        return """
                fn(%s) {
                %s%s
                }
                """.formatted(params, TAB, body);
    }
}
