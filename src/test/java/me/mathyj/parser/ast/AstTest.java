package me.mathyj.parser.ast;
import me.mathyj.parser.ast.expression.Identifier;
import me.mathyj.parser.ast.statement.LetStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AstTest {
    @Test
    void string() {
        var program = new Program().addStatement(new LetStatement(new Identifier("myVar"), new Identifier("anotherVar")));

        assertEquals("let myVar = anotherVar;", program.toString());
    }
}
