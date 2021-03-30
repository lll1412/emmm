package me.mathyj.ast.statement;

import me.mathyj.ast.expression.Expression;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.object.ReturnObject;
import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

public class ReturnStatement extends Statement {
    private static final Token token = Token.build(TokenType.RETURN);
    public final Expression returnValue;

    public ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        return "%s %s;".formatted(token.literal(), returnValue);
    }

    @Override
    public Object eval(Environment env) {
        return new ReturnObject(returnValue.eval(env));
    }
}
