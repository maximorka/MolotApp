package com.first.util;

import com.first.lowLevel.Sample;

import java.nio.ByteBuffer;

public class ConvertorInt16 {
    public static byte[] convert(Sample sample) {
        short i = (short) (sample.getI() * 32767f);
        short q = (short) (sample.getQ() * 32767f);

        return ByteBuffer.allocate(4).putShort(i).putShort(q).array();
    }

    public static void main(String[] args) {
        Sample sample = new Sample(-0.999f, 0.999f);

        byte[] bytes = convert(sample);
        System.out.println(bytes.length);


    }
}
