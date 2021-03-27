package me.mathyj.ast.expression;

import me.mathyj.ast.statement.BlockStatement;

/**
 * fn identifier(params...) { body }
 * or
 * let identifier = fn (params...) { body }
 */
public class FunctionLiteral extends Expression {
    private Identifier identifier;
    private FunctionParams params;
    private BlockStatement body;

    public FunctionLiteral(Identifier identifier, FunctionParams params, BlockStatement body) {
        this.identifier = identifier;
        this.params = params;
        this.body = body;
    }

    public FunctionLiteral(FunctionParams params, BlockStatement body) {
        this.params = params;
        this.body = body;
    }

    public FunctionLiteral(FunctionParams params) {
        this.params = params;
    }

    public FunctionLiteral(BlockStatement body) {
        this.body = body;
    }

    public FunctionLiteral() {
    }

    @Override
    public String toString() {
        return """
                fn %s(%s) {
                    %s
                }
                """.formatted(ifNull(identifier), ifNull(params), ifNull(body));
    }
}
