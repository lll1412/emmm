package me.mathyj.exception.parse;

public class UnsupportedBinaryOperator extends ParseException {
    private final java.lang.Object token;

    public UnsupportedBinaryOperator(java.lang.Object token) {
        this.token = token;
    }
//
//    public UnsupportedBinaryOperator(BinaryOperator assignOp) {
//        this.token = assignOp;
//    }

    @Override
    public String getMessage() {
        return "unsupported binary operator: %s".formatted(token);
    }
}
