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
        if (store.containsKey(name)) return null;
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

    public void defineFunction(String name) {
        var symbol = new Symbol(name, Symbol.Scope.FUNCTION, 0);
        store.put(name, symbol);
    }

    public Symbol resolve(String name) {
        if (store.containsKey(name)) {//优先但当前作用域查询
            return store.get(name);
        } else if (outer != null) {//外部函数作用域查找
            var symbol = outer.resolve(name);
            if (symbol == null) return null;
            if (symbol.scope() == Symbol.Scope.GLOBAL || symbol.scope() == Symbol.Scope.BUILTIN) {
                return symbol;
            }
            // 外部函数的local变量，就是自由变量
            return defineFree(symbol);
        } else {
            return builtins.get(name);// 内置函数
        }
    }

    public int numDefinitions() {
        return numDefinitions;
    }
}
