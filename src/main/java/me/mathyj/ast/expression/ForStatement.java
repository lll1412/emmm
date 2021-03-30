package me.mathyj.ast.expression;

import me.mathyj.ast.statement.BlockStatement;
import me.mathyj.ast.statement.LetStatement;
import me.mathyj.ast.statement.Statement;

public class ForStatement extends Statement {
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
}
