package me.mathyj.code;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OpcodeTest {
    @Test
    void make() {
        var tests = new T[]{
                T.of(Opcode.CONSTANT, new int[]{0xFFFE}, new byte[]{(byte) Opcode.CONSTANT.ordinal(), (byte) 0xFF, (byte) 0xFE}),
        };
        for (var t : tests) {
            var instruction = Opcode.make(t.op, t.operands);
            assertEquals(t.expected.length, instruction.length);
            assertArrayEquals(t.expected, instruction);
        }
    }

    private static class T {
        Opcode op;
        int[] operands;
        byte[] expected;

        public T(Opcode op, int[] operands, byte[] expected) {
            this.op = op;
            this.operands = operands;
            this.expected = expected;
        }

        public static T of(Opcode op, int[] operands, byte[] expected) {
            return new T(op, operands, expected);
        }
    }
}