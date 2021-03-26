package me.mathyj.ast;

import java.util.ArrayList;
import java.util.List;

public class Program implements Node {
    public final List<Statement> statements;
    public final List<String> errors;

    public Program() {
        this.statements = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    @Override
    public String tokenLiteral() {
        if (statements.size() > 0) {
            return statements.get(0).tokenLiteral();
        } else {
            return "";
        }
    }

    public int size() {
        return statements.size();
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public void addError(RuntimeException e) {
        errors.add(e.getMessage());
    }

}
