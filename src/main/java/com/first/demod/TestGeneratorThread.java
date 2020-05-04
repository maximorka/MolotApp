//package com.first.demod;
//
//import com.integer.horizon.lowlevel.demod.convertors.ConvertorInt12;
//import com.integer.horizon.lowlevel.demod.generator.GeneratorIQFSK;
//import com.integer.horizon.lowlevel.demod.generator.Sample;
//import com.integer.horizon.lowlevel.demod.generator.WaveGenerator;
//import com.integer.horizon.lowlevel.rw.horizon.command.HorizonCommand;
//import com.integer.horizon.lowlevel.rw.horizon.command.HorizonCommands;
//import com.integer.horizon.lowlevel.rw.horizon.device.HorizonDevice;
//import com.integer.horizon.lowlevel.rw.horizon.device.HorizonDeviceListener;
//
//import java.util.List;
//
//public class TestGeneratorThread extends Thread implements HorizonDeviceListener {
//    int packetSize = 64;
//
//    private boolean running = true;
//    private byte[] sampleBytes;
//    private byte[] generatorPacket = new byte[packetSize * 3];
//    private int packetIdx = 0;
//    private Sample resultSample = new Sample(0,0);
//    private int sideChannels = 2;
//    private int sideChannelsStep = 1000;
//    private Sample[] samples = new Sample[sideChannels * 2];
//    private GeneratorIQFSK generator = new GeneratorIQFSK();
//    private GeneratorIQFSK[] generators = new GeneratorIQFSK[sideChannels];
//    private WaveGenerator[] gens = new WaveGenerator[sideChannels * 2];
//
//    private int fd = 48000;
//    private int speed = 250;
//    private int repeats = fd/speed;
//
//    private char[] chars = new char[90];
//
//    public boolean getRunning(){
//        return running;
//    }
//
//    private HorizonDevice horizonDevice;
//
//    public TestGeneratorThread(HorizonDevice horizonDevice){
//        if (horizonDevice != null) {
//            this.horizonDevice = horizonDevice;
//            horizonDevice.addEventListener(this);
//        }
//
//        for (int i = 1; i <= sideChannels; i += 2){
//            gens[i-1] = new WaveGenerator((double)-sideChannelsStep * i /(double)fd);
//            gens[i] = new WaveGenerator((double)sideChannelsStep* i /(double)fd);
//            generators[i-1] = new GeneratorIQFSK();
//            generators[i] = new GeneratorIQFSK();
//        }
//
//        setFd(6000);
//    }
//
//    public void setFd(int fd){
//        System.out.println("set generator Fd = " + fd);
//        this.fd = fd;
//        for (int i = 1; i <= sideChannels; i += 2){
//            gens[i-1].setShift((double)-sideChannelsStep * i /(double)fd);
//            gens[i].setShift((double)sideChannelsStep * i /(double)fd);
//        }
//        generator.setModulationFrequency(250);
//        generator.setBandwidth(fd);
//        for (int i = 0; i < sideChannels; i++){
//            generators[i].setModulationFrequency(250);
//            generators[i].setBandwidth(fd);
//        }
//        repeats = fd / speed;
//    }
//
//    public void setRunning(boolean running){
//        this.running = running;
//    }
//
//    @Override
//    public void run() {
//        while(true){
//            while (running){
//                if (bufferPercent <= 60) {
//
//                    for (int idx = 0; idx < chars.length; idx++){ //Записати весь файл
////                        boolean data = chars[idx] == '1';
//                        boolean data = idx % 2 == 0;
//
//                        generator.setData(data); //center
//                        for (int channel = 0; channel < sideChannels; channel++){
//                            generators[channel].setData(data);
//                        }
//
//                        for(int repeat = 0; repeat < repeats; repeat++){
//                            Sample sample = generator.modulate();
//                            double iValue = sample.getI();
//                            double qValue = sample.getQ();
//                            for (int channel = 0; channel < sideChannels; channel++){
//                                samples[channel] = generators[channel].modulate();
//                                samples[channel].multiply(gens[channel].modulate());
//                                iValue += samples[channel].getI();
//                                qValue += samples[channel].getQ();
//                            }
//
//                            resultSample.set(iValue/(1+sideChannels),qValue/(1+sideChannels));
//                            sampleBytes = ConvertorInt12.convert(resultSample);
//                            generatorPacket[packetIdx++] = sampleBytes[2];
//                            generatorPacket[packetIdx++] = sampleBytes[1];
//                            generatorPacket[packetIdx++] = sampleBytes[0];
//                            if (packetIdx >= generatorPacket.length){
//                                packetIdx = 0;
//                                horizonDevice.writeSamples(HorizonCommands.transmitIQ(generatorPacket));
//                            }
//                        }
//
//                    }
//                }
//
//                    try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
//
//            }
//
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private int bufferPercent;
//
//    @Override
//    public void ethernetParamsReceived(String ip, String mask, int port, String gateway) {
//
//    }
//
//    @Override
//    public void seansStateChanged(boolean seans) {
//
//    }
//
//    @Override
//    public void commandReceived(HorizonCommand command, int value) {
//        if (command == HorizonCommand.tx_get_buffer_percent) {
//            bufferPercent = value;
//        }
//    }
//
//    @Override
//    public void iqReceived(List<Sample> iq) {
//
//    }
//
//    @Override
//    public void connectStateChanged(boolean connected) {
//
//    }
//}