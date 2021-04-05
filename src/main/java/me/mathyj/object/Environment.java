package me.mathyj.object;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private static final Map<String, Object> builtins = BuiltinObject.builtinMap;
    private final Map<String, Object> store;
    private Environment parent;

    public Environment() {
        this.store = new HashMap<>();
    }

    public Environment(Environment parent) {
        this.parent = parent;
        this.store = new HashMap<>();
    }

    public Object get(String name) {
        // 先从当前环境查询，再去父环境递归查询
        return store.getOrDefault(name, parent == null ? builtins.get(name) : parent.get(name));
    }

    public Object set(String name, Object val) {
        store.put(name, val);
        return val;
    }
}
