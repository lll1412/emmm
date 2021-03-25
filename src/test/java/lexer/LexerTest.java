package lexer;

import org.junit.jupiter.api.Test;
import token.Token;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static token.Token.*;

class LexerTest {

    @Test
    void nextToken() {
        var input = """
                let five = 5;
                let ten = 10;
                let add = fn(x, y) {
                    x + y;
                };
                let result = add(five, ten);
                """;
        var tests = List.of(
                LET, IDENT("five"), ASSIGN, INT(5), SEMICOLON,
                LET, IDENT("ten"), ASSIGN, INT(10), SEMICOLON,
                LET, IDENT("add"), ASSIGN, FUNCTION, LPAREN, IDENT("x"), COMMA, RPAREN, LBRACE, IDENT("x"), PLUS, IDENT("y"), SEMICOLON, RBRACE, SEMICOLON,
                LET, IDENT("result"), ASSIGN, IDENT("add"), LPAREN, IDENT("five"), COMMA, IDENT("ten"), RPAREN, SEMICOLON,
                EOF
        );
        var lexer = new Lexer(input);
        for (int i = 0; i < tests.size(); i++) {
            var token = lexer.nextToken();
            Token expectedToken = tests.get(i);
            assertEquals(expectedToken, token, "tests[%d] token type wrong. expected=%s, got=%s".formatted(i, expectedToken, token));
        }
    }
}