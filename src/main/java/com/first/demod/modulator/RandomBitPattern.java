package com.first.demod.modulator;

import java.util.Random;

public class RandomBitPattern extends BitPattern {
    private Random random = new Random();

    @Override
    public boolean next() {
        return random.nextBoolean();
    }
}
