package me.mathyj.parser.ast.statement;

import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.object.ReturnObject;
import me.mathyj.parser.ast.expression.Expression;
import me.mathyj.parser.token.Token;
import me.mathyj.parser.token.TokenType;

public class ReturnStatement extends Statement {
    private static final Token token = Token.build(TokenType.RETURN);
    public final Expression returnValue;

    public ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public ReturnStatement() {
        this(null);
    }

    @Override
    public String toString() {
        if (returnValue != null)
            return "%s %s;".formatted(token.literal(), returnValue);
        else
            return "%s".formatted(token.literal());
    }

    @Override
    public Object eval(Environment env) {
        if (returnValue != null) {
            return new ReturnObject(returnValue.eval(env));
        } else {
            return new ReturnObject(Object.NULL);
        }
    }
}
