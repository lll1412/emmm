package me.mathyj.compiler;

import me.mathyj.parser.ast.ASTNode;
import me.mathyj.parser.ast.Program;
import me.mathyj.parser.ast.expression.*;
import me.mathyj.parser.ast.statement.BlockStatement;
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
                bytecode.emitPop();
            }
        } else if (node instanceof BlockStatement) {
            var blockStatement = (BlockStatement) node;
            for (var statement : blockStatement.statements) {
                compile(statement);
            }
            bytecode.emitPop();
        } else if (node instanceof BinaryExpression) {
            var binaryExpression = (BinaryExpression) node;
            var operator = binaryExpression.operator;
            compile(binaryExpression.left);
            compile(binaryExpression.right);
            var op = Opcode.from(operator);
            bytecode.emit(op);
        } else if (node instanceof IntegerLiteral) {
            var integerLiteral = (IntegerLiteral) node;
            var integer = IntegerObject.valueOf(integerLiteral.value);
            bytecode.emitConst(integer);
        } else if (node instanceof BooleanLiteral) {
            var booleanLiteral = (BooleanLiteral) node;
            var op = Opcode.from(booleanLiteral);
            bytecode.emit(op);
        } else if (node instanceof UnaryExpression) {
            var unaryExpression = (UnaryExpression) node;
            var operator = unaryExpression.operator;
            compile(unaryExpression.right);
            var op = Opcode.from(operator);
            bytecode.emit(op);
        } else if (node instanceof IfExpression) {
            var ifExpression = (IfExpression) node;
            compile(ifExpression.condition);
            var jumpIfOpPos = bytecode.emit(Opcode.JUMP_IF_NOT_TRUTHY, 0);// 随便设置一个值，根据后续指令调整
            compile(ifExpression.consequence);
            // 因为if是个表达式，所以有返回值，
            // 但是每个语句后面都生成了一个pop指令，这样的话返回值就没了
            // 所以把 if/else 语句块里的pop给删掉
            bytecode.removeLastInsIfPop();
            var alternative = ifExpression.alternative;
            var jumpAlwaysOpPos = bytecode.emit(Opcode.JUMP_ALWAYS, 0);
            // 矫正jump_if指令的跳转位置
            var afterConsequence = bytecode.instructionsSize();
            bytecode.changeOperand(jumpIfOpPos, afterConsequence);
            if (alternative == null) {// 没有else语句的话，用null代替
                bytecode.emit(Opcode.NULL);
            } else {
                compile(alternative);
                bytecode.removeLastInsIfPop();
            }
            // 矫正jump_always指令的跳转位置
            var afterAlternative = bytecode.instructionsSize();
            bytecode.changeOperand(jumpAlwaysOpPos, afterAlternative);// 调整为正确的跳转地址
        }
    }

    // 返回编译出来的字节码
    public Bytecode bytecode() {
        return bytecode;
    }

}


