package me.mathyj.ast;

import me.mathyj.token.Token;

public class Identifier {
    public final Token token;

    public Identifier(String value) {
        token = Token.IDENT(value);
    }
}
