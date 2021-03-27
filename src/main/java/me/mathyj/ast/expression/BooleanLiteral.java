package me.mathyj.ast.expression;

import me.mathyj.token.Token;

public class BooleanLiteral extends Expression {
    public static final BooleanLiteral TRUE = new BooleanLiteral(true);
    public static final BooleanLiteral FALSE = new BooleanLiteral(false);
    private final boolean bool;

    public BooleanLiteral(boolean bool) {
        this.bool = bool;
    }

    public BooleanLiteral(Token token) {
        this.bool = token == Token.TRUE;
    }

    @Override
    public String toString() {
        return String.valueOf(bool);
    }
}
