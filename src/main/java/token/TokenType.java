package token;

public enum TokenType {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // 标识符 + 字面量
    IDENT("IDENT"), // add, foobar, x, y, ...
    INT("INT"), // 123456

    // 操作符
    ASSIGN("="),
    PLUS("+"),

    // 分隔符
    COMMA(","),
    SEMICOLON(";"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // 关键字
    FUNCTION("FUNCTION"),
    LET("LET"),

    ;

    String literal;// 字面值

    TokenType(String literal) {
        this.literal = literal;
    }
}
