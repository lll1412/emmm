package me.mathyj.ast.operator;

import me.mathyj.exception.UnsupportedBinaryOperator;
import me.mathyj.token.Token;

public enum BinaryOperator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    GREATER_THEN(">"),
    LESS_THEN("<"),
    EQUALS("=="),
    NOT_EQUALS("!="),
    ;
    String literal;

    BinaryOperator(String literal) {
        this.literal = literal;
    }

    public static BinaryOperator from(Token token) {
        var tokenType = token.type();
        return switch (tokenType) {
            case PLUS -> ADD;
            case MINUS -> SUBTRACT;
            case ASTERISK -> MULTIPLY;
            case SLASH -> DIVIDE;
            case LT -> LESS_THEN;
            case GT -> GREATER_THEN;
            case EQ -> EQUALS;
            case NE -> NOT_EQUALS;
            default -> throw new UnsupportedBinaryOperator(token);
        };
    }

    @Override
    public String toString() {
        return literal;
    }
}
