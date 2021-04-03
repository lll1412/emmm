package me.mathyj.parser.ast.expression;

import me.mathyj.parser.ast.operator.BinaryOperator;
import me.mathyj.exception.eval.UndefinedException;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.parser.token.TokenType;

public class AssignExpression extends Expression {
    public static AssignExpression INC(Identifier identifier) {
        return new AssignExpression(identifier, TokenType.PLUS_ASSIGN, new IntegerLiteral(1));
    }

    public static AssignExpression DEC(Identifier identifier) {
        return new AssignExpression(identifier, TokenType.MINUS_ASSIGN, new IntegerLiteral(1));
    }

    private final Identifier left;
    private final Expression right;

    public AssignExpression(Identifier left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public AssignExpression(Identifier left, TokenType op, Expression right) {
        this.left = left;
        this.right = new BinaryExpression(left, BinaryOperator.assignFrom(op), right);
    }

    @Override
    public Object eval(Environment env) {
        var leftObj = env.get(left.value);
        if (leftObj != null) {
            return env.set(left.value, right.eval(env));
        } else {
            throw new UndefinedException(left.value);
        }
    }

    @Override
    public String toString() {
        return "(%s = %s)".formatted(ifNull(left), ifNull(right));
    }
}
