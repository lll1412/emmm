package me.mathyj.ast;

import me.mathyj.exception.UnimplementedException;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;

public interface Node {

    default Object eval(Environment env) {
        throw new UnimplementedException(getClass().getName());
    }

    default String ifNull(java.lang.Object o) {
        if (o == null) return "";
        return o.toString();
    }
}
