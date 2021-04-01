package me.mathyj.compiler;

import me.mathyj.code.Opcode;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    // 整数运算测试
    @Test
    void integerArithmetic() {
//        byte[][] bytes = ;
        var tests = Map.of(
                "1 + 2", new Bytecode(new Object[]{IntegerObject.valueOf(1), IntegerObject.valueOf(2)}, Instructions.make(Opcode.CONSTANT), Instructions.make(Opcode.CONSTANT))
        );
        compileCheck(tests);
    }

    private <T> void compileCheck(Map<String, T> tests) {
        tests.forEach((input, expectedBytecode) -> {
            var program = new Parser(input).parseProgram();
            var compiler = new Compiler();
            compiler.compile(program);
            var actualBytecode = compiler.bytecode();
            assertEquals(expectedBytecode, actualBytecode);
        });
    }

}