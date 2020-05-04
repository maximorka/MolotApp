package com.first.demod.modulator;

import com.first.Core;
import com.first.demod.HorizonChannelConfig;
import com.first.lowLevel.Sample;
import com.first.lowLevel.enums.HorizonWidth;
import com.first.util.ConvertorInt12;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HorizonModulator {
    private HorizonChannelConfig config;
    public static double koef = 1.0;
    private int count = 0;
   // private WaveGenerator[] gens;
   // private GeneratorIQFSK[] iqfskGenerators;
    private int iqRewSymbol;
    static boolean lastbit = false;
    private int mod = 2;
    private Sample[] constelation = {new Sample (0.f,1.f), new Sample(0.f,-1.f)};



    private List<Queue<Boolean>> data;
    private BitPattern[] bitPatterns;

    private Queue<Byte> packetData = new ConcurrentLinkedDeque<>();

    private volatile boolean busy = false;
    private boolean needReinit;
    Sample accum = new Sample(0,0);
    Sample out = new Sample(0,0);
    private int currentChannelIndex;

    private List<Integer> freeChannelIndeces = new ArrayList<>();

    //private DeviceType deviceType;

    public HorizonModulator config(HorizonChannelConfig config) {

        this.config = config;

        setup();

        return this;
    }

    private void setup() {
        //deviceType = config.getDeviceType();

        if (busy) {
            return;
        }

        mod = 2;
        int channelCount = 1;
        int channelSpeed = getSingleChannelSpeed();
        int bandwidth = getHorizonWidth().getValue();

       // gens = new WaveGenerator[channelCount];
        //iqfskGenerators = new GeneratorIQFSK[channelCount];
        data = new ArrayList<>();

        bitPatterns = new BitPattern[channelCount];
        bitPatterns[0] =  BitPattern.create("010100");
//        for(int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
//            //Init Bit Pattern
//           // bitPatterns[channelIndex] =  new RandomBitPattern();
////            bitPatterns[channelIndex] =  BitPattern.create("0000000000101101010111000011101010111010110101011001010101010110010101101001010110100110101010100101");
//            bitPatterns[channelIndex] =  BitPattern.create("10");
//
//            //Init wave generators
////            double genOffset = config.getChannelOffset(channelIndex) / (double) bandwidth;
////            gens[channelIndex] = new WaveGenerator(genOffset);
////
////            //Init IQFSK generators
////            iqfskGenerators[channelIndex] = new GeneratorIQFSK();
////            iqfskGenerators[channelIndex].setModulationFrequency(channelSpeed);
////            iqfskGenerators[channelIndex].setBandwidth(bandwidth);
//
//            data.add(new ConcurrentLinkedDeque<>());
//        }
        data.add(new ConcurrentLinkedDeque<>());
        //data.add(new ConcurrentLinkedDeque<>());
        iqRewSymbol =  getHorizonWidth().getValue() / getSingleChannelSpeed();
        System.out.println("IQOER "+iqRewSymbol);
        currentChannelIndex = 1;

        needReinit = false;
    }

    public HorizonWidth getHorizonWidth() {
        return config.calculateTotalHorizonWidth();

    }

    public int getSingleChannelSpeed() {
        return config.getChannelSpeed();

    }

    public void addChannelData(int channelIndex, boolean item) {

        data.get(0).add(item);
    }

    public boolean getChannelDataOrStub(int channelIndex) {
        //System.out.println("size "+data.size());
        if (channelIndex >= data.size()) {
           // System.out.println("data less "+count);
            //Core.getInstance().countC=0;
            return false;
        }

        Queue<Boolean> q = data.get(0);
        //System.out.println(q.size()+" ytutyu");
        if (q.size() > 0) {
            return q.poll();
        }

        Core.getInstance().countC=0;
        return bitPatterns[0].next();
    }

    public void setBitPattern(int channelIndex, BitPattern pattern) {
        bitPatterns[channelIndex] = pattern;
    }


    private List<Sample> lastResultSamples = new ArrayList<>();

    public byte[] generate() {

        if (needReinit) {
            System.out.println("Reinit");
            setup();
        }

        busy = true;

        int channelCount = config.getChannelCount();

        lastResultSamples.clear();

        int n = 0;
        while (true) {

            boolean data = getChannelDataOrStub(0);
            boolean defData = lastbit ^ data;
            lastbit = defData;
            //Модулюємо дані
            for (int repeat = 0; repeat < iqRewSymbol; repeat++) {
                    Sample sample = constelation[defData ? 1 : 0];

                //sample = filter(sample);
                float iValue = sample.getI();
                float qValue = sample.getQ();
                //Відправляємо семпл для подальшого відображення
                lastResultSamples.add(new Sample(iValue , qValue ));


                    byte[] sampleBytes = ConvertorInt12.convert(sample);
                    //Переносимо дані в пакет
                    packetData.add(sampleBytes[2]);
                    packetData.add(sampleBytes[1]);
                    packetData.add(sampleBytes[0]);
            }

            //Якщо достатньо даних - повертаємо пакет
            if (packetData.size() >= 192) {
count++;
                byte[] packet = new byte[192];
                for (int i = 0; i < packet.length; i++) {
                    packet[i] = packetData.poll();
                }
                //System.out.println("Creaate pac "+packetData.size());
                busy = false;
                return packet;
            }
        }
    }

    private Sample filter(Sample iq){
//        double Aci = accum.getI();
//        double Acq = accum.getQ();
//
//        Aci = Aci + iq.getI() - out.getI();
//        Acq = Acq + iq.getQ() - out.getQ();
//
//        accum.set(Aci,Acq);
//
//        out.set((double) (Aci/30),(double) (Acq/30));

        return out;
    }
    public int getChannelDataSize(int channelIndex) {
        return data.get(channelIndex).size();
    }

    public List<Sample> getLastResultSamples() {
        return lastResultSamples;
    }

    public int getCurrentChannelIndex() {
        return currentChannelIndex;
    }

    public void switchChannel() {
        currentChannelIndex++;
        if (currentChannelIndex >= data.size()) {
            currentChannelIndex = 0;
        }
    }

    public List<Integer> getFreeChannelIndeces() {
        freeChannelIndeces.clear();
        for(int i = 0; i < data.size(); i++) {
            if (data.get(i).size() <= 0) {
                freeChannelIndeces.add(i);
            }
        }
        return freeChannelIndeces;
    }

    public int getChannelIndexWithMinDataSize() {
        int result = 0;
        int value = data.get(0).size();

        for(int i = 1; i < data.size(); i++) {
            if (data.get(i).size() < value) {
                value = data.get(i).size();
                result = i;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        HorizonChannelConfig config = new HorizonChannelConfig(250, 1);

        HorizonModulator transmitter = new HorizonModulator().config(config);


        //byte [] temp =   transmitter.generate();
        System.out.println("Size:"+  (1 ^ 1));
    }

    public int getChannelCount() {
        return config.getChannelCount();
    }
}
