package me.mathyj.compiler;

import me.mathyj.object.BuiltinObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SymbolTable {
    private static final Map<String, Symbol> builtins;

    static {
        builtins = new LinkedHashMap<>(BuiltinObject.builtinMap.size());
        var i = new AtomicInteger();
        BuiltinObject.builtinMap.forEach((name, obj) -> builtins.put(name, new Symbol(name, Symbol.Scope.BUILTIN, i.getAndIncrement())));
    }

    public final SymbolTable parent;
    private final Map<String, Symbol> store;
    private int numDefinitions;

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
//        if (parent != null) numDefinitions += parent.numDefinitions();
        this.store = new HashMap<>();
    }

    public Symbol define(String name) {
        var scope = parent == null ? Symbol.Scope.GLOBAL : Symbol.Scope.LOCAL;
        var symbol = new Symbol(name, scope, numDefinitions);
        store.put(name, symbol);
        numDefinitions++;
        return symbol;
    }

    public Symbol resolve(String name) {
        return store.getOrDefault(name, parent != null ? parent.resolve(name) : builtins.get(name));
    }

    public int numDefinitions() {
        return numDefinitions;
    }
}
