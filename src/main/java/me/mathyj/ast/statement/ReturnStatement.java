package me.mathyj.ast.statement;

import me.mathyj.ast.expression.Expression;
import me.mathyj.token.Token;

public class ReturnStatement extends Statement {
    private static final Token token = Token.RETURN;
    private final Expression returnValue;

    public ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        return "%s %s;".formatted(token.literal(), returnValue);
    }
}
