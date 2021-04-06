package me.mathyj.vm;

import me.mathyj.MyMap;
import me.mathyj.compiler.Compiler;
import me.mathyj.object.Object;
import me.mathyj.object.*;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class VmTest {
    // 虚拟机引擎执行算数运算测试
    @Test
    void integerArithmetic() {
        var tests = MyMap.of(
                "1", 1,
                "2", 2,
                "1+2", 3,
                "1-2", -1,
                "2 * 2", 4,
                "2 / 2", 1,
                "50/2*2+10-5", 55,
                "5*(2+10)", 60,
                "-5", -5,
                "2+-3", -1,
                "-20+15+-5", -10
        );
        vmRunCheck(tests);
    }

    // 布尔表达式执行测试
    @Test
    void booleanExpression() {
        var tests = MyMap.of(
                "true", Object.TRUE,
                "false", false,
                "2>1", true,
                "2==1", false,
                "2!=1", true,
                "true == false", false,
                "true!=false", true,
                "(1<2) == true", true,
                "(1<2) == false", false,
                "(1>2) == true", false,
                "(1>2) == false", true,
                "!true", false,
                "!!true", true,
                "!5", false,
                "!!5", true
        );
        vmRunCheck(tests);
    }

    // 条件表达式执行测试
    @Test
    void conditionExpression() {
        var tests = Map.of(
                "if (true) { 10 }", 10,
                "if (true) { 10 } else { 20 }", 10,
                "if (false) { 10 } else { 20 }", 20,
                "if (1) { 10 } else { 20 }", 10,
                "if (1 < 2) { 10 } else { 20 }", 10,
                "if (1 > 2) { 10 } else { 20 }", 20,
                "if(false){10}", Object.NULL,
                "if(if(false){10}) {10} else {20}", 20

        );
        vmRunCheck(tests);
    }

    // 变量绑定执行测试
    @Test
    void letStatement() {
        var tests = MyMap.of(
                "let one = 1;", 1,
                "let one = 1; one", 1,
                "let one = 1; let two = one; two", 1,
                "let one = 1; let two = 2; one + two", 3,
                """
                        let num = 3;
                        fn() { let mul = 2; mul * num }()
                        """, 6

        );
        vmRunCheck(tests);
    }

    // 字符串执行测试
    @Test
    void stringLiteral() {
        vmRunCheck(MyMap.of(
                "'hello'", "hello",
                "'hello' + 123 + 'world'", "hello123world"
        ));
    }

    @Test
    void arrayLiteral() {
        vmRunCheck(MyMap.of(
                "[]", new ArrayObject(),
                "[1 + 1, 2 + 3, 3 + 5]", new ArrayObject(IntegerObject.valueOf(2), IntegerObject.valueOf(5), IntegerObject.valueOf(8))
        ));
    }

    @Test
    void hashLiteral() {
        vmRunCheck(MyMap.of(
                "{}", new HashObject(),
                "{'name':'ljz','age': 25}", new HashObject(new LinkedHashMap<>() {{
                    put(StringObject.valueOf("name"), StringObject.valueOf("ljz"));
                    put(StringObject.valueOf("age"), IntegerObject.valueOf(25));
                }})
        ));
    }

    @Test
    void indexExpression() {
        vmRunCheck(Map.of(
                "[1,2][1]", 2,
                "{'name':'ljz','age': 25}['age']", 25,
                """
                        let arr = [1,2,3]
                        let map = {"name": "Tom", "arr": arr}
                        map['arr']
                        """, "[1, 2, 3]"
        ));
    }

    @Test
    void functionLiteral() {
        vmRunCheck(Map.of(
                "let f = fn(){}; f()", Object.NULL,
                "let r = fn() {1+2};r()", 3,
                """
                        let f1 = fn() { 1+ 1}
                        let f2 = fn() { f1 }
                        f2()()
                        """, 2,
                "let oneArg = fn(a) {}; oneArg(1)", Object.NULL,
                """
                        let add = fn(a,b) {
                            a + b
                        }
                        add(1, 3)
                        """, 4,
                """
                        let global= 10;
                        let f = fn() {
                          let a = 1;
                          let b = 2;
                          a + b + global
                        }
                        f()
                        """, 13,
                """
                        let global = 10;
                        let f1 = fn(a) {
                            global + a + 1
                        }
                        let f2 = fn(a, b) {
                            f1(a) + f1(b) + 3
                        }
                        f2(1, 2)
                        """, 28,
                """
                        let f1 = fn() {}
                        f1()
                        fn f2(a) {
                            a + 1
                        }
                        f2(10)
                        """, 11
        ));
    }

    @Test
    void builtinFunction() {
        vmRunCheck(Map.of(
                "len([])", 0,
                "push([], 1, 2)", "[1, 2]"
        ));
    }

    @Test
    void closureFunction() {
        var tests = Map.of(
                """
                        let newAdder = fn(a) {
                            let adder = fn(b) {a + b}
                            adder
                        }
                        let addTwo = newAdder(10)
                        addTwo(12)
                        """, 22,
                """
                        fn f1(a) {
                            fn(b) {
                                a + b
                            }
                        }
                        f1(10)(20)
                        """, 30
        );
        vmRunCheck(tests);
    }

    private <T> void vmRunCheck(Map<String, T> tests) {
        tests.forEach((input, expected) -> {
            var program = new Parser(input).parseProgram();
            if (program.hasErrors()) {
                fail(program.getErrors().toString());
            }
            var compiler = new Compiler();
            compiler.compile(program);
            var bytecode = compiler.bytecode();
            var vm = new Vm(bytecode);
            try {
                vm.run();
            } catch (Exception e) {
                System.err.println(bytecode.toString());
                fail(e);
            }
            var result = vm.lastPopped();
            if (expected instanceof Object)
                assertEquals(expected, result, bytecode.toString());
            else
                assertEquals(expected.toString(), result.toString(), bytecode.toString());
        });
    }
}
