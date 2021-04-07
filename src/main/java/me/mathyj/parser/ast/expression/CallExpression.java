package me.mathyj.parser.ast.expression;

import me.mathyj.exception.eval.ErrorArgumentsCount;
import me.mathyj.exception.eval.UndefinedException;
import me.mathyj.object.BuiltinObject;
import me.mathyj.object.Environment;
import me.mathyj.object.FunctionObject;
import me.mathyj.object.Object;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CallExpression extends Expression {
    public final Expression left;
    // 实参
    public final List<Expression> arguments;

    public CallExpression(Expression left, List<Expression> arguments) {
        this.left = left;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        var argumentsStr = arguments == null ? "" : arguments.stream().map(Objects::toString).collect(Collectors.joining(", "));
        return "%s(%s)".formatted(ifNull(left), argumentsStr);
    }

    @Override
    public Object eval(Environment env) {
        var obj = left.eval(env);
        if (obj instanceof FunctionObject fn) {
            if (arguments.size() != fn.params.size()) {
                throw new ErrorArgumentsCount(fn.params.size(), arguments.size());
            }
            return fn.apply(arguments);
        } else if (obj instanceof BuiltinObject) {
            var args = eval(arguments, env);
            return ((BuiltinObject) obj).apply(args);
        }
        throw new UndefinedException("");
    }

    private List<Object> eval(List<Expression> expressionList, Environment env) {
        return expressionList.stream().map(expr -> expr.eval(env)).collect(Collectors.toList());
    }
}
