package me.mathyj.exception.eval;

import me.mathyj.ast.operator.BinaryOperator;
import me.mathyj.object.Object;

public class TypeMismatchException extends EvalException {
    private final Object left;
    private final BinaryOperator operator;
    private final Object right;

    public TypeMismatchException(Object left, BinaryOperator operator, Object right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String getMessage() {
        return "type mismatch: %s %s %s".formatted(left.type(), operator, right.type());
    }
}
