package me.mathyj.ast.expression;

import me.mathyj.ast.operator.BinaryOperator;
import me.mathyj.exception.eval.UndefinedException;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.token.TokenType;

public class AssignExpression extends Expression {
    public static AssignExpression INC(Identifier identifier) {
        return new AssignExpression(identifier, BinaryOperator.ADD_ASSIGN, new IntegerLiteral(1));
    }

    public static AssignExpression DEC(Identifier identifier) {
        return new AssignExpression(identifier, BinaryOperator.SUB_ASSIGN, new IntegerLiteral(1));
    }

    private final Identifier left;
    private final Expression right;

    public AssignExpression(Identifier left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public AssignExpression(Identifier left, BinaryOperator op, Expression right) {
        this.left = left;
        this.right = new BinaryExpression(left, BinaryOperator.assignFrom(op), right);
    }

    public AssignExpression(Identifier left, TokenType op, Expression right) {
        this.left = left;
        this.right = new BinaryExpression(left, BinaryOperator.assignFrom(op), right);
    }

    @Override
    public Object eval(Environment env) {
        var leftObj = env.get(left.identifier);
        if (leftObj != null) {
            return env.set(left.identifier, right.eval(env));
        } else {
            throw new UndefinedException(left.identifier);
        }
    }

    @Override
    public String toString() {
        return "(%s = %s)".formatted(ifNull(left), ifNull(right));
    }
}
