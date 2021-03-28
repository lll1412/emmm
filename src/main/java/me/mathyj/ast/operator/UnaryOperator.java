package me.mathyj.ast.operator;

import me.mathyj.exception.parse.UnsupportedUnaryException;
import me.mathyj.token.Token;

public enum UnaryOperator {
    BANG("!"),
    MINUS("-");

    private final String literal;

    UnaryOperator(String literal) {
        this.literal = literal;
    }

    public static UnaryOperator from(Token token) {
        return switch (token.type()) {
            case BANG -> UnaryOperator.BANG;
            case MINUS -> UnaryOperator.MINUS;
            default -> throw new UnsupportedUnaryException(token);
        };
    }


    @Override
    public String toString() {
        return literal;
    }
}
