package me.mathyj.parser;

import me.mathyj.parser.token.Token;

/**
 * 优先级
 */
public enum Precedence {
    LOWEST,
    ASSIGN,
    // && ||
    LOGIC,
    // ==
    EQUALS,
    // + -
    SUM,
    // > or <
    LESS_GRATER,
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
            case LBRACKET -> INDEX;
            case LPAREN -> CALL;
            case AND, OR -> LOGIC;
            case ASTERISK, SLASH, ASTERISK_ASSIGN, SLASH_ASSIGN -> PRODUCT;
            case PLUS, MINUS, PLUS_ASSIGN, MINUS_ASSIGN -> SUM;
            case LT, LE, GT, GE -> LESS_GRATER;
            case EQ, NE -> EQUALS;
            case ASSIGN -> ASSIGN;
            default -> LOWEST;
        };
    }

    public boolean lt(Precedence precedence) {
        return this.compareTo(precedence) < 0;
    }
}
