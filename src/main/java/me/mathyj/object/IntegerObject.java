package me.mathyj.object;

public class IntegerObject implements Object {
    // 常用整数缓存，防止每次都新建对象
    private static final IntegerObject[] INTEGER_CACHE;
    private static final int INTEGER_CACHE_NUM = 128;

    static {
        INTEGER_CACHE = new IntegerObject[INTEGER_CACHE_NUM * 2];
        // 缓存 -128 ~ 127
        for (int i = 0; i < INTEGER_CACHE_NUM * 2; i++) {
            INTEGER_CACHE[i] = new IntegerObject(i - INTEGER_CACHE_NUM);
        }
    }

    public final int value;

    private IntegerObject(int value) {
        this.value = value;
    }

    public static IntegerObject valueOf(int value) {
        if (value >= -INTEGER_CACHE_NUM && value < INTEGER_CACHE_NUM) return INTEGER_CACHE[value + INTEGER_CACHE_NUM];
        return new IntegerObject(value);
    }

    @Override
    public ObjectType type() {
        return ObjectType.INTEGER;
    }

    @Override
    public String value() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
