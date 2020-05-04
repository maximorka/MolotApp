package com.first.demod.demodulator;

//import com.integer.horizon.lowlevel.demod.demodulator.Fm2DemodulatorMultChannels;
//import com.integer.horizon.lowlevel.demod.generator.Sample;
import com.first.demod.HorizonChannelConfig;
import com.first.lowLevel.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HorizonDemodulator {
    private HorizonChannelConfig config;
    int index = 0;
    //private List<Fm2DemodulatorMultChannels> demodulators;
    private DBPSKdemodulator dbpsKdemodulator;
    private List<HorizonDemodulatorListener> listeners;

    private DemodulateThread demodulateThread;
    private volatile Queue<Sample> sampleQ;

    public HorizonDemodulator() {
        listeners = new ArrayList<>();
        sampleQ = new ConcurrentLinkedQueue<>();// ArrayDeque<>(2048);

        demodulateThread = new DemodulateThread();
        demodulateThread.start();
    }

    public void addDataListener(HorizonDemodulatorListener listener) {
        listeners.add(listener);
    }

    public HorizonDemodulator config(HorizonChannelConfig config) {
        this.config = config;

        setup();

        return this;
    }

    private void setup() {
        sampleQ.clear();

//        if (demodulators != null) {
//            for(Fm2DemodulatorMultChannels demodulator: demodulators) {
//                demodulator.close();
//            }
//        }
//
//        demodulators = new ArrayList<>();
        dbpsKdemodulator = new DBPSKdemodulator(250,3000);



    }

    public void setInverse(boolean inverse) {
//        for(Fm2DemodulatorMultChannels d: demodulators) {
//            d.setInverse(inverse);
//        }
    }

    public void addSample(float I, float Q) {
        sampleQ.add(new Sample(I, Q));

//        if (sampleQ.size() > 10000) {
//            System.out.println(sampleQ.size());
//        }
    }

    class DemodulateThread extends Thread {
        private boolean running = true;
        private Sample tempSample = null;

        @Override
        public void run() {

            while (running) {
                //Читаємо всі семпли
                while(!sampleQ.isEmpty()) {

                    tempSample = sampleQ.poll();
                    boolean bits = dbpsKdemodulator.demodulate(tempSample);
                   // for (int j = 0; j < bits.length; j++) {
                                    System.out.print(bits ? 1 : 0);
                                    index++;
                                    if (index == 100) {
                                        index = 0;
                                        System.out.println();
                                    }
                                    // Core.getInstance().generatorIQServer.writeSample(new Sample(bits[j]?1:0, 0));
                             //   }
//                    for (int i = 0; i < demodulators.size(); i++) {
//                        Fm2DemodulatorMultChannels demodulator = demodulators.get(i);
//
//                        if (demodulator.demodulate(tempSample)) {
//                            boolean[] bits = demodulator.getDemodulatedBits().getBits();
//                            //if (Core.getInstance().generatorIQServerMode == 4 && Core.getInstance().generatorIQServer != null) {
////
////if(i==0){
////
////
////                                for (int j = 0; j < bits.length; j++) {
////                                    System.out.print(bits[j] ? 1 : 0);
////                                    index++;
////                                    if (index == 100) {
////                                        index = 0;
////                                        System.out.println();
////                                    }
////                                    // Core.getInstance().generatorIQServer.writeSample(new Sample(bits[j]?1:0, 0));
////                                }
////                        }
//
//                            //}
//                            for (int listenerIndex = 0; listenerIndex < listeners.size(); listenerIndex++) {
//                               listeners.get(listenerIndex).dataReceived(i, bits);
//
//                            }
//                        }
//                    }
                }

                //Відпочиваємо 1 мілісекунду
                try {
                    Thread.sleep(0,20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void close() {
            running = false;
        }
    }

    public void close() {
        if (demodulateThread != null) {
            demodulateThread.close();
            demodulateThread = null;
        }
    }

    public static void main(String[] args) {
        HorizonChannelConfig config = new HorizonChannelConfig(250, 3);
        HorizonDemodulator receiver = new HorizonDemodulator().config(config);
    }
}
