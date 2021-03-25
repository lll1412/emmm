package me.mathyj.token;

public enum TokenType {
    ILLEGAL("ILLEGAL"),
    EOF("\0"),

    // 标识符 + 字面量
    IDENT("IDENT"), // add, foobar, x, y, ...
    INT("INT"), // 123456

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

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // 关键字
    FUNCTION("fn"),
    LET("let"),
    IF("if"),
    ELSE("else"),
    TRUE("true"),
    FALSE("false"),
    RETURN("return"),

    ;

    String literal;// 字面值

    TokenType(String literal) {
        this.literal = literal;
    }
}
