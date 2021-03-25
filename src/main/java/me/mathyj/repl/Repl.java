package me.mathyj.repl;

import me.mathyj.lexer.Lexer;
import me.mathyj.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Repl {
    public static final String PROMPT = ">> ";

    public static void start(InputStream is, OutputStream os) {
        var sc = new Scanner(is);
        var out = new PrintStream(os);
        while (true) {
            out.print(PROMPT);
            if (!sc.hasNextLine()) {
                return;
            }
            var line = sc.nextLine();
            var lexer = new Lexer(line);
            var tk = lexer.nextToken();
            while (tk != Token.EOF) {
                out.printf("%s\n", tk);
                tk = lexer.nextToken();
            }
        }
    }

    public static void start() {
        start(System.in, System.out);
    }
}
