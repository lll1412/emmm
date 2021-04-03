package me.mathyj.parser.ast.expression;

import me.mathyj.object.Environment;
import me.mathyj.object.HashObject;
import me.mathyj.object.Object;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HashLiteral extends Expression {
    private final List<Pair> pairs;

    public HashLiteral(List<Pair> pairs) {
        this.pairs = pairs;
    }

    public HashLiteral() {
        this.pairs = Collections.emptyList();
    }

    @Override
    public Object eval(Environment env) {
        LinkedHashMap<Object, Object> hash = new LinkedHashMap<>();
        for (var pair : pairs) {
            var key = pair.key.eval(env);
            var val = pair.val.eval(env);
            hash.put(key, val);
        }
        return new HashObject(hash);
    }

    @Override
    public String toString() {
        var s = pairs.stream().map(Objects::toString).collect(Collectors.joining(", "));
        return "{%s}".formatted(s);
    }

    public static class Pair {
        public final Expression key;
        public final Expression val;

        private Pair(Expression key, Expression val) {
            this.key = key;
            this.val = val;
        }

        public static Pair of(Expression key, Expression val) {
            return new Pair(key, val);
        }

        @Override
        public String toString() {
            return "%s: %s".formatted(key, val);
        }
    }
}
