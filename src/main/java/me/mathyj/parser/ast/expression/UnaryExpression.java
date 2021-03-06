package me.mathyj.parser.ast.expression;

import me.mathyj.parser.ast.operator.UnaryOperator;
import me.mathyj.exception.eval.UnknownOperatorException;
import me.mathyj.object.Environment;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;
import me.mathyj.object.ObjectType;

public class UnaryExpression extends Expression {
    public final UnaryOperator operator;
    public final Expression right;

    public UnaryExpression(UnaryOperator operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(%s%s)".formatted(operator, right);
    }

    @Override
    public Object eval(Environment env) {
        var eval = right.eval(env);
        return switch (operator) {
            case NOT -> switch (eval.type()) {
                case BOOLEAN -> eval.equals(Object.TRUE) ? Object.FALSE : Object.TRUE;
                case NULL -> Object.TRUE;
                default -> Object.FALSE;
//                default -> throw new UnsupportedUnaryException(operator, eval);
            };
            case NEG -> {
                if (eval.type() == ObjectType.INTEGER) yield IntegerObject.valueOf(-Integer.parseInt(eval.value()));
                else throw new UnknownOperatorException(operator, eval);
            }
        };
    }
}
