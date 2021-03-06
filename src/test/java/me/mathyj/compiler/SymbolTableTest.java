package me.mathyj.compiler;

import me.mathyj.MyMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymbolTableTest {
    /**
     * 符号定义测试
     */
    @Test
    void define() {
        var tests = MyMap.of(
                "a", new Symbol("a", Symbol.Scope.GLOBAL, 0),
                "b", new Symbol("b", Symbol.Scope.GLOBAL, 1)
        );
        var global = new SymbolTable();
        tests.forEach((name, symbol) -> {
            var s = global.define(name);
            assertEquals(s, symbol);
        });
    }

    @Test
    void resolve() {
        var global = new SymbolTable();
        global.define("a");
        global.define("b");

        var tests = List.of(
                new Symbol("a", Symbol.Scope.GLOBAL, 0),
                new Symbol("b", Symbol.Scope.GLOBAL, 1)
        );
        tests.forEach(symbol -> {
            var s = global.resolve(symbol.name());
            assertEquals(s, symbol);
        });
    }

    @Test
    void resolveLocal() {
        var global = new SymbolTable();
        global.define("a");
        global.define("b");

        var firstLocal = new SymbolTable(global);
        firstLocal.define("c");
        firstLocal.define("d");

        var secondLocal = new SymbolTable(firstLocal);
        secondLocal.define("e");
        secondLocal.define("f");
        var tests = List.of(
                new Symbol("a", Symbol.Scope.GLOBAL, 0),
                new Symbol("b", Symbol.Scope.GLOBAL, 1),
//                new Symbol("c", Symbol.Scope.LOCAL, 0),
//                new Symbol("d", Symbol.Scope.LOCAL, 1),
                new Symbol("e", Symbol.Scope.LOCAL, 0),
                new Symbol("f", Symbol.Scope.LOCAL, 1)
        );
        tests.forEach(symbol -> assertEquals(secondLocal.resolve(symbol.name()), symbol));
    }


}