package me.mathyj.object;

import me.mathyj.exception.UnsupportedArgumentException;
import me.mathyj.exception.WrongArgumentsCount;
import me.mathyj.parser.Parser;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BuiltinObject implements Object {
    private static final BuiltinObject len = new BuiltinObject(lenFn());
    private static final BuiltinObject first = new BuiltinObject(firstFn());
    private static final BuiltinObject last = new BuiltinObject(lastFn());
    private static final BuiltinObject push = new BuiltinObject(pushFn());
    private static final BuiltinObject print = new BuiltinObject(printFn());
    private static final BuiltinObject eval = new BuiltinObject(evalFn());
    public static Map<String, Object> builtins = Map.of(
            "len", len,
            "first", first,
            "last", last,
            "push", push,
            "print", print,
            "eval", eval
    );
    private final Function<List<Object>, Object> fn;

    private BuiltinObject(Function<List<Object>, Object> fn) {
        this.fn = fn;
    }

    private static Function<List<Object>, Object> evalFn() {
        return args -> {
            assertArgCount(1, args.size(), "eval");
            var arg = args.get(0);
            if (arg instanceof StringObject) {
                var value = arg.value();
                // todo 每次调用eval都会新建一个环境，多次eval之间没有关系，不确定是否正确，后面再看怎么优化, 可能需要传入一个环境对象作为eval参数
                var env = new Environment();
                return new Parser(value).parseProgram().eval(env);
            }
            throw new UnsupportedArgumentException(arg.type(), "last");
        };
    }

    private static Function<List<Object>, Object> printFn() {
        return args -> {
            if (args == null) return StringObject.valueOf("\n");
            else if (args.size() == 1) return args.get(0);
            else {
                return StringObject.valueOf(args.stream().map(Objects::toString).collect(Collectors.joining("\n")));
            }
        };
    }

    private static Function<List<Object>, Object> firstFn() {
        return args -> {
            assertArgCount(1, args.size(), "first");
            var arg = args.get(0);
            if (arg instanceof StringObject) {
                return StringObject.valueOf(String.valueOf(arg.value().charAt(0)));
            } else if (arg instanceof ArrayObject) {
                return ((ArrayObject) arg).get(0);
            }
            throw new UnsupportedArgumentException(arg.type(), "first");
        };
    }

    private static Function<List<Object>, Object> lastFn() {
        return args -> {
            assertArgCount(1, args.size(), "last");
            var arg = args.get(0);
            if (arg instanceof StringObject) {
                return StringObject.valueOf(String.valueOf(arg.value().charAt(arg.value().length() - 1)));
            } else if (arg instanceof ArrayObject) {
                var arrayObject = (ArrayObject) arg;
                return arrayObject.get(arrayObject.size() - 1);
            }
            throw new UnsupportedArgumentException(arg.type(), "last");
        };
    }

    private static Function<List<Object>, Object> lenFn() {
        return args -> {
            assertArgCount(1, args.size(), "len");
            var arg = args.get(0);
            if (arg instanceof StringObject) {
                return IntegerObject.valueOf(arg.value().length());
            } else if (arg instanceof ArrayObject) {
                return IntegerObject.valueOf(((ArrayObject) arg).size());
            }
            throw new UnsupportedArgumentException(arg.type(), "len");
        };
    }

    private static Function<List<Object>, Object> pushFn() {
        return args -> {
            assertArgCount(2, args.size(), "push");
            var arr = args.get(0);
            var arg = args.get(1);
            if (arr instanceof ArrayObject) {
                return ((ArrayObject) arr).add(arg);
            }
            throw new UnsupportedArgumentException(arg.type(), "push");
        };
    }

    private static void assertArgCount(int excepted, int actual, String name) {
        if (excepted != actual) throw new WrongArgumentsCount(excepted, actual, name);
    }

    public Object apply(List<Object> args) {
        return fn.apply(args);
    }

    @Override
    public ObjectType type() {
        return ObjectType.BUILTIN;
    }

    @Override
    public String value() {
        return "builtin function";
    }
}
