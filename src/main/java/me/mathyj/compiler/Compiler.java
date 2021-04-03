package me.mathyj.compiler;

import me.mathyj.exception.runtime.UndefinedVariable;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;
import me.mathyj.object.StringObject;
import me.mathyj.parser.ast.Program;
import me.mathyj.parser.ast.expression.*;
import me.mathyj.parser.ast.statement.BlockStatement;
import me.mathyj.parser.ast.statement.LetStatement;
import me.mathyj.parser.ast.statement.Statement;

import java.util.List;

public class Compiler {
    private final Bytecode bytecode;
    private final SymbolTable symbolTable;

    public Compiler(SymbolTable symbolTable, List<Object> constantsPool) {
        this.symbolTable = symbolTable;
        this.bytecode = new Bytecode(constantsPool);
    }

    public Compiler() {
        this.symbolTable = new SymbolTable();
        this.bytecode = new Bytecode();
    }

    public void compile(Program program) {
        compile(program.statements);
    }

    private void compile(Statement node) {
        if (node instanceof BlockStatement) {
            var blockStatement = (BlockStatement) node;
            compile(blockStatement);
        } else if (node instanceof LetStatement) {
            var letStatement = (LetStatement) node;
            compile(letStatement.value);
            var symbol = symbolTable.define(letStatement.name());
            bytecode.emit(Opcode.SET_GLOBAL, symbol.index());
        } else if (node instanceof Expression) {
            compile(((Expression) node));
            bytecode.emitPop();
        }
    }

    private void compile(Expression node) {
        if (node instanceof BinaryExpression) {
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
        } else if (node instanceof Identifier) {
            var identifier = (Identifier) node;
            var value = identifier.value;
            var symbol = symbolTable.resolve(value);
            if (symbol == null) throw new UndefinedVariable(value);
            bytecode.emit(Opcode.GET_GLOBAL, symbol.index());
        } else if (node instanceof StringLiteral) {
            var stringLiteral = (StringLiteral) node;
            var value = StringObject.valueOf(stringLiteral.val);
            bytecode.emitConst(value);
        } else if (node instanceof ArrayLiteral) {
            var arrayLiteral = (ArrayLiteral) node;
            for (var element : arrayLiteral.elements) {
                compile(element);
            }
            bytecode.emit(Opcode.ARRAY, arrayLiteral.elements.size());
        } else if (node instanceof HashLiteral) {
            var hashLiteral = (HashLiteral) node;
            for (var pair : hashLiteral.pairs) {
                compile(pair.key);
                compile(pair.val);
            }
            bytecode.emit(Opcode.HASH, hashLiteral.pairs.size());
        } else if (node instanceof IndexExpression) {
            var indexExpression = (IndexExpression) node;
            compile(indexExpression.left);
            compile(indexExpression.index);
            bytecode.emit(Opcode.INDEX);
        }
    }

    private void compile(List<Statement> statements) {
        for (var statement : statements) {
            compile(statement);
        }
    }

    private void compile(BlockStatement blockStatement) {
        if (blockStatement == null) return;
        compile(blockStatement.statements);
    }

    // 返回编译出来的字节码
    public Bytecode bytecode() {
        return bytecode;
    }

}


