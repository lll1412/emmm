package me.mathyj.compiler;

import me.mathyj.ast.ASTNode;
import me.mathyj.ast.Program;
import me.mathyj.ast.expression.BinaryExpression;
import me.mathyj.ast.expression.BooleanLiteral;
import me.mathyj.ast.expression.IntegerLiteral;
import me.mathyj.code.Opcode;
import me.mathyj.exception.compile.UnknownOperatorException;
import me.mathyj.object.IntegerObject;

public class Compiler {
    private final Bytecode bytecode;

    public Compiler() {
        this.bytecode = new Bytecode();
    }

    public void compile(ASTNode node) {
        if (node instanceof Program) {
            var program = (Program) node;
            for (var statement : program.statements) {
                compile(statement);
            }
            bytecode.emit(Opcode.POP);
        } else if (node instanceof BinaryExpression) {
            var binaryExpression = (BinaryExpression) node;
            var operator = binaryExpression.operator;
            compile(binaryExpression.left);
            compile(binaryExpression.right);
            switch (operator) {
                case ADD -> bytecode.emit(Opcode.ADD);
                case SUBTRACT -> bytecode.emit(Opcode.SUB);
                case MULTIPLY -> bytecode.emit(Opcode.MUL);
                case DIVIDE -> bytecode.emit(Opcode.DIV);
                default -> throw new UnknownOperatorException(operator);
            }
        } else if (node instanceof IntegerLiteral) {
            var integerLiteral = (IntegerLiteral) node;
            var integer = IntegerObject.valueOf(integerLiteral.value);
            bytecode.emitConst(integer);
        } else if (node instanceof BooleanLiteral) {
            var booleanLiteral = (BooleanLiteral) node;
            if (booleanLiteral.equals(BooleanLiteral.TRUE)) {
                bytecode.emit(Opcode.TRUE);
            } else {
                bytecode.emit(Opcode.FALSE);
            }
        }
    }


    // 返回编译出来的字节码
    public Bytecode bytecode() {
        return bytecode;
    }

}


