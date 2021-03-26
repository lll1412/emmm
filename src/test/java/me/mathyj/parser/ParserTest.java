package me.mathyj.parser;

import me.mathyj.ast.LetStatement;
import me.mathyj.ast.Program;
import me.mathyj.ast.ReturnStatement;
import me.mathyj.ast.Statement;
import me.mathyj.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    void letStatement() {
        var input = """
                let x = 5;
                let y = 10;
                let foobar = 12345;
                """;
        var lexer = new Lexer(input);
        var parser = new Parser(lexer);
        var program = parser.parseProgram();
        checkErrors(program);
        assertNotNull(program);
        assertEquals(3, program.size());
        var tests = List.of("x", "y", "foobar");
        for (var i = 0; i < tests.size(); i++) {
            var name = tests.get(i);
            var statement = program.statements.get(i);
            assertEquals("let", statement.tokenLiteral());
            assertInstanceOf(LetStatement.class, statement);
            assertEquals(name, ((LetStatement) statement).name.token.literal());

        }
    }

    @Test
    void returnStatement() {
        var inputs = """
                return 1;
                return 10;
                return 233;
                """;
        var parser = new Parser(inputs);
        var program = parser.parseProgram();
        checkErrors(program);
        assertEquals(3, program.size());
        for (Statement statement : program.statements) {
            assertInstanceOf(ReturnStatement.class, statement);
            assertEquals("return", statement.tokenLiteral());
        }
    }

    private void checkErrors(Program program) {
        var errors = program.errors;
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
}