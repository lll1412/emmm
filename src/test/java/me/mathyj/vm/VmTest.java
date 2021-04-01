package me.mathyj.vm;

import me.mathyj.compiler.Compiler;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class VmTest {
    @Test
    void integerArithmetic() {
        var tests = Map.of(
                "1", 1,
                "2", 2,
                "1+2", 2
        );
        vmCheck(tests);
    }

    private <T> void vmCheck(Map<String, T> tests) {
        tests.forEach((input, expected) -> {
            var program = new Parser(input).parseProgram();
            if (program.hasErrors()) {
                fail(program.getErrors().toString());
            }
            var compiler = new Compiler();
            compiler.compile(program);
            var vm = new Vm(compiler.bytecode());
            vm.run();
            var result = vm.stackPop();
            assertEquals(expected, result);
        });
    }
}
