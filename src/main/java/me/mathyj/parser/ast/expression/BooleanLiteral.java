package me.mathyj.parser.ast.expression;

import me.mathyj.object.BooleanObject;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.parser.token.Token;
import me.mathyj.parser.token.TokenType;

public class BooleanLiteral extends Expression {
    public static final BooleanLiteral TRUE = new BooleanLiteral(true);
    public static final BooleanLiteral FALSE = new BooleanLiteral(false);
    private final boolean bool;

    private BooleanLiteral(boolean bool) {
        this.bool = bool;
    }

    public static BooleanLiteral valueOf(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public static BooleanLiteral valueOf(Token token) {
        return token.type().equals(TokenType.TRUE) ? BooleanLiteral.TRUE : BooleanLiteral.FALSE;
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
