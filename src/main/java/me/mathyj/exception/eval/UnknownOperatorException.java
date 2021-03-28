package me.mathyj.exception.eval;

import me.mathyj.ast.operator.BinaryOperator;
import me.mathyj.ast.operator.UnaryOperator;
import me.mathyj.object.Object;

public class UnknownOperatorException extends EvalException {
    private final Object right;
    private final boolean isBin;
    private Object left;
    private BinaryOperator binaryOperator;
    private UnaryOperator unaryOperator;

    public UnknownOperatorException(Object left, BinaryOperator binaryOperator, Object right) {
        this.left = left;
        this.binaryOperator = binaryOperator;
        this.right = right;
        isBin = true;
    }

    public UnknownOperatorException(UnaryOperator unaryOperator, Object right) {
        this.unaryOperator = unaryOperator;
        this.right = right;
        isBin = false;
    }

    @Override
    public String getMessage() {
        if (isBin)
            return "unknown operator: %s %s %s".formatted(left.type(), binaryOperator, right.type());
        else
            return "unknown operator: %s %s".formatted(unaryOperator, right.type());
    }
}
