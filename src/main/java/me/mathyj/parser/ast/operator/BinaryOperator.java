package me.mathyj.parser.ast.operator;

import me.mathyj.exception.parse.UnsupportedBinaryOperator;
import me.mathyj.parser.token.Token;
import me.mathyj.parser.token.TokenType;

public enum BinaryOperator {
    // 四则运算
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),

    // 比较
    GREATER_THEN(">"),
    GREATER_EQ(">="),
    LESS_THEN("<"),
    LESS_EQ("<="),
    EQUALS("=="),
    NOT_EQUALS("!="),
    AND("&&"),
    OR("||"),


    // 赋值
    ASSIGN("="),

    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),

    AND_ASSIGN("&&="),
    OR_ASSIGN("||="),
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
            case LE -> LESS_EQ;
            case GT -> GREATER_THEN;
            case GE -> GREATER_EQ;
            case EQ -> EQUALS;
            case NE -> NOT_EQUALS;
            case ASSIGN -> ASSIGN;
            case AND -> AND;
            case OR -> OR;
            default -> throw new UnsupportedBinaryOperator(token);
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
