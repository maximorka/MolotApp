package com.first.util;

import com.first.lowLevel.Sample;

import java.nio.ByteBuffer;

public class ConvertorInt12 {
    private static boolean[] bitsI, bitsQ, resultBits;

    /**
     * Оригінальна версія
     */
    public static byte[] convert(float iValue, float qValue) {
        short i = (short) (iValue * 2047f);
        short q = (short) (qValue * 2047f);
        bitsI = ByteUtils.getBits(ByteUtils.convertShortToByteArray(i));
        bitsQ = ByteUtils.getBits(ByteUtils.convertShortToByteArray(q));
        resultBits = new boolean[24];
        System.arraycopy(bitsI, 4, resultBits, 0, 12);
        System.arraycopy(bitsQ, 4, resultBits, 12, 12);
        return ByteUtils.getBytes(resultBits);
    }

    //Швидке перетворення
    public static byte[] convert(Sample sample) {
//        return convert((float) sample.getI(), (float) sample.getQ());

        byte[] result = new byte[3];

        short i = (short) (sample.getI() * 2047f);
        short q = (short) (sample.getQ() * 2047f);

        byte[] iqBytes = ByteBuffer.allocate(4).putShort(i).putShort(q).array();
        boolean[] iqBits = ByteUtils.getBits(iqBytes);

        //First byte
        result[0] = getByte(iqBits, 4);

        //Middle byte
        boolean[] middleBits = new boolean[8];
        middleBits[0] = iqBits[12];
        middleBits[1] = iqBits[13];
        middleBits[2] = iqBits[14];
        middleBits[3] = iqBits[15];
        middleBits[4] = iqBits[20];
        middleBits[5] = iqBits[21];
        middleBits[6] = iqBits[22];
        middleBits[7] = iqBits[23];
        result[1] = getByte(middleBits, 0);

        //Last byte
        result[2] = getByte(iqBits, 24);

        return result;
    }

    private static byte getByte(boolean[] bits, int offset) {
        byte result = 0;
        for(int i = 0; i < 8; i++) {
            if (bits[i + offset]) {
                byte power = 1;
                for(int powerIndex = 0; powerIndex < 7 - i; powerIndex++) {
                    power *= 2;
                }
                result += power;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        test();
        long end = System.currentTimeMillis() - start;
        System.out.println("time"+end);

//        Sample s = new Sample(0,-1);
//        byte[] result = ConvertorInt12.convert(s);
//        ByteUtils.printBytes(result); //00000000 00001000 00000001
    }

    private static void test() {
        Sample sample = new Sample(0, 0);
        for(int i = 0; i < 16000000; i++) {
            sample.set(i , i);

            byte[] r1 = convert(sample);
//            byte[] r2 = convert(i, i);
////
//            for(int j = 0; j < 3; j++) {
//                if (r1[j] != r2[j]) {
//                    System.out.println("BAD: " + i);
//                }
//            }
        }
    }

}

