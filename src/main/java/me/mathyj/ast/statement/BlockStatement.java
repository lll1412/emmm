package me.mathyj.ast.statement;

import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.object.ObjectType;

import java.util.Collections;
import java.util.List;

public class BlockStatement extends Statement {
    public final List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    public static BlockStatement emptyBlock() {
        return new BlockStatement(Collections.emptyList());
    }

    @Override
    public String toString() {
        if (statements == null || statements.isEmpty()) return "";
        var buff = new StringBuilder();
        for (var statement : statements) {
            buff.append(statement);
        }
        return buff.toString();
    }

    @Override
    public Object eval(Environment env) {
        var ret = Object.NULL;
        for (var statement : statements) {
            ret = statement.eval(env);
            if (ret.type() == ObjectType.RETURN) break;
        }
        return ret;
    }
}
