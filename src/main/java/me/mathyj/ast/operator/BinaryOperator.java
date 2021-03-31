package me.mathyj.ast.operator;

import me.mathyj.exception.parse.UnsupportedBinaryOperator;
import me.mathyj.token.Token;
import me.mathyj.token.TokenType;

import static me.mathyj.token.TokenType.ASTERISK_ASSIGN;

public enum BinaryOperator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    GREATER_THEN(">"),
    LESS_THEN("<"),
    EQUALS("=="),
    NOT_EQUALS("!="),
    ASSIGN("="),

    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    ;

    final String literal;

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
            case ASSIGN -> ASSIGN;
            default -> throw new UnsupportedBinaryOperator(token);
        };
    }

    public static BinaryOperator assignFrom(BinaryOperator assignOp) {
        return switch (assignOp) {
            case ADD_ASSIGN -> ADD;
            case SUB_ASSIGN -> SUBTRACT;
            case MUL_ASSIGN -> MULTIPLY;
            case DIV_ASSIGN -> DIVIDE;
            case ASSIGN -> ASSIGN;
            default -> assignOp;
        };
    }

    public static BinaryOperator assignFrom(TokenType tokenType) {
        return switch (tokenType) {
            case PLUS_ASSIGN -> ADD;
            case MINUS_ASSIGN -> SUBTRACT;
            case ASTERISK_ASSIGN -> MULTIPLY;
            case SLASH_ASSIGN -> DIVIDE;
            case ASSIGN -> ASSIGN;
            default -> throw new UnsupportedBinaryOperator(tokenType);
        };
    }

    @Override
    public String toString() {
        return literal;
    }
}
