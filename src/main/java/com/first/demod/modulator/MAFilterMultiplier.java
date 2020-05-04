//package com.first.demod.modulator;
//
//import com.integer.horizon.lowlevel.demod.generator.Sample;
//
//import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedDeque;
//
//public class MAFilterMultiplier {
//    private double accumulatorI = 0;
//    private double accumulatorQ = 0;
//
//    private int sampleCount;
//
//    private Queue<Sample> samples = new ConcurrentLinkedDeque<>();
//
//    public MAFilterMultiplier(int sampleCount) {
//        this.sampleCount = sampleCount;
//
//        for(int i = 0; i < sampleCount; i++) {
//            samples.add(new Sample(0, 0));
//        }
//    }
//
//    public void addSample(Sample sample) {
//        accumulatorI += sample.getI();
//        accumulatorQ += sample.getQ();
//
//        Sample last = samples.poll();
//        accumulatorI -= last.getI();
//        accumulatorQ -= last.getQ();
//
//        samples.add(sample.copy());
//    }
//
//    public Sample getResult() {
//        return new Sample(accumulatorI / (double) sampleCount, accumulatorQ / (double) sampleCount);
//    }
//}
