package me.mathyj.parser;

import me.mathyj.token.Token;

/**
 * 优先级
 */
public enum Precedence {
    LOWEST,
    // ==
    EQUALS,
    // > or <
    LESS_GRATER,
    // + -
    SUM,
    // * /
    PRODUCT,
    // !x or -x
    PREFIX,
    // myFunction(x)
    CALL,
    //
    INDEX,
    ;

    public static Precedence from(Token token) {
        return switch (token.type()) {
            case EQ, NE -> EQUALS;
            case LT, GT -> LESS_GRATER;
            case PLUS, MINUS -> SUM;
            case ASTERISK, SLASH -> PRODUCT;
            case LPAREN -> CALL;
            case LBRACKET -> INDEX;
            default -> LOWEST;
        };
    }

    public boolean lt(Precedence precedence) {
        return this.compareTo(precedence) < 0;
    }
}
