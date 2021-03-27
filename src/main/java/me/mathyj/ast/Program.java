package me.mathyj.ast;

import me.mathyj.ast.statement.Statement;
import me.mathyj.exception.ParseException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Program implements Node {
    private final List<Statement> statements;
    private final List<String> errors;

    public Program() {
        this.statements = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public Program(List<Statement> statements) {
        this.statements = statements;
        this.errors = new ArrayList<>();
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        for (var statement : statements) {
            buf.append(statement);
        }
        return buf.toString();
    }

    public int size() {
        return statements.size();
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public void addError(ParseException e) {
        errors.add(e.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
