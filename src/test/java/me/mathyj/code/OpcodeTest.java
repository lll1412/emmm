package me.mathyj.code;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OpcodeTest {
    @Test
    void make() {
        var tests = new T[]{
                T.of(Opcode.CONSTANT, new int[]{0xFFFE}, new Instructions(Opcode.CONSTANT, (char) 0xFF, (char) 0xFE)),
        };
        for (var t : tests) {
            var actualInstruction = Instructions.make(t.op, t.operands);
            assertEquals(t.expected.toString(), actualInstruction.toString());
        }
    }

    private static class T {
        Opcode op;
        int[] operands;
        Instructions expected;

        public T(Opcode op, int[] operands, Instructions expected) {
            this.op = op;
            this.operands = operands;
            this.expected = expected;
        }

        public static T of(Opcode op, int[] operands, Instructions expected) {
            return new T(op, operands, expected);
        }
    }
}