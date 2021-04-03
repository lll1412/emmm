package me.mathyj.compiler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> store;
    private int numDefinitions;

    public SymbolTable() {
        this.store = new HashMap<>();
    }

    public Symbol define(String name) {
        var symbol = new Symbol(name, Symbol.Scope.GLOBAL, numDefinitions);
        store.put(name, symbol);
        numDefinitions++;
        return symbol;
    }

    public Symbol resolve(String name) {
        return store.get(name);
    }
}
