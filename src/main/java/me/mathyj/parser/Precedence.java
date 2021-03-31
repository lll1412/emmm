package me.mathyj.parser;

import me.mathyj.token.Token;

/**
 * 优先级
 */
public enum Precedence {
    LOWEST,
    ASSIGN,
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
            case PLUS, MINUS,PLUS_ASSIGN,MINUS_ASSIGN -> SUM;
            case ASTERISK, SLASH,ASTERISK_ASSIGN,SLASH_ASSIGN -> PRODUCT;
            case LPAREN -> CALL;
            case LBRACKET -> INDEX;
            case ASSIGN -> ASSIGN;
            default -> LOWEST;
        };
    }

    public boolean lt(Precedence precedence) {
        return this.compareTo(precedence) < 0;
    }
}
