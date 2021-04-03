package me.mathyj.parser.ast.expression;

import me.mathyj.object.Object;
import me.mathyj.object.*;

/**
 * 索引表达式
 * arr[i]
 */
public class IndexExpression extends Expression {
    public final Expression left;
    public final Expression index;

    public IndexExpression(Expression left, Expression index) {
        this.left = left;
        this.index = index;
    }

    @Override
    public Object eval(Environment env) {
        var leftObj = left.eval(env);
        var indexObj = index.eval(env);
        if (leftObj instanceof ArrayObject) {
            var arrayObject = (ArrayObject) leftObj;
            var index = ((IntegerObject) indexObj);
            return arrayObject.get(index.value);
        } else if (leftObj instanceof HashObject) {
            var hashObject = ((HashObject) leftObj);
            return hashObject.get(indexObj);
        }
        return Object.NULL;
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(left, index);
    }
}
