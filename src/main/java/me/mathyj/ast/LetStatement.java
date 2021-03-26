package me.mathyj.ast;

import me.mathyj.token.Token;

public class LetStatement implements Statement {
    private final Token token = Token.LET;

    public final Identifier name;
    public final Expression value;

    public LetStatement(Identifier name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }
}
