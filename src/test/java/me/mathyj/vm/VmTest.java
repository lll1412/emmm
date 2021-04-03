package me.mathyj.vm;

import me.mathyj.MyMap;
import me.mathyj.compiler.Compiler;
import me.mathyj.object.Object;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class VmTest {
    // 虚拟机引擎执行算数运算
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

    // 布尔表达式执行
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

    private <T> void vmRunCheck(Map<String, T> tests) {
        tests.forEach((input, expected) -> {
            var program = new Parser(input).parseProgram();
            if (program.hasErrors()) {
                fail(program.getErrors().toString());
            }
            var compiler = new Compiler();
            compiler.compile(program);
            var vm = new Vm(compiler.bytecode());
            vm.run();
            var result = vm.lastPopped();
            if (expected instanceof Object)
                assertEquals(expected, result, input);
            else
                assertEquals(expected.toString(), result.toString(), input);
        });
    }
}
