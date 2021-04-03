package me.mathyj.parser.ast;

import me.mathyj.parser.ast.statement.Statement;
import me.mathyj.exception.parse.ParseException;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.object.ObjectType;

import java.util.ArrayList;
import java.util.List;

public class Program implements ASTNode {
    public final List<Statement> statements;
    private final List<String> errors;

    public Program() {
        this.statements = new ArrayList<>();
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


    public Program addStatement(Statement statement) {
        statements.add(statement);
        return this;
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

    @Override
    public Object eval(Environment env) {
        var ret = Object.NULL;
        for (var statement : statements) {
            ret = statement.eval(env);
            //如果是return对象，不计算后续，直接返回
            if (ret.type() == ObjectType.RETURN) break;
        }
        return ret;
    }
}
