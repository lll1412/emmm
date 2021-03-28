package me.mathyj.object;

import me.mathyj.exception.UnsupportedArgumentException;
import me.mathyj.exception.WrongArgumentsCount;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BuiltinObject implements Object {
    private static final BuiltinObject len = new BuiltinObject(args -> {
        assertArgCount(1, args.size(), "len");
        var arg = args.get(0);
        if (arg instanceof StringObject) {
            return IntegerObject.valueOf(arg.value().length());
        }
        throw new UnsupportedArgumentException(ObjectType.STRING, arg.type(), "len");
    });
    public static Map<String, Object> builtins = Map.of(
            "len", len
    );
    private final Function<List<Object>, Object> fn;

    public BuiltinObject(Function<List<Object>, Object> fn) {
        this.fn = fn;
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
