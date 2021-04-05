package me.mathyj.object;

import me.mathyj.exception.UnsupportedArgumentException;
import me.mathyj.exception.WrongArgumentsCount;
import me.mathyj.parser.Parser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BuiltinObject implements Object {
    public static final List<Object> builtins;
    public static Map<String, Object> builtinMap = new LinkedHashMap<>() {{
        put("len", new BuiltinObject(lenFn()));
        put("first", new BuiltinObject(firstFn()));
        put("last", new BuiltinObject(lastFn()));
        put("push", new BuiltinObject(pushFn()));
        put("print", new BuiltinObject(printFn()));
        put("eval", new BuiltinObject(evalFn()));
    }};

    static {
        builtins = new ArrayList<>(builtinMap.size());
        builtinMap.forEach((k, v) -> builtins.add(v));
    }

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
            if (args.size() == 0) return Object.NULL;
            else if (args.size() == 1) return args.get(0);
//            assertArgCount(2, args.size(), "push");
            var arr = args.get(0);
            if (arr instanceof ArrayObject) {
                var array = ((ArrayObject) arr);
                for (int i = 1; i < args.size(); i++) {
                    var arg = args.get(i);
                    array.add(arg);
                }
                return array;
            }
            throw new UnsupportedArgumentException(arr.type(), "push");
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
