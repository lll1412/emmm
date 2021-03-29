package me.mathyj.ast.expression;

import me.mathyj.object.ArrayObject;
import me.mathyj.object.Environment;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;

/**
 * 索引表达式
 * arr[i]
 */
public class IndexExpression extends Expression {
    private final Expression array;
    private final Expression index;

    public IndexExpression(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public Object eval(Environment env) {
        var arrayObject = ((ArrayObject) array.eval(env));
        var indexObj = ((IntegerObject) index.eval(env));
        return arrayObject.get(indexObj.value);
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(array, index);
    }
}
