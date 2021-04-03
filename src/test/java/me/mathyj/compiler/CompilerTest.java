package me.mathyj.compiler;

import me.mathyj.code.Opcode;
import me.mathyj.object.IntegerObject;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static me.mathyj.compiler.Instructions.*;
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
                Instructions.concat(make(Opcode.ADD), makeConst(2), makeConst(65535))
        );
        tests.forEach((expected, input) -> {
            assertEquals(expected, input.print());
        });

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

    private <T> void compileCheck(Map<String, T> tests) {
        tests.forEach((input, expectedBytecode) -> {
            var program = new Parser(input).parseProgram();
            var compiler = new Compiler();
            compiler.compile(program);
            var actualBytecode = compiler.bytecode();
            assertEquals(expectedBytecode.toString(), actualBytecode.toString());
        });
    }
}