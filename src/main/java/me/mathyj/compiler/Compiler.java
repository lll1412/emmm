package me.mathyj.compiler;

import me.mathyj.ast.ASTNode;

public class Compiler {
    private final Bytecode bytecode;

    public Compiler() {
        this.bytecode = new Bytecode();
    }

    public void compile(ASTNode node) {
        // todo
    }

    // 返回编译出来的字节码
    public Bytecode bytecode() {
        return bytecode;
    }

}


