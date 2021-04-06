package me.mathyj.code;

import me.mathyj.compiler.Symbol;
import me.mathyj.compiler.SymbolTable;
import me.mathyj.object.Object;

import java.util.ArrayList;
import java.util.List;

public class Bytecode {
    // 函数嵌套深度
    public static final int SCOPE_MAX_DEPTH = 2048;

    public final List<Object> constantsPool;// 常量池

    private final CompilationScope[] scopes;
    // 局部变量符号表
    public SymbolTable symbolTable;
    private int scopeIndex;


    public Bytecode() {
        this(new ArrayList<>(), new SymbolTable(), new Instructions());
    }

    public Bytecode(List<Object> constantsPool, Instructions... instructions) {
        this(constantsPool, new SymbolTable(), instructions);
    }

    public Bytecode(List<Object> constantsPool, SymbolTable mainSymbolTable, Instructions... instructions) {
        this.scopes = new CompilationScope[SCOPE_MAX_DEPTH];
        this.constantsPool = constantsPool;

        var mainScope = new CompilationScope(Instructions.concat(instructions));
        this.scopes[scopeIndex] = mainScope;

        this.symbolTable = mainSymbolTable;
    }

    public CompilationScope currentScope() {
        return scopes[scopeIndex];
    }

    public Instructions currentInstructions() {
        return currentScope().instructions;
    }

    // 进入到下一层
    public void enterScope() {
        this.scopes[++scopeIndex] = new CompilationScope();
        this.symbolTable = new SymbolTable(symbolTable);
    }

    // 返回到上一层 并返回该层的指令
    public Instructions leaveScope() {
        var instructions = scopes[scopeIndex--].instructions;
        this.symbolTable = symbolTable.parent;
        return instructions;
    }

    /**
     * 改变pos处的指令的操作数
     */
    public void changeOperand(int pos, int operand) {
        var opcode = Opcode.lookup(currentInstructions().bytes[pos]);
        var newIns = Instructions.make(opcode, operand);
        currentScope().replaceInstruction(pos, newIns);
    }


    // 生成指令
    public int emit(Opcode op, int... operands) {
        var ins = Instructions.make(op, operands);// 创建指令，
        var pos = currentScope().addInstructions(ins);// 添加到指令列表，返回指令地址
        currentScope().setLastEmittedIns(op, pos);
        return pos;
    }

    // 生成常量指令
    public void emitConst(Object obj) {
        constantsPool.add(obj);
        var index = constantsPool.size() - 1;// 添加到常量池，返回索引
        emit(Opcode.CONSTANT, index);
    }

    public void emitClosure(Object fn) {
        constantsPool.add(fn);
        var index = constantsPool.size() - 1;// 添加到常量池，返回索引
        emit(Opcode.CLOSURE, index, 0);
    }

    // 生成pop指令
    public void emitPop() {
        emit(Opcode.POP);
    }

    // 生成变量设置指令
    public void storeSymbol(Symbol symbol) {
        if (symbol.scope() == Symbol.Scope.GLOBAL) {
            emit(Opcode.SET_GLOBAL, symbol.index());
        } else {
            emit(Opcode.SET_LOCAL, symbol.index());
        }
    }

    public void loadSymbol(Symbol symbol) {
        var index = symbol.index();
        switch (symbol.scope()) {
            case GLOBAL -> emit(Opcode.GET_GLOBAL, index);
            case LOCAL -> emit(Opcode.GET_LOCAL, index);
            case BUILTIN -> emit(Opcode.GET_BUILTIN, index);
        }
    }

    @Override
    public String toString() {
        return "constantsPool: %s, \ninstructions:\n%s".formatted(constantsPool, currentInstructions().print());
    }

    public int instructionsSize() {
        return currentInstructions().size();
    }


    public void removeLastInsIfPop() {
        if (lastInsIs(Opcode.POP)) removeLastIns();
    }

    public void removeLastIns() {
        currentScope().removeLastIns();
    }

    public boolean lastInsIs(Opcode op) {
        return currentScope().lastIns != null && currentScope().lastIns.opcode() == op;
    }

    public void replaceLastPopWithReturn() {
        if (lastInsIs(Opcode.POP)) {
            // 可以先删除原先的pop，再emit一个return
            // 但这里只需要改一个字节的操作数，所以直接替换，防止频繁创建数组
            var lastInsPos = currentScope().lastIns.position();
            var newIns = Instructions.makeReturnValue();
            currentScope().replaceInstruction(lastInsPos, newIns);
            currentScope().lastIns = new EmittedInstruction(Opcode.RETURN_VALUE, lastInsPos);
        }
    }


    // 用来表示每次生成的指令，保存着指令操作码和指令位置
    private record EmittedInstruction(Opcode opcode, int position) {
    }

    private static class CompilationScope {
        public Instructions instructions;// 生成的字节码指令
        private EmittedInstruction prevIns;// 上上个生成的指令
        private EmittedInstruction lastIns;// 上一个生成的指令

        CompilationScope(Instructions instructions) {
            this.instructions = instructions;
        }

        CompilationScope() {
            this(new Instructions());
        }


        void removeLastIns() {
            var len = lastIns.position();
            var newBytes = new char[len];
            // 舍弃上一条指令位置后面的字节
            for (var i = 0; i < len; i++) {
                newBytes[i] = instructions.bytes[i];
            }

            instructions.bytes = newBytes;
            lastIns = prevIns;
        }

        void setLastEmittedIns(Opcode op, int pos) {
            prevIns = lastIns;
            lastIns = new EmittedInstruction(op, pos);
        }

        /**
         * 添加指令，并返回添加的位置
         */
        int addInstructions(Instructions ins) {
            // 添加新指令时的长度，也就是当前指令在指令列表中的位置
            var posNewInstruction = instructions.size();
            instructions = Instructions.concat(instructions, ins);
            return posNewInstruction;
        }

        /**
         * 将pos处的指令替换为新指令,
         * 此方法主要是辅助替换操作数/指令码，所以指令占用的空间和原来一致，不用担心把后面的指令覆盖或越界
         */
        void replaceInstruction(int pos, Instructions newIns) {
            var bytes = newIns.bytes;
            for (var i = 0; i < bytes.length; i++) {
                this.instructions.bytes[pos + i] = bytes[i];
            }
        }
    }
}
