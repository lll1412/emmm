package me.mathyj.ast;

import me.mathyj.token.Token;

public class ReturnStatement implements Statement {
    private static final Token token = Token.RETURN;
    private final Expression returnValue;

    public ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }
}
