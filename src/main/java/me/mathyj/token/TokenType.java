package me.mathyj.token;

public enum TokenType {
    ILLEGAL,
    EOF("\0"),

    // 标识符 + 字面量
    IDENT, // add, foobar, x, y, ...
    INT, // 123456
    STRING,// "hello"

    // 操作符
    ASSIGN("="),

    PLUS("+"),
    MINUS("-"),
    ASTERISK("*"),
    SLASH("/"),

    BANG("!"),

    LT("<"),
    GT(">"),
    EQ("=="),
    NE("!="),

    // 分隔符
    COMMA(","),
    SEMICOLON(";"),
    COLON(":"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    LBRACKET("["),
    RBRACKET("]"),

    // 关键字
    FUNCTION("fn"),
    LET("let"),
    IF("if"),
    ELSE("else"),
    TRUE("true"),
    FALSE("false"),
    RETURN("return"),
    FOR("for"),

    ;

    String literal;// 字面值

    TokenType(String literal) {
        this.literal = literal;
    }
    TokenType() {
    }
}
