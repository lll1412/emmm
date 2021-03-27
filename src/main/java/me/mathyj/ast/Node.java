package me.mathyj.ast;

import java.util.Collection;

public interface Node {
    default String ifNull(Object o) {
        if (o == null) return "";
        return o.toString();
    }

    default boolean isEmpty(Object o) {
        if (o == null) return true;
        if (o instanceof String) return o.equals("");
        if (o instanceof Collection) return ((Collection<?>) o).isEmpty();
        return false;
    }
}
