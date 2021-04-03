package me.mathyj.parser.ast.expression;

import me.mathyj.parser.ast.operator.BinaryOperator;
import me.mathyj.exception.eval.TypeMismatchException;
import me.mathyj.exception.eval.UnknownOperatorException;
import me.mathyj.object.Object;
import me.mathyj.object.*;

public class BinaryExpression extends Expression {
    public final Expression left;
    public final BinaryOperator operator;
    public final Expression right;

    public BinaryExpression(Expression left, BinaryOperator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(%s %s %s)".formatted(left, operator, right);
    }

    @Override
    public Object eval(Environment env) {
        var left = this.left.eval(env);
        var right = this.right.eval(env);
        if (left.type().equals(ObjectType.INTEGER) && right.type().equals(ObjectType.INTEGER)) {
            var leftVal = Integer.parseInt(left.value());
            var rightVal = Integer.parseInt(right.value());
            return switch (operator) {
                case ADD -> IntegerObject.valueOf(leftVal + rightVal);
                case SUBTRACT -> IntegerObject.valueOf(leftVal - rightVal);
                case MULTIPLY -> IntegerObject.valueOf(leftVal * rightVal);
                case DIVIDE -> IntegerObject.valueOf(leftVal / rightVal);
                case GREATER_THEN -> BooleanObject.valueOf(leftVal > rightVal);
                case GREATER_EQ -> BooleanObject.valueOf(leftVal >= rightVal);
                case LESS_THEN -> BooleanObject.valueOf(leftVal < rightVal);
                case LESS_EQ -> BooleanObject.valueOf(leftVal <= rightVal);
                case EQUALS -> BooleanObject.valueOf(leftVal == rightVal);
                case NOT_EQUALS -> BooleanObject.valueOf(leftVal != rightVal);
                default -> throw new UnknownOperatorException(left, operator, right);
            };
        } else if (left.type().equals(ObjectType.BOOLEAN) && right.type().equals(ObjectType.BOOLEAN)) {
            var eq = left.equals(right);
            var leftBool = left.equals(Object.TRUE);
            var rightBool = right.equals(Object.TRUE);
            return switch (operator) {
                case EQUALS -> BooleanObject.valueOf(eq);
                case NOT_EQUALS -> BooleanObject.valueOf(!eq);
                case AND -> BooleanObject.valueOf(leftBool && rightBool);
                case OR -> BooleanObject.valueOf(leftBool || rightBool);
                default -> throw new UnknownOperatorException(left, operator, right);
            };
        } else if (left.type().equals(ObjectType.STRING)) {
            if (operator.equals(BinaryOperator.ADD)) {
                return new StringObject(left.value() + right.value());
            } else {
                throw new UnknownOperatorException(left, operator, right);
            }
        }
        throw new TypeMismatchException(left, operator, right);
    }

}
