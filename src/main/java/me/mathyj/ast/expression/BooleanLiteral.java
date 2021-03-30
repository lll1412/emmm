package me.mathyj.ast.expression;

import me.mathyj.object.BooleanObject;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

public class BooleanLiteral extends Expression {
    public static final BooleanLiteral TRUE = new BooleanLiteral(true);
    public static final BooleanLiteral FALSE = new BooleanLiteral(false);
    private final boolean bool;

    public BooleanLiteral(boolean bool) {
        this.bool = bool;
    }

    public BooleanLiteral(Token token) {
        this.bool = token.type().equals(TokenType.TRUE);
    }

    @Override
    public String toString() {
        return String.valueOf(bool);
    }

    @Override
    public Object eval(Environment env) {
        return BooleanObject.valueOf(bool);
    }

}
