package me.mathyj.compiler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
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
        return store.getOrDefault(name, parent == null ? null : parent.resolve(name));
    }

    public int numDefinitions() {
        return numDefinitions;
    }
}
