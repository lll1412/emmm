package me.mathyj.compiler;

import me.mathyj.ast.ASTNode;
import me.mathyj.ast.Program;
import me.mathyj.ast.expression.BinaryExpression;
import me.mathyj.ast.expression.IntegerLiteral;
import me.mathyj.code.Opcode;
import me.mathyj.object.IntegerObject;

public class Compiler {
    private final Bytecode bytecode;

    public Compiler() {
        this.bytecode = new Bytecode();
    }

    public void compile(ASTNode node) {
        if (node instanceof Program) {
            for (var statement : ((Program) node).statements) compile(statement);
        } else if (node instanceof BinaryExpression) {
            var binaryExpression = (BinaryExpression) node;
            compile(binaryExpression.left);
            compile(binaryExpression.right);
        } else if (node instanceof IntegerLiteral) {
            var integer = IntegerObject.valueOf(((IntegerLiteral) node).value);
            var index = bytecode.addConst(integer);// 添加到常量池，返回索引
            emit(Opcode.CONSTANT, index);// 生成指令 操作数是常量的索引
        }
    }

    private int emit(Opcode op, int... operands) {
        var ins = Instructions.make(op, operands);
        var pos = bytecode.addInstructions(ins);
        return pos;
    }

    // 返回编译出来的字节码
    public Bytecode bytecode() {
        return bytecode;
    }

}


