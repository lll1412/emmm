package me.mathyj.compiler;

import me.mathyj.object.IntegerObject;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static me.mathyj.compiler.Instructions.makeConst;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    // 整数运算编译测试
    @Test
    void integerArithmetic() {
//        byte[][] bytes = ;
        var tests = Map.of(
                "1 + 2", new Bytecode(List.of(IntegerObject.valueOf(1), IntegerObject.valueOf(2)), makeConst(0), makeConst(1))
        );
        compileCheck(tests);
    }

    @Test
    void instructionsString() {
        var tests = Map.of(
                """
                        0000 CONSTANT 1
                        0003 CONSTANT 2
                        0006 CONSTANT 65535
                        """,
                Instructions.concat(makeConst(1), makeConst(2), makeConst(65535))
        );
        tests.forEach((expected, input) -> {
            assertEquals(expected, input.print());
        });

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