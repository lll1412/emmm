import me.mathyj.repl.Repl;

public class Main {
    public static void main(String[] args) {
        String username = System.getenv("username");
        System.out.printf("Hello %s! This is the Jo programming language!\n", username);
        System.out.println("Feel free to type in commands.");
        Repl.start();
    }
}
