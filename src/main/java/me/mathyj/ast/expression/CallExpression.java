package me.mathyj.ast.expression;

import me.mathyj.exception.eval.ErrorArgumentsCount;
import me.mathyj.exception.eval.UndefinedException;
import me.mathyj.object.BuiltinObject;
import me.mathyj.object.Environment;
import me.mathyj.object.FunctionObject;
import me.mathyj.object.Object;

public class CallExpression extends Expression {
    private final Identifier fnName;
    // 实参
    private final CallArguments arguments;

    public CallExpression(Identifier fnName, CallArguments arguments) {
        this.fnName = fnName;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(ifNull(fnName), ifNull(arguments));
    }

    @Override
    public Object eval(Environment env) {
        var obj = env.get(fnName.identifier);
        if (obj instanceof FunctionObject) {
            var fn = ((FunctionObject) obj);
            if (arguments.size() != fn.params.size()) {
                throw new ErrorArgumentsCount(fn.params.size(), arguments.size());
            }
            return fn.apply(arguments);
        } else if (obj instanceof BuiltinObject) {
            var args = arguments.eval(env);
            return ((BuiltinObject) obj).apply(args);
        }
        throw new UndefinedException(fnName.identifier);
    }
}
