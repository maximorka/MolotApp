//package com.first.demod.modulator;
//
//import com.integer.horizon.lowlevel.demod.generator.Sample;
//
//public class SampleFilterMultiplier {
//    //private double[] samples;
//
//    private double[] iValues, qValues;
//    private double[] filter;
//
//    private double iValue, qValue;
//
//    public SampleFilterMultiplier(int bufferSize, double[] filter) {
//        iValues = new double[bufferSize];
//        qValues = new double[bufferSize];
//        this.filter = filter;
//    }
//
//    public void addSample(Sample sample) {
//        System.arraycopy(iValues, 0, iValues, 1, iValues.length - 1);
//        System.arraycopy(qValues, 0, qValues, 1, qValues.length - 1);
//        iValues[0] = sample.getI();
//        qValues[0] = sample.getQ();
//
//        //Filtering
//        int filterSize = filter.length;
//
//        iValue = 0;
//        qValue = 0;
//
//        for (int i = 0; i < filterSize/2; i++) {
//            iValue += (iValues[i] + iValues[filterSize - i - 1]) * filter[i];
//            qValue += (qValues[i] + qValues[filterSize - i - 1]) * filter[i];
//        }
//    }
//
//    public Sample getResult() {
//        return new Sample(iValue, qValue);
//    }
//
//    //1889
//    public static void main(String[] args) {
//        double[] filter = filter(1024, 1);
//        Sample sample = sample(5d, 5d);
//
//        SampleFilterMultiplier multiplier = new SampleFilterMultiplier(filter.length, filter);
//
//        int count = 1000000;
//
//        long start = System.currentTimeMillis();
//        for(int i = 1; i <= count; i++) {
//            multiplier.addSample(sample);
//        }
//        System.out.println("Time: " + (System.currentTimeMillis() - start));
//    }
//
//    private static Sample sample(double I, double Q) {
//        return new Sample(I, Q);
//    }
//
//    private static double[] filter(int count, double value) {
//        double[] result = new double[count];
//        for(int i = 0; i < count; i++) {
//            result[i] = value;
//        }
//        return result;
//    }
//}
