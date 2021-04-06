package me.mathyj.parser.ast.expression;

import me.mathyj.parser.ast.statement.BlockStatement;
import me.mathyj.object.Environment;
import me.mathyj.object.FunctionObject;
import me.mathyj.object.Object;

import java.util.List;

/**
 * fn identifier(params...) { body }
 * or
 * let identifier = fn (params...) { body }
 */
public class FunctionLiteral extends Expression {
    public Identifier identifier;
    public List<Identifier> params;
    public BlockStatement body;

    public FunctionLiteral(Identifier identifier, List<Identifier> params, BlockStatement body) {
        this.identifier = identifier;
        this.params = params;
        this.body = body;
    }

    public FunctionLiteral(List<Identifier> params, BlockStatement body) {
        this.params = params;
        this.body = body;
    }

    public FunctionLiteral(List<Identifier> params) {
        this.params = params;
    }


    public FunctionLiteral() {
    }

    @Override
    public String toString() {
        return """
                fn %s(%s) {
                    %s
                }
                """.formatted(ifNull(identifier), toString(params), ifNull(body));
    }

    @Override
    public Object eval(Environment env) {
        var fn = new FunctionObject(params, body, env);
        if (identifier != null) env.set(identifier.value, fn);
        return fn;
    }
}
