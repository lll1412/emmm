package me.mathyj.ast;
import me.mathyj.ast.expression.Identifier;
import me.mathyj.ast.statement.LetStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AstTest {
    @Test
    void string() {
        var program = new Program(List.of(
                new LetStatement(new Identifier("myVar"), new Identifier("anotherVar"))
        ));

        assertEquals("let myVar = anotherVar;", program.toString());
    }
}
