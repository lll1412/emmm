package me.mathyj.eval;

import me.mathyj.ast.expression.BinaryExpression;
import me.mathyj.ast.expression.Identifier;
import me.mathyj.ast.expression.IntegerLiteral;
import me.mathyj.ast.operator.BinaryOperator;
import me.mathyj.ast.operator.UnaryOperator;
import me.mathyj.ast.statement.BlockStatement;
import me.mathyj.exception.UnsupportedArgumentException;
import me.mathyj.exception.WrongArgumentsCount;
import me.mathyj.exception.eval.TypeMismatchException;
import me.mathyj.exception.eval.UnknownOperatorException;
import me.mathyj.object.Object;
import me.mathyj.object.*;
import me.mathyj.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class EvaluatorTest {
    /**
     * 整数求值测试
     */
    @Test
    void integerObject() {
        var tests = Map.of(
                "5", IntegerObject.valueOf(5),
                "10", IntegerObject.valueOf(10)
        );
        check(tests);
    }

    /**
     * 布尔求值测试
     */
    @Test
    void booleanObject() {
        var tests = Map.of(
                "true", Object.TRUE,
                "false", Object.FALSE
        );
        check(tests);
    }

    @Test
    void stringObject() {
        var tests = Map.of(
                "'hello'", new StringObject("hello"),
                "\"world\"", new StringObject("world")
        );
        check(tests);
    }

    @Test
    void arrayObject() {
        var tests = Map.of(
                "[1,2,3]", new ArrayObject(IntegerObject.valueOf(1), IntegerObject.valueOf(2), IntegerObject.valueOf(3)),
                "['hello',2+2, fn(){}]", new ArrayObject(StringObject.valueOf("hello"), IntegerObject.valueOf(4), new FunctionObject())
        );
        check(tests);

    }

    @Test
    void hashObject() {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        map.put(StringObject.valueOf("a"), IntegerObject.valueOf(1));
        map.put(IntegerObject.valueOf(4), BooleanObject.TRUE);
        var tests = Map.of(
                "{}", new HashObject(),
                "{'a':1, 4: true}", new HashObject(map)
        );
        check(tests);
    }

    /**
     * 一元表达式求值测试
     */
    @Test
    void unaryExpression() {
        var tests = Map.of(
                "!true", Object.FALSE,
                "!false", Object.TRUE,
                "!5", Object.FALSE,

                "!!true", Object.TRUE,
                "!!false", Object.FALSE,
                "!!5", Object.TRUE,

                "-5", IntegerObject.valueOf(-5),
                "-10", IntegerObject.valueOf(-10)

        );
        check(tests);
    }

    /**
     * 二元表达式求值测试
     */
    @Test
    void binaryExpression() {
        var tests = MyMap.of(
                "5+5", IntegerObject.valueOf(10),
                "5-5", IntegerObject.valueOf(0),
                "5*5", IntegerObject.valueOf(25),
                "5/5", IntegerObject.valueOf(1),
                "5>5", Object.FALSE,
                "5<5", Object.FALSE,
                "5==5", Object.TRUE,
                "5!=5", Object.FALSE,
                "(1 < 2) == true", Object.TRUE,
                "(1 > 2) != false", Object.FALSE,
                "'hello'+' world'", new StringObject("hello world"),
                "'hello'-'world'", new UnknownOperatorException(new StringObject("hello"), BinaryOperator.SUBTRACT, new StringObject("world"))
        );
        check(tests);
    }

    @Test
    void returnStatement() {
        var tests = MyMap.of(
                "return 1;", IntegerObject.valueOf(1),
                "if(true) {1+1;return 3; 2+2}", IntegerObject.valueOf(3),
                """
                        if(10>1) {
                            if(2> 1) {
                                return 10;
                            }
                            return 1;
                        }
                        """, IntegerObject.valueOf(10)
        );
        check(tests);
    }

    @Test
    void ifExpression() {
        var tests = MyMap.of(
                "if(true) {10}", IntegerObject.valueOf(10),
                "if(false) {10}", Object.NULL,
                "if(1) {10}", IntegerObject.valueOf(10),
                "if(1 < 2) {10}", IntegerObject.valueOf(10),
                "if(1 > 2) {10}", Object.NULL,
                "if(1>2) { 10 } else {20}", IntegerObject.valueOf(20)
        );
        check(tests);
    }

    @Test
    void testExceptionHandle() {
        var tests = MyMap.of(
                "5+true;5", new TypeMismatchException(IntegerObject.valueOf(5), BinaryOperator.ADD, Object.TRUE),
                "-true", new UnknownOperatorException(UnaryOperator.MINUS, Object.TRUE),
                "if(1<2) {true+false}", new UnknownOperatorException(Object.TRUE, BinaryOperator.ADD, Object.FALSE)
        );
        check(tests);
    }

    @Test
    void letStatement() {
        var tests = MyMap.of(
                "let a = 5; a", 5,
                "let a = 5 * 5; a", 25,
                "let a = 5;let b =a; b", 5,
                "let a = 5;let b = a; let c = a + b + 5;c", 15,
                "let s = 'hello';s", "hello"
        );
        check(tests);
    }

    @Test
    void forStatement() {
        var tests = Map.of(
                """
                        let sum = 0;
                        for(let i=0; i< 10; i = i + 1) {
                            sum = sum + i;
                        }
                        sum
                        """, 45
        );
        check(tests);
    }

    @Test
    void functionExpression() {
        var tests = Map.of(
                "fn(x){x+2}", new FunctionObject(
                        List.of(new Identifier("x")),
                        new BlockStatement(List.of(new BinaryExpression(new Identifier("x"), BinaryOperator.ADD, new IntegerLiteral(2))))
                        , null),
                "let identity = fn(x){return x; 1};identity(5)", 5,
                """
                        let newAdder = fn(x) {
                            fn(y) {x + y}
                        }
                        let addTwo = newAdder(2);
                        addTwo(2);
                        """, 4
        );
        check(tests);
    }

    @Test
    void indexExpression() {
        var tests = Map.of(
                "[1,2,3][1]", 2,
                "let arr = [1,2,3];arr[1]", 2,
                "let arr = [1,2,3];let i = 2; arr[i]", 3,
                "let h = {'a':22,'b':false}; h['b']", false
        );
        check(tests);
    }

    @Test
    void builtinFunction() {
        var tests = Map.of(
                "len('hello')", 5,
                "len(123)", new UnsupportedArgumentException(ObjectType.INTEGER, "len"),
                "len('one','two')", new WrongArgumentsCount(1, 2, "len")
        );
        check(tests);
    }

    /**
     * 辅助方法
     */
    private <T> void check(Map<String, T> tests) {
        tests.forEach((input, expected) -> {
            var program = new Parser(input).parseProgram();
            try {
                var env = new Environment();
                var evalResult = program.eval(env);
                if (expected instanceof Object) {
                    assertEquals(((Object) expected).value(), evalResult.value());
                } else {
                    assertEquals(expected.toString(), evalResult.toString());
                }
            } catch (RuntimeException e) {
                if (expected instanceof RuntimeException) {
                    assertEquals(((RuntimeException) expected).getMessage(), e.getMessage());
                } else {
                    fail(e);
                }
            }
        });
    }

    // 因为 Map.of(..) 最多只支持10个键值对，所以这里自己封装一下
    private static class MyMap<V> extends LinkedHashMap<String, V> {
        public static MyMap<java.lang.Object> of(java.lang.Object... els) {
            assert els != null && els.length % 2 == 0;
            var myMap = new MyMap<>();
            for (int i = 0; i < els.length; i += 2) {
                myMap.put(String.valueOf(els[i]), els[i + 1]);
            }
            return myMap;
        }
    }
}