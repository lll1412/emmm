package me.mathyj.repl;

import me.mathyj.ast.Program;
import me.mathyj.exception.eval.EvalException;
import me.mathyj.object.Environment;
import me.mathyj.parser.Parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Repl {
    public static final String PROMPT = ">> ";

    public static void start(InputStream is, OutputStream os) {
        var sc = new Scanner(is);
        var out = new PrintStream(os);
        var env = new Environment();
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
                printProgram(out, program, env);
            }
        }
    }

    public static void start() {
        start(System.in, System.out);
    }

    private static void printErrors(PrintStream out, List<String> errors) {
        out.println("  parser errors:");
        for (String error : errors) {
            out.printf("\t%s\n", error);
        }
    }

    private static void printProgram(PrintStream out, Program program, Environment env) {
        try {
            var eval = program.eval(env);
            out.println(eval.value());
        } catch (EvalException e) {
            out.println("  eval errors:");
            out.println(e.getMessage());
        }
    }
}
