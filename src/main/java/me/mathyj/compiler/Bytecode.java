package me.mathyj.compiler;

import me.mathyj.code.Opcode;
import me.mathyj.object.Object;

import java.util.ArrayList;
import java.util.List;

public class Bytecode {
    public List<Object> constantsPool;// 常量池
    public Instructions instructions;// 生成的字节码指令
    public EmittedInstruction prevIns;// 上上个生成的指令
    public EmittedInstruction lastIns;// 上一个生成的指令

    public Bytecode(List<Object> constantsPool, Instructions... instructions) {
        this.constantsPool = constantsPool;
        this.instructions = Instructions.concat(instructions);
    }

    public Bytecode() {
        this.constantsPool = new ArrayList<>();
        this.instructions = new Instructions();
    }

    /**
     * 改变pos处的指令的操作数
     */
    public void changeOperand(int pos, int operand) {
        var opcode = Opcode.lookup(instructions.bytes[pos]);
        var newIns = Instructions.make(opcode, operand);
        replaceInstruction(pos, newIns);
    }


    // 生成指令
    public int emit(Opcode op, int... operands) {
        var ins = Instructions.make(op, operands);// 创建指令，
        var pos = this.addInstructions(ins);// 添加到指令列表，返回指令地址
        setLastEmittedIns(op, pos);
        return pos;
    }

    // 生成常量指令
    public int emitConst(Object obj) {
        var index = addConst(obj);// 添加到常量池，返回索引
        return emit(Opcode.CONSTANT, index);
    }

    // 生成pop指令
    public int emitPop() {
        return emit(Opcode.POP);
    }

    @Override
    public String toString() {
        return "constantsPool: %s, \ninstructions:\n%s".formatted(constantsPool, instructions.print());
    }


    public void removeLastInsIfPop() {
        if (lastIns.opcode() == Opcode.POP) removeLastIns();
    }

    public int instructionsSize() {
        return instructions.size();
    }

    private void removeLastIns() {
        var len = lastIns.position();
        var newBytes = new char[len];
        // 舍弃上一条指令位置后面的字节
        for (var i = 0; i < len; i++) {
            newBytes[i] = instructions.bytes[i];
        }
        instructions.bytes = newBytes;
    }

    private void setLastEmittedIns(Opcode op, int pos) {
        prevIns = lastIns;
        lastIns = new EmittedInstruction(op, pos);
    }

    /**
     * 添加常量，并返回在常量池中的索引
     */
    private int addConst(Object constant) {
        constantsPool.add(constant);
        return constantsPool.size() - 1;
    }

    /**
     * 添加指令，并返回添加的位置
     */
    private int addInstructions(Instructions ins) {
        // 添加新指令时的长度，也就是当前指令在指令列表中的位置
        var posNewInstruction = instructions.size();
        instructions = Instructions.concat(instructions, ins);
        return posNewInstruction;
    }

    /**
     * 将pos处的指令零替换为新指令
     */
    private void replaceInstruction(int pos, Instructions newIns) {
        var bytes = newIns.bytes;
        for (var i = 0; i < bytes.length; i++) {
            this.instructions.bytes[pos + i] = bytes[i];
        }
    }

    // 用来表示每次生成的指令，保存着指令操作码和指令位置
    public record EmittedInstruction(Opcode opcode, int position) {
    }
}
