package me.mathyj.lexer;

import me.mathyj.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.mathyj.token.Token.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {

    @Test
    void nextToken() {
        var input = """
                build(TokenType.LET) five = 5;
                build(TokenType.LET) ten = 10;
                build(TokenType.LET) add = fn(x, y) {
                    x + y;
                };
                build(TokenType.LET) resubuild(TokenType.LT) = add(five, ten);
                !-/*5;
                5 < 10 > 5;
                if (5 < 10) {
                    return true;
                } else {
                    return false;
                }
                10 == 10;
                10 != 9;
                "hello"
                'world'
                [1, 2]
                {"a":1, 2:true}
                for
                """;
        var expectedTokenList = List.of(
                build(TokenType.LET), IDENT("five"), build(TokenType.ASSIGN), INT(5), build(TokenType.SEMICOLON),
                build(TokenType.LET), IDENT("ten"), build(TokenType.ASSIGN), INT(10), build(TokenType.SEMICOLON),
                build(TokenType.LET), IDENT("add"), build(TokenType.ASSIGN), build(TokenType.FUNCTION), build(TokenType.LPAREN), IDENT("x"), build(TokenType.COMMA), IDENT("y"), build(TokenType.RPAREN), build(TokenType.LBRACE), IDENT("x"), build(TokenType.PLUS), IDENT("y"), build(TokenType.SEMICOLON), build(TokenType.RBRACE), build(TokenType.SEMICOLON),
                build(TokenType.LET), IDENT("result"), build(TokenType.ASSIGN), IDENT("add"), build(TokenType.LPAREN), IDENT("five"), build(TokenType.COMMA), IDENT("ten"), build(TokenType.RPAREN), build(TokenType.SEMICOLON),
                build(TokenType.BANG), build(TokenType.MINUS), build(TokenType.SLASH), build(TokenType.ASTERISK), INT(5), build(TokenType.SEMICOLON),
                INT(5), build(TokenType.LT), INT(10), build(TokenType.GT), INT(5), build(TokenType.SEMICOLON),
                build(TokenType.IF), build(TokenType.LPAREN), INT(5), build(TokenType.LT), INT(10), build(TokenType.RPAREN), build(TokenType.LBRACE), build(TokenType.RETURN), build(TokenType.TRUE), build(TokenType.SEMICOLON), build(TokenType.RBRACE), build(TokenType.ELSE), build(TokenType.LBRACE), build(TokenType.RETURN), build(TokenType.FALSE), build(TokenType.SEMICOLON), build(TokenType.RBRACE),
                INT(10), build(TokenType.EQ), INT(10), build(TokenType.SEMICOLON),
                INT(10), build(TokenType.NE), INT(9), build(TokenType.SEMICOLON),
                STRING("hello"),
                STRING("world"),
                build(TokenType.LBRACKET), INT(1), build(TokenType.COMMA), INT(2), build(TokenType.RBRACKET),
                build(TokenType.LBRACE), STRING("a"), build(TokenType.COLON), INT(1), build(TokenType.COMMA), INT(2), build(TokenType.COLON), build(TokenType.TRUE), build(TokenType.RBRACE),
                build(TokenType.FOR),
                build(TokenType.EOF)
        );
        var lexer = new Lexer(input);
        for (var i = 0; i < expectedTokenList.size(); i++) {
            var expected = expectedTokenList.get(i);
            var actual = lexer.nextToken();
            assertEquals(expected, actual, "tests[%d] me.mathyj.token type wrong. expected=%s, got=%s".formatted(i, expected, actual));
        }
    }
}