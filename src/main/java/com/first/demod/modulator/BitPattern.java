package com.first.demod.modulator;

import com.first.util.ByteUtils;

public class BitPattern {
    private boolean[] bits;
    private int index;

    public BitPattern() {
    }

    public boolean next() {
        boolean bit = bits[index];
        index++;
        if (index >= bits.length) {
            index = 0;
        }
        return bit;
    }

    public static BitPattern create(String bits) {
        return create(ByteUtils.getBits(bits));
    }

    public static BitPattern create(boolean[] bits) {
        BitPattern result = new BitPattern();
        result.bits = new boolean[bits.length];
        System.arraycopy(bits, 0, result.bits, 0, bits.length);
        result.index = 0;
        return result;
    }

    public static void main(String[] args) {
        BitPattern test = BitPattern.create("110");
        for(int i = 0; i < 100; i++) {
            System.out.print(test.next() ? 1 : 0);
        }
    }
}
