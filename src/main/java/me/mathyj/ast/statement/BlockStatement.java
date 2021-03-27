package me.mathyj.ast.statement;

import java.util.List;

public class BlockStatement extends Statement {
    private final List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        if (statements == null || statements.isEmpty()) return "";
        var buff = new StringBuilder();
        for (Statement statement : statements) {
            buff.append(statement);
        }
        return buff.toString();
    }
}
