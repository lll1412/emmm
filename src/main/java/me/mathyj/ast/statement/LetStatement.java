package me.mathyj.ast.statement;

import me.mathyj.ast.expression.Expression;
import me.mathyj.ast.expression.Identifier;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

public class LetStatement extends Statement {
    private static final Token token = Token.build(TokenType.LET);
    public final Identifier name;
    public final Expression value;

    public LetStatement(Identifier name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "%s %s = %s;".formatted(token.literal(), name, value);
    }

    public String name() {
        return name.toString();
    }

    @Override
    public Object eval(Environment env) {
        var eval = value.eval(env);
        env.set(name.identifier, eval);
        return eval;
    }
}
