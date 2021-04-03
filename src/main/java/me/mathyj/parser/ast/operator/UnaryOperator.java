package me.mathyj.parser.ast.operator;

import me.mathyj.exception.parse.UnsupportedUnaryException;
import me.mathyj.parser.token.Token;

public enum UnaryOperator {
    NOT("!"),
    NEG("-"),
//    INC("++"),
//    DEC("--")
    ;

    private final String literal;

    UnaryOperator(String literal) {
        this.literal = literal;
    }

    public static UnaryOperator from(Token token) {
        return switch (token.type()) {
            case BANG -> UnaryOperator.NOT;
            case MINUS -> UnaryOperator.NEG;
//            case INC -> UnaryOperator.INC;
//            case DEC -> UnaryOperator.DEC;
            default -> throw new UnsupportedUnaryException(token);
        };
    }


    @Override
    public String toString() {
        return literal;
    }
}
