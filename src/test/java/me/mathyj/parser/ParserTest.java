package me.mathyj.parser;

import me.mathyj.ast.Program;
import me.mathyj.ast.expression.*;
import me.mathyj.ast.operator.BinaryOperator;
import me.mathyj.ast.operator.UnaryOperator;
import me.mathyj.ast.statement.BlockStatement;
import me.mathyj.ast.statement.LetStatement;
import me.mathyj.ast.statement.ReturnStatement;
import me.mathyj.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ParserTest {
    /**
     * let 语句测试
     */
    @Test
    void letStatement() {
        var input = """
                let x = 5;
                let y = true
                let foobar = y;
                """;
        var tests = Map.of(input, new Program()
                .addStatement(new LetStatement(new Identifier("x"), new IntegerLiteral(5)))
                .addStatement(new LetStatement(new Identifier("y"), BooleanLiteral.TRUE))
                .addStatement(new LetStatement(new Identifier("foobar"), new Identifier("y")))
        );
        check(tests);
    }

    /**
     * return 语句测试
     */
    @Test
    void returnStatement() {
        var input = """
                return 1;
                return 10;
                return 233;
                """;
        var expected = new Program()
                .addStatement(new ReturnStatement(new IntegerLiteral(1)))
                .addStatement(new ReturnStatement(new IntegerLiteral(10)))
                .addStatement(new ReturnStatement(new IntegerLiteral(233)));
        var tests = Map.of(input, expected);
        check(tests);
    }

    /**
     * 标识符表达式测试
     */
    @Test
    void identifierExpression() {
        var tests = Map.of(
                "foobar;", new Identifier("foobar")
        );
        check(tests);
    }

    /**
     * 整数字面量表达式测试
     */
    @Test
    void integerLiteral() {
        var tests = Map.of(
                "5", new IntegerLiteral(5)
        );
        check(tests);
    }

    /**
     * 布尔字面量表达式测试
     */
    @Test
    void booleanExpression() {
        var tests = Map.of(
                "true", new BooleanLiteral(true),
                "false;", new BooleanLiteral(false)
        );
        check(tests);
    }

    @Test
    void stringLiteral() {
        var tests = Map.of(
                "'hello'", new StringLiteral("hello"),
                "\"world\";", new StringLiteral("world")
        );
        check(tests);
    }

    @Test
    void arrayLiteral() {
        var tests = Map.of(
                "[1,2,3]", new ArrayLiteral(new IntegerLiteral(1), new IntegerLiteral(2), new IntegerLiteral(3)),
                "[]", new ArrayLiteral()
        );
        check(tests);
    }

    @Test
    void hashLiteral() {
        var tests = Map.of(
                "{}", new HashLiteral(),
                "{'a':1}", new HashLiteral(List.of(HashLiteral.Pair.of(new StringLiteral("a"), new IntegerLiteral(1))))
        );
        check(tests);
    }

    @Test
    void indexExpression() {
        var tests = Map.of(
                "[1,2,3][1]", new IndexExpression(new ArrayLiteral(new IntegerLiteral(1), new IntegerLiteral(2), new IntegerLiteral(3)), new IntegerLiteral(1)),
                "{'a':1, 2: true}['a']", new IndexExpression(
                        new HashLiteral(List.of(HashLiteral.Pair.of(new StringLiteral("a"), new IntegerLiteral(1)), HashLiteral.Pair.of(new IntegerLiteral(2), BooleanLiteral.TRUE))),
                        new StringLiteral("a"))
        );
        check(tests);
    }

    /**
     * 一元表达式测试
     */
    @Test
    void parsingUnaryExpression() {
        var tests = Map.of(
                "!5", new UnaryExpression(UnaryOperator.BANG, new IntegerLiteral(5)),
                "-15", new UnaryExpression(UnaryOperator.MINUS, new IntegerLiteral(15)),
                "++i", new AssignExpression(new Identifier("i"), new BinaryExpression(new Identifier("i"), BinaryOperator.ADD, new IntegerLiteral(1))),
                "--i", new AssignExpression(new Identifier("i"), new BinaryExpression(new Identifier("i"), BinaryOperator.SUBTRACT, new IntegerLiteral(1)))
        );
        check(tests);
    }

    /**
     * 二元表达式测试
     */
    @Test
    void parsingBinaryExpression() {
        var tests = MyMap.of(
                "5+5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.ADD, new IntegerLiteral(5)),
                "5-5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.SUBTRACT, new IntegerLiteral(5)),
                "5*5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.MULTIPLY, new IntegerLiteral(5)),
                "5/5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.DIVIDE, new IntegerLiteral(5)),
                "5<5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.LESS_THEN, new IntegerLiteral(5)),
                "5>5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.GREATER_THEN, new IntegerLiteral(5)),
                "5==5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.EQUALS, new IntegerLiteral(5)),
                "5!=5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.NOT_EQUALS, new IntegerLiteral(5)),
                "5>=5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.GREATER_EQ, new IntegerLiteral(5)),
                "5<=5", new BinaryExpression(new IntegerLiteral(5), BinaryOperator.LESS_EQ, new IntegerLiteral(5)),
                "true && false", new BinaryExpression(BooleanLiteral.TRUE, BinaryOperator.AND, BooleanLiteral.FALSE),
                "true==true", new BinaryExpression(BooleanLiteral.TRUE, BinaryOperator.EQUALS, BooleanLiteral.TRUE),
                "true!=false", new BinaryExpression(BooleanLiteral.TRUE, BinaryOperator.NOT_EQUALS, BooleanLiteral.FALSE),
                "a+=2", new AssignExpression(new Identifier("a"), TokenType.PLUS_ASSIGN, new IntegerLiteral(2)),
                "a&&b", new BinaryExpression(new Identifier("a"), BinaryOperator.AND, new Identifier("b")),
                "a||b", new BinaryExpression(new Identifier("a"), BinaryOperator.OR, new Identifier("b"))
        );

        check(tests);
    }

    /**
     * 操作符优先级测试
     */
    @Test
    void operatorPrecedence() {
        // 最多只能10个
        var tests = Map.of(
                "-a * b", "((-a) * b)",
                "!-a", "(!(-a))",
                "a+b+c", "((a + b) + c)",
                "a + b - c", "((a + b) - c)",
                "a*b/c", "((a * b) / c)",
                "a+b/c", "(a + (b / c))",
                "a+b*c+d/e-f", "(((a + (b * c)) + (d / e)) - f)",
                "3+4;-5*5", "(3 + 4)((-5) * 5)",
                "5>3==3<4", "((5 > 3) == (3 < 4))",
                "5<3!=3>4", "((5 < 3) != (3 > 4))"
        );
        check(tests);
        // 超过10个了，太多把他们分开来
        tests = Map.of(
                "3+4*5==3*1+4*5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))",
                "true", "true",
                "false", "false",
                "3>5==false", "((3 > 5) == false)",
                "3<5==true", "((3 < 5) == true)",
                "1+(2+3)+4", "((1 + (2 + 3)) + 4)",
                "(5+5)*2", "((5 + 5) * 2)"
        );
        check(tests);
    }

    /**
     * If表达式测试
     */
    @Test
    void ifExpression() {
        var tests = Map.of("""
                        if (x < y) {
                            x
                        } else {
                            y
                        }
                        """,
                new IfExpression(
                        new BinaryExpression(new Identifier("x"), BinaryOperator.LESS_THEN, new Identifier("y")),
                        new BlockStatement(List.of(new Identifier("x"))),
                        new BlockStatement(List.of(new Identifier("y")))
                )
        );
        check(tests);
    }

    /**
     * for循环表达式测试
     */
    @Test
    void forExpression() {
        var tests = Map.of(
                """
                        for(let i = 0; i < 1; i=i+1) {
                            return i;
                        }
                        """, new ForStatement()
                        .setInitial(new LetStatement(new Identifier("i"), new IntegerLiteral(0)))
                        .setCondition(new BinaryExpression(new Identifier("i"), BinaryOperator.LESS_THEN, new IntegerLiteral(1)))
                        .setLast(new AssignExpression(new Identifier("i"), new BinaryExpression(new Identifier("i"), BinaryOperator.ADD, new IntegerLiteral(1))))
                        .setBlock(new BlockStatement(List.of(new ReturnStatement(new Identifier("i")))))
        );
        check(tests);
    }

    /**
     * 函数表达式测试
     */
    @Test
    void FunctionExpression() {
        var x = new Identifier("x");
        var y = new Identifier("y");
        var tests = Map.of(
                """
                        fn (x, y) {
                            x + y
                        }
                        """,
                new FunctionLiteral(List.of(x, y),
                        new BlockStatement(List.of(new BinaryExpression(x, BinaryOperator.ADD, y)))),
                "fn(){}", new FunctionLiteral(),
                "fn(x){}", new FunctionLiteral(List.of(new Identifier("x")))
        );
        check(tests);
    }

    /**
     * 函数调用测试
     */
    @Test
    void callExpression() {
        var tests = Map.of(
                "add(1, 3)", new CallExpression(new Identifier("add"), List.of(new IntegerLiteral(1), new IntegerLiteral(3))),
                "a + add(b * c) + d", "((a + add((b * c))) + d)",
                "add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))", "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))",
                "add(a + b + c * d / f + g)", "add((((a + b) + ((c * d) / f)) + g))"
        );
        check(tests);
    }


    private <T> void check(Map<String, T> tests) {
        tests.forEach((input, expected) -> {
            var program = new Parser(input).parseProgram();
            checkErrors(program);
            assertEquals(expected.toString(), program.toString());
        });
    }

    private void checkErrors(Program program) {
        var errors = program.getErrors();
        if (errors.isEmpty()) return;
        StringBuilder msg = new StringBuilder();
        var errorsCount = "parser has %d errors.\n".formatted(errors.size());
        msg.append(errorsCount);
        for (var error : errors) {
            var m = "parser error: %s.\n".formatted(error);
            msg.append(m);
        }
        fail(msg.toString());
    }


    // 因为 Map.of(..) 最多只支持10个键值对，所以这里自己封装一下
    public static class MyMap<V> extends LinkedHashMap<String, V> {
        public static MyMap<Object> of(java.lang.Object... els) {
            assert els != null && els.length % 2 == 0;
            var myMap = new MyMap<>();
            for (int i = 0; i < els.length; i += 2) {
                myMap.put(String.valueOf(els[i]), els[i + 1]);
            }
            return myMap;
        }
    }

}