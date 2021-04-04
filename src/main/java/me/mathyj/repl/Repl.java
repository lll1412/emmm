package me.mathyj.repl;

import me.mathyj.compiler.Compiler;
import me.mathyj.compiler.SymbolTable;
import me.mathyj.object.Environment;
import me.mathyj.object.Object;
import me.mathyj.parser.Parser;
import me.mathyj.parser.ast.Program;
import me.mathyj.vm.Vm;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Repl {
    public static final String PROMPT = ">> ";

    public static void start(InputStream is, OutputStream os) {
        var sc = new Scanner(is);
        var out = new PrintStream(os);
        var env = new Environment();
        var constantsPool = new ArrayList<Object>();
        var globals = new Object[Vm.GLOBALS_SIZE];
        var symbolTable = new SymbolTable();
        while (true) {
            out.print(PROMPT);
            if (!sc.hasNextLine()) {
                return;
            }
            var line = sc.nextLine();
            if (line.isEmpty()) continue;
            var program = new Parser(line).parseProgram();
            if (program.hasErrors()) {
                printErrors(out, program.getErrors());
            } else {
//                var eval = program.eval(env);
//                out.println(eval.value());
                printProgram(out, program, symbolTable, constantsPool, globals);
            }
        }
    }

    public static void start() {
        start(System.in, System.out);
    }

    private static void printErrors(PrintStream out, List<String> errors) {
        out.println("  parser errors:");
        for (var error : errors) {
            out.printf("\t%s\n", error);
        }
    }

    private static void printProgram(PrintStream out, Program program, SymbolTable symbolTable, List<Object> constantsPool, Object[] globals) {
        try {
            var compiler = new Compiler(symbolTable, constantsPool);
            compiler.compile(program);
            var bytecode = compiler.bytecode();
            var vm = new Vm(bytecode, globals);
            vm.run();
            var val = vm.lastPopped();
            out.println(val);
        } catch (RuntimeException e) {
            e.printStackTrace();
            out.println("  errors:");
            out.println("  \t" + e.getMessage());
        }
    }
}
