package me.mathyj.ast.expression;

public class CallExpression extends Expression {
    private Identifier identifier;
    private CallArguments arguments;

    public CallExpression(Identifier identifier, CallArguments arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public CallExpression(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(ifNull(identifier), ifNull(arguments));
    }
}
