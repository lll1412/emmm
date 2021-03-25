package me.mathyj.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static me.mathyj.token.Token.*;

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
                !-/*5;
                5 < 10 > 5;
                if (5 < 10) {
                    return true;
                } else {
                    return false;
                }
                10 == 10;
                10 != 9;
                """;
        var expectedTokenList = List.of(
                LET, IDENT("five"), ASSIGN, INT(5), SEMICOLON,
                LET, IDENT("ten"), ASSIGN, INT(10), SEMICOLON,
                LET, IDENT("add"), ASSIGN, FUNCTION, LPAREN, IDENT("x"), COMMA, IDENT("y"), RPAREN, LBRACE, IDENT("x"), PLUS, IDENT("y"), SEMICOLON, RBRACE, SEMICOLON,
                LET, IDENT("result"), ASSIGN, IDENT("add"), LPAREN, IDENT("five"), COMMA, IDENT("ten"), RPAREN, SEMICOLON,
                BANG, MINUS, SLASH, ASTERISK, INT(5), SEMICOLON,
                INT(5), LT, INT(10), GT, INT(5), SEMICOLON,
                IF, LPAREN, INT(5), LT, INT(10), RPAREN, LBRACE, RETURN, TRUE, SEMICOLON, RBRACE, ELSE, LBRACE, RETURN, FALSE, SEMICOLON, RBRACE,
                INT(10), EQ, INT(10), SEMICOLON,
                INT(10), NE, INT(9), SEMICOLON,
                EOF
        );
        var lexer = new Lexer(input);
        for (var i = 0; i < expectedTokenList.size(); i++) {
            var expected = expectedTokenList.get(i);
            var actual = lexer.nextToken();
            assertEquals(expected, actual, "tests[%d] me.mathyj.token type wrong. expected=%s, got=%s".formatted(i, expected, actual));
        }
    }
}