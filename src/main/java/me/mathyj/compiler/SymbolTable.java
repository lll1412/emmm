package me.mathyj.compiler;

import me.mathyj.object.BuiltinObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SymbolTable {
    private static final Map<String, Symbol> builtins;

    static {
        builtins = new LinkedHashMap<>(BuiltinObject.builtinMap.size());
        var i = new AtomicInteger();
        BuiltinObject.builtinMap.forEach((name, obj) -> builtins.put(name, new Symbol(name, Symbol.Scope.BUILTIN, i.getAndIncrement())));
    }

    public final SymbolTable outer;
    public final List<Symbol> freeSymbols;
    private final Map<String, Symbol> store;
    private int numDefinitions;

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable outer) {
        this.outer = outer;
//        if (parent != null) numDefinitions += parent.numDefinitions();
        this.store = new HashMap<>();
        this.freeSymbols = new ArrayList<>();
    }

    public Symbol define(String name) {
        var scope = outer == null ? Symbol.Scope.GLOBAL : Symbol.Scope.LOCAL;
        var symbol = new Symbol(name, scope, numDefinitions);
        store.put(name, symbol);
        numDefinitions++;
        return symbol;
    }

    public Symbol defineFree(Symbol original) {
        freeSymbols.add(original);
        var symbol = new Symbol(original.name(), Symbol.Scope.FREE, freeSymbols.size() - 1);
        store.put(original.name(), symbol);
        return symbol;
    }

    public Symbol resolve(String name) {
        if (store.containsKey(name)) {
            return store.get(name);
        } else if (outer != null) {
            var symbol = outer.resolve(name);
            if (symbol == null) return builtins.get(name);
            if (symbol.scope() == Symbol.Scope.GLOBAL || symbol.scope() == Symbol.Scope.BUILTIN) {
                return symbol;
            }
            return defineFree(symbol);
        }
        return builtins.get(name);
//        return store.getOrDefault(name, parent != null ? parent.resolve(name) : builtins.get(name));
    }

    public int numDefinitions() {
        return numDefinitions;
    }
}
