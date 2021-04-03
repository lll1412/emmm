package me.mathyj.exception.runtime;

import me.mathyj.object.Object;

public class UnsupportedIndexOpcode extends RuntimeException {
    private final Object object;
    private final Object indexObj;

    public UnsupportedIndexOpcode(Object object, Object indexObj) {
        this.object = object;
        this.indexObj = indexObj;
    }

    @Override
    public String getMessage() {
        return "unsupported index operation: %s[%s]".formatted(object, indexObj);
    }
}
