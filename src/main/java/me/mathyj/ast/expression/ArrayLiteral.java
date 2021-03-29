package me.mathyj.ast.expression;

import me.mathyj.object.ArrayObject;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayLiteral extends Expression {

    private final List<Expression> elements;

    public ArrayLiteral(Expression... elements) {
        this.elements = Arrays.asList(elements);
    }

    public ArrayLiteral(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public Object eval(Environment env) {
        var list = elements.stream()
                .map(element -> element.eval(env))
                .collect(Collectors.toList());
        return new ArrayObject(list);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
