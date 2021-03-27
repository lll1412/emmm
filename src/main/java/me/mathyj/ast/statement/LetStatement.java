package me.mathyj.ast.statement;

import me.mathyj.ast.expression.Expression;
import me.mathyj.ast.expression.Identifier;
import me.mathyj.token.Token;

public class LetStatement extends Statement {
    private final Identifier name;
    private final Expression value;
    private final Token token = Token.LET;

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
}
