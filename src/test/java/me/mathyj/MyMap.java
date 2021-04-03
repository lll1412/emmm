package me.mathyj;

import java.util.LinkedHashMap;

// 因为 Map.of(..) 最多只支持10个键值对，所以这里自己封装一下
// 这里使用LinkedHashMap保证内部顺序和添加顺序一致
public class MyMap<V> extends LinkedHashMap<String, V> {
    public static MyMap<Object> of(Object... els) {
        assert els != null && els.length % 2 == 0;
        var myMap = new MyMap<>();
        for (int i = 0; i < els.length; i += 2) {
            myMap.put(String.valueOf(els[i]), els[i + 1]);
        }
        return myMap;
    }
}
