package me.mathyj.compiler;

import me.mathyj.MyMap;
import me.mathyj.code.Bytecode;
import me.mathyj.code.Instructions;
import me.mathyj.code.Opcode;
import me.mathyj.object.CompiledFunctionObject;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.StringObject;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static me.mathyj.code.Instructions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    // 整数运算编译测试
    @Test
    void integerArithmetic() {
//        byte[][] bytes = ;
        var tests = Map.of(
                "1 + 2", new Bytecode(List.of(IntegerObject.valueOf(1), IntegerObject.valueOf(2)),
                        makeConst(0), makeConst(1), make(Opcode.ADD), make(Opcode.POP)
                ),
                "1-2", new Bytecode(List.of(IntegerObject.valueOf(1), IntegerObject.valueOf(2)),
                        makeConst(0), makeConst(1), make(Opcode.SUB), makePop()
                ),
                "2/2", new Bytecode(List.of(IntegerObject.valueOf(2), IntegerObject.valueOf(2)),
                        makeConst(0), makeConst(1), make(Opcode.DIV), makePop()
                ),
                "-2", new Bytecode(List.of(IntegerObject.valueOf(2)), makeConst(0), make(Opcode.NEG), makePop())
        );
        compileCheck(tests);
    }

    // 布尔表达式编译测试
    @Test
    void booleanExpression() {
        var tests = Map.of(
                "true", new Bytecode(List.of(), make(Opcode.TRUE), makePop()),
                "false", new Bytecode(List.of(), make(Opcode.FALSE), makePop()),
                "2>1", new Bytecode(List.of(IntegerObject.valueOf(2), IntegerObject.valueOf(1)), makeConst(0), makeConst(1), make(Opcode.GT), makePop()),
                "2==1", new Bytecode(List.of(IntegerObject.valueOf(2), IntegerObject.valueOf(1)), makeConst(0), makeConst(1), make(Opcode.EQ), makePop()),
                "2!=1", new Bytecode(List.of(IntegerObject.valueOf(2), IntegerObject.valueOf(1)), makeConst(0), makeConst(1), make(Opcode.NE), makePop()),
                "true == false", new Bytecode(List.of(), make(Opcode.TRUE), make(Opcode.FALSE), make(Opcode.EQ), makePop()),
                "true != false", new Bytecode(List.of(), make(Opcode.TRUE), make(Opcode.FALSE), make(Opcode.NE), makePop()),
                "!true", new Bytecode(List.of(), make(Opcode.TRUE), make(Opcode.NOT), makePop())
        );
        compileCheck(tests);
    }

    /**
     * 指令 字符串格式打印 测试
     */
    @Test
    void instructionsString() {
        var tests = Map.of(
                """
                        0000 ADD
                        0001 CONSTANT 2
                        0004 CONSTANT 65535
                        """,
                Instructions.concat(make(Opcode.ADD), makeConst(2), makeConst(65535)),
                """
                        0000 ADD
                        0001 GET_LOCAL 1
                        0003 CONSTANT 2
                        0006 CONSTANT 65535
                        0009 CLOSURE 65535 255
                        """,
                Instructions.concat(make(Opcode.ADD), make(Opcode.GET_LOCAL, 1), makeConst(2), makeConst(65535), make(Opcode.CLOSURE, 65535, 255))
        );
        tests.forEach((expected, input) -> assertEquals(expected, input.print()));

    }

    /**
     * 条件表达式编译测试
     */
    @Test
    void conditionExpression() {
        var tests = Map.of(

                "if(true){ 10 }", new Bytecode(List.of(IntegerObject.valueOf(10)),
                        // 0000
                        make(Opcode.TRUE),
                        // 0001
                        make(Opcode.JUMP_IF_NOT_TRUTHY, 10),
                        // 0004
                        makeConst(0),
                        // 0007
                        make(Opcode.JUMP_ALWAYS, 11),
                        // 00010
                        make(Opcode.NULL),
                        // 0011
                        makePop()),
                "if(true){ 10 } else { 20 }", new Bytecode(List.of(IntegerObject.valueOf(10), IntegerObject.valueOf(20)),
                        // 0000
                        make(Opcode.TRUE),
                        // 0001
                        make(Opcode.JUMP_IF_NOT_TRUTHY, 10),
                        // 0004
                        makeConst(0),
                        // 0007
                        make(Opcode.JUMP_ALWAYS, 13),
                        // 00010
                        makeConst(1),
                        // 0013
                        makePop()
                )
        );
        compileCheck(tests);
    }

    /**
     * let语句变量绑定编译测试
     */
    @Test
    void globalLetStatement() {
        var tests = MyMap.of(
                "let one = 1;", new Bytecode(List.of(IntegerObject.valueOf(1)), makeConst(0), make(Opcode.SET_GLOBAL)),
                "let one = 1;let two = 2", new Bytecode(
                        List.of(IntegerObject.valueOf(1), IntegerObject.valueOf(2)),
                        makeConst(0),
                        make(Opcode.SET_GLOBAL, 0),
                        makeConst(1),
                        make(Opcode.SET_GLOBAL, 1)
                ),
                "let one=1;one", new Bytecode(List.of(IntegerObject.valueOf(1)),
                        makeConst(0),
                        make(Opcode.SET_GLOBAL, 0),
                        make(Opcode.GET_GLOBAL, 0),
                        makePop()
                ),
                "let one=1;let two = one; two;", new Bytecode(List.of(IntegerObject.valueOf(1)),
                        makeConst(0),
                        make(Opcode.SET_GLOBAL, 0),
                        make(Opcode.GET_GLOBAL, 0),
                        make(Opcode.SET_GLOBAL, 1),
                        make(Opcode.GET_GLOBAL, 1),
                        makePop()
                )
        );
        compileCheck(tests);
    }

    @Test
    void localLetStatement() {
        var tests = MyMap.of(
                """
                        let num = 1;
                        fn() { num }
                        """, new Bytecode(
                        List.of(IntegerObject.valueOf(1), new CompiledFunctionObject(make(Opcode.GET_GLOBAL, 0), makeReturnValue())),
                        makeConst(0),
                        make(Opcode.SET_GLOBAL, 0),
                        makeClosure(1, 0),
                        makePop()
                ),
                """
                        fn() {
                          let num = 1;
                          num
                        }
                        """, new Bytecode(
                        List.of(IntegerObject.valueOf(1), new CompiledFunctionObject(makeConst(0), make(Opcode.SET_LOCAL, 0), make(Opcode.GET_LOCAL, 0), make(Opcode.RETURN_VALUE))),
                        makeClosure(1, 0),
                        makePop()
                ),
                """
                        fn() {
                          let a = 1;
                          let b = 2;
                          a + b
                        }
                        """, new Bytecode(
                        List.of(IntegerObject.valueOf(1), IntegerObject.valueOf(2),
                                new CompiledFunctionObject(makeConst(0), make(Opcode.SET_LOCAL, 0), makeConst(1), make(Opcode.SET_LOCAL, 1), make(Opcode.GET_LOCAL, 0), make(Opcode.GET_LOCAL, 1), make(Opcode.ADD), makeReturnValue())
                        ),
                        makeClosure(2, 0),
                        makePop()
                )
        );
        compileCheck(tests);
    }

    // 字符串字面量编译测试
    @Test
    void stringLiteral() {
        var tests = MyMap.of(
                "'hello'", new Bytecode(List.of(StringObject.valueOf("hello")), makeConst(0), makePop()),
                "'hello' + 'world'", new Bytecode(List.of(StringObject.valueOf("hello"), StringObject.valueOf("world")), makeConst(0), makeConst(1), make(Opcode.ADD), makePop())
        );
        compileCheck(tests);
    }

    // 数组字面量编译测试
    @Test
    void arrayLiteral() {
        compileCheck(MyMap.of(
                "[]", new Bytecode(List.of(), makeArray(0), makePop()),
                "[1, 2, 3]", new Bytecode(
                        List.of(IntegerObject.valueOf(1), IntegerObject.valueOf(2), IntegerObject.valueOf(3)),
                        makeConst(0),
                        makeConst(1),
                        makeConst(2),
                        makeArray(3),
                        makePop()
                )
        ));
    }

    // hash字面量编译测试
    @Test
    void hashLiteral() {
        compileCheck(MyMap.of(
                "{}", new Bytecode(List.of(), makeHash(0), makePop()),
                "{'name':'ljz','age': 25}", new Bytecode(
                        List.of(StringObject.valueOf("name"), StringObject.valueOf("ljz"), StringObject.valueOf("age"), IntegerObject.valueOf(25)),
                        makeConst(0),
                        makeConst(1),
                        makeConst(2),
                        makeConst(3),
                        makeHash(2),
                        makePop()
                )
        ));
    }

    // 索引 编译测试
    @Test
    void indexExpression() {
        compileCheck(Map.of(
                "[1,2][1]", new Bytecode(
                        List.of(IntegerObject.valueOf(1), IntegerObject.valueOf(2), IntegerObject.valueOf(1)),
                        makeConst(0),
                        makeConst(1),
                        makeArray(2),
                        makeConst(2),
                        make(Opcode.INDEX),
                        makePop()
                ),
                "{'name':'ljz','age': 25}['age']", new Bytecode(
                        List.of(StringObject.valueOf("name"), StringObject.valueOf("ljz"), StringObject.valueOf("age"), IntegerObject.valueOf(25), StringObject.valueOf("age")),
                        makeConst(0),
                        makeConst(1),
                        makeConst(2),
                        makeConst(3),
                        makeHash(2),
                        makeConst(4),
                        make(Opcode.INDEX),
                        makePop()
                )
        ));
    }

    // 函数编译测试
    @Test
    void functionLiteral() {
        compileCheck(MyMap.of(
                "fn() { 5 + 10 };", new Bytecode(
                        List.of(IntegerObject.valueOf(5), IntegerObject.valueOf(10),
                                new CompiledFunctionObject(makeConst(0), makeConst(1), make(Opcode.ADD), makeReturnValue())
                        ),
                        makeClosure(2, 0),
                        makePop()
                ),
                "fn(){}", new Bytecode(
                        List.of(new CompiledFunctionObject(makeReturn())),
                        makeClosure(0, 0),
                        makePop()
                ),
                "fn(){12}()", new Bytecode(
                        List.of(IntegerObject.valueOf(12), new CompiledFunctionObject(makeConst(0), makeReturnValue())),
                        makeClosure(1, 0),
                        makeCall(),
                        makePop()
                ),
                "let noArg = fn(){24};noArg()", new Bytecode(
                        List.of(IntegerObject.valueOf(24), new CompiledFunctionObject(makeConst(0), makeReturnValue())),
                        makeClosure(1, 0),
                        make(Opcode.SET_GLOBAL, 0),
                        make(Opcode.GET_GLOBAL, 0),
                        makeCall(),
                        makePop()
                ),
                "let oneArg = fn(a){24};", new Bytecode(
                        List.of(IntegerObject.valueOf(24), new CompiledFunctionObject(makeConst(0), makeReturnValue())),
                        makeClosure(1, 0),
                        make(Opcode.SET_GLOBAL, 0)
                ),
                """
                        fn(a) { }(1)
                        """, new Bytecode(
                        List.of(new CompiledFunctionObject(makeReturn()), IntegerObject.valueOf(1)),
                        makeClosure(0, 0),
                        makeConst(1),
                        makeCall(1),
                        makePop()
                ),
                """
                        fn(a, b) { }(1, 2)
                        """, new Bytecode(
                        List.of(new CompiledFunctionObject(makeReturn()), IntegerObject.valueOf(1), IntegerObject.valueOf(2)),
                        makeClosure(0, 0),
                        makeConst(1),
                        makeConst(2),
                        makeCall(2),
                        makePop()
                ),
                """
                        let no1 = fn() {}
                        fn nothing() {
                        }
                        """, new Bytecode(
                        List.of(new CompiledFunctionObject(makeReturn()), new CompiledFunctionObject(makeReturn())),
                        makeClosure(0, 0),
                        make(Opcode.SET_GLOBAL, 0),
                        makeClosure(1, 0),
                        make(Opcode.SET_GLOBAL, 1)

                )
        ));

    }

    @Test
    void testBuiltin() {
        compileCheck(Map.of(
                "len([])", new Bytecode(List.of(),
                        make(Opcode.GET_BUILTIN, 0),
                        makeArray(0),
                        makeCall(1),
                        makePop()
                )
        ));
    }

    @Test
    void closureFunction() {
        compileCheck(Map.of(
                """
                        fn(a) {
                            fn(b) {
                                a + b
                            }
                        }
                        """, new Bytecode(
                        List.of(new CompiledFunctionObject(make(Opcode.GET_FREE, 0), make(Opcode.GET_LOCAL, 0), make(Opcode.ADD), makeReturnValue()),
                                new CompiledFunctionObject(make(Opcode.GET_LOCAL, 0), makeClosure(0, 1), makeReturnValue())),
                        makeClosure(1, 0),
                        makePop()
                ),
                """
                        fn(a) {
                            fn(b) {
                                fn(c) {
                                    a + b + c
                                }
                            }
                        }
                        """, new Bytecode(
                        List.of(
                                new CompiledFunctionObject(make(Opcode.GET_FREE, 0), make(Opcode.GET_FREE, 1), make(Opcode.ADD), make(Opcode.GET_LOCAL), make(Opcode.ADD), makeReturnValue()),
                                new CompiledFunctionObject(make(Opcode.GET_FREE, 0), make(Opcode.GET_LOCAL, 0), makeClosure(0, 2), makeReturnValue()),
                                new CompiledFunctionObject(make(Opcode.GET_LOCAL, 0), makeClosure(1, 1), makeReturnValue())
                        ),
                        makeClosure(2, 0),
                        makePop()
                )

        ));

    }

    private <T> void compileCheck(Map<String, T> tests) {
        tests.forEach((input, expectedBytecode) -> {
            var program = new Parser(input).parseProgram();
            var compiler = new Compiler();
            compiler.compile(program);
            var actualBytecode = compiler.bytecode();
            assertEquals(expectedBytecode.toString(), actualBytecode.toString(), input);
        });
    }
}