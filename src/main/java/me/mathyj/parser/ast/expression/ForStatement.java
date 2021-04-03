package me.mathyj.parser.ast.expression;

import me.mathyj.parser.ast.statement.BlockStatement;
import me.mathyj.parser.ast.statement.LetStatement;
import me.mathyj.parser.ast.statement.Statement;
import me.mathyj.object.BooleanObject;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.object.ReturnObject;
import me.mathyj.parser.token.Token;
import me.mathyj.parser.token.TokenType;

public class ForStatement extends Statement {
    private static final Token token = Token.build(TokenType.FOR);
    private LetStatement initial;
    private Expression condition;
    private Expression last;
    private BlockStatement block;

    public ForStatement setInitial(LetStatement initial) {
        this.initial = initial;
        return this;
    }

    public ForStatement setCondition(Expression condition) {
        this.condition = condition;
        return this;
    }

    public ForStatement setLast(Expression last) {
        this.last = last;
        return this;
    }

    public ForStatement setBlock(BlockStatement block) {
        this.block = block;
        return this;
    }

    @Override
    public Object eval(Environment env) {
        if (initial != null) initial.eval(env);
        var isTruthy = true;
        while (isTruthy) {
            if (condition != null) {
                var condObj = condition.eval(env);
                if (condObj instanceof BooleanObject) {// 必须是布尔值
                    isTruthy = condObj == Object.TRUE;
                } else {
                    throw new RuntimeException("unexpected object in for statement: %s".formatted(condObj));// 懒得创建异常类了
                }
            }
            if (isTruthy) {
                if (block != null) {
                    var result = block.eval(env);
                    if (result instanceof ReturnObject) return result;
                }
                if (last != null) last.eval(env);
            }
        }
        return Object.NULL;
    }

    @Override
    public String toString() {
        return """
                %s (%s %s; %s) {
                \t%s
                }
                """.formatted(token, ifNull(initial), ifNull(condition), ifNull(last), ifNull(block));
    }
}
