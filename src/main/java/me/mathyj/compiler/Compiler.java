package me.mathyj.compiler;

import me.mathyj.code.Bytecode;
import me.mathyj.code.Opcode;
import me.mathyj.exception.runtime.UndefinedVariable;
import me.mathyj.object.CompiledFunctionObject;
import me.mathyj.object.IntegerObject;
import me.mathyj.object.Object;
import me.mathyj.object.StringObject;
import me.mathyj.parser.ast.Program;
import me.mathyj.parser.ast.expression.*;
import me.mathyj.parser.ast.statement.BlockStatement;
import me.mathyj.parser.ast.statement.LetStatement;
import me.mathyj.parser.ast.statement.ReturnStatement;
import me.mathyj.parser.ast.statement.Statement;

import java.util.List;

public class Compiler {
    private final Bytecode bytecode;

    public Compiler(SymbolTable symbolTable, List<Object> constantsPool) {
        this.bytecode = new Bytecode(constantsPool, symbolTable);
    }

    public Compiler() {
        this.bytecode = new Bytecode();
    }

    public void compile(Program program) {
        compile(program.statements);
    }

    private void compile(Statement node) {
        if (node instanceof BlockStatement blockStatement) {
            compile(blockStatement);
        } else if (node instanceof LetStatement letStatement) {
            var symbol = bytecode.symbolTable.define(letStatement.name());
            compile(letStatement.value);
            bytecode.storeSymbol(symbol);
        } else if (node instanceof ReturnStatement returnStatement) {
            var returnValue = returnStatement.returnValue;
            compile(returnValue);
        } else if (node instanceof Expression) {
            compile(((Expression) node));
            if (node instanceof FunctionLiteral fl && fl.identifier != null) {
                // 这是函数申明，不算表达式，不pop
                return;
            }
            bytecode.emitPop();
        }
    }

    private void compile(Expression node) {
        if (node instanceof BinaryExpression binaryExpression) {
            var operator = binaryExpression.operator;
            compile(binaryExpression.left);
            compile(binaryExpression.right);
            var op = Opcode.from(operator);
            bytecode.emit(op);
        } else if (node instanceof IntegerLiteral integerLiteral) {
            var integer = IntegerObject.valueOf(integerLiteral.value);
            bytecode.emitConst(integer);
        } else if (node instanceof BooleanLiteral booleanLiteral) {
            var op = Opcode.from(booleanLiteral);
            bytecode.emit(op);
        } else if (node instanceof UnaryExpression unaryExpression) {
            var operator = unaryExpression.operator;
            compile(unaryExpression.right);
            var op = Opcode.from(operator);
            bytecode.emit(op);
        } else if (node instanceof IfExpression ifExpression) {
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
        } else if (node instanceof Identifier identifier) {
            var value = identifier.value;
            var symbol = bytecode.symbolTable.resolve(value);
            if (symbol == null) throw new UndefinedVariable(value);
            bytecode.loadSymbol(symbol);
        } else if (node instanceof StringLiteral stringLiteral) {
            var value = StringObject.valueOf(stringLiteral.val);
            bytecode.emitConst(value);
        } else if (node instanceof ArrayLiteral arrayLiteral) {
            for (var element : arrayLiteral.elements) {
                compile(element);
            }
            bytecode.emit(Opcode.ARRAY, arrayLiteral.elements.size());
        } else if (node instanceof HashLiteral hashLiteral) {
            for (var pair : hashLiteral.pairs) {
                compile(pair.key());
                compile(pair.val());
            }
            bytecode.emit(Opcode.HASH, hashLiteral.pairs.size());
        } else if (node instanceof IndexExpression indexExpression) {
            compile(indexExpression.left);
            compile(indexExpression.index);
            bytecode.emit(Opcode.INDEX);
        } else if (node instanceof FunctionLiteral functionLiteral) {
            Symbol symbol = null;
            if (functionLiteral.identifier != null) {
                // let xx = fn(){} 这类的函数申明 在 let语句处把函数名注册到符号表了
                // 这里的话，是 fn xx() {}这类带名字的函数，需要自己注册
                symbol = bytecode.symbolTable.define(functionLiteral.identifier.value);
            }
            bytecode.enterScope();
            if (functionLiteral.identifier != null) {
                bytecode.symbolTable.defineFunction(functionLiteral.identifier.value);
            }
            var params = functionLiteral.params;
            for (var param : params) {
                bytecode.symbolTable.define(param.value);
            }

            compile(functionLiteral.body);

            // 如果没有显示声明return，则新增一个
            if (bytecode.lastInsIs(Opcode.POP)) {
                bytecode.replaceLastPopWithReturn();
            }
            if (!bytecode().lastInsIs(Opcode.RETURN_VALUE)) {
                bytecode.emit(Opcode.RETURN);
            }
            var freeSymbols = bytecode.symbolTable.freeSymbols;
            var numLocals = bytecode.symbolTable.numDefinitions();
            var instructions = bytecode.leaveScope();
            for (var freeSymbol : freeSymbols) {
                bytecode.loadSymbol(freeSymbol);
            }
            var compiledFunctionObject = new CompiledFunctionObject(numLocals, params.size(), instructions);
            bytecode.emitClosure(compiledFunctionObject, freeSymbols.size());
            if (symbol != null) {
                bytecode.storeSymbol(symbol);
            }
        } else if (node instanceof CallExpression callExpression) {
            compile(callExpression.left);
            var arguments = callExpression.arguments;
            for (var argument : arguments) {
                compile(argument);
            }
            bytecode.emit(Opcode.CALL, arguments.size());

        }
    }

    private void compile(List<Statement> statements) {
        if (statements == null) return;
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


