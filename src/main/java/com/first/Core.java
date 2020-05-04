package com.first;

import com.first.demod.GeneratorIQServer;
import com.first.demod.HorizonChannelConfig;
import com.first.demod.HorizonModem;
import com.first.demod.modulator.HorizonModulator;
import com.first.lowLevel.*;
import com.first.lowLevel.command.HorizonCommand;
import com.first.lowLevel.command.HorizonCommands;
import com.first.lowLevel.command.HorizonError;
import com.first.lowLevel.enums.HorizonWidth;
import com.first.lowLevel.enums.WorkMode;
import com.first.util.Params;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.first.PackageData.SendData.byteSendQ;

/**
 * Created by pc3 on 01.04.2020.
 */
public class Core {
    private static Core instance = new Core();
    private HorizonDevice dev[] = new HorizonDevice[2];
    private boolean running ;
    public String type = "Int16";
    public boolean mode = false;
    //public MachineState machineState;
    private HorizonModem horizonModem;
    private HorizonChannelConfig config;
    public int size = 4000;
    public int countC = 0;
    public  int pointWrite = 0;
    public double[] buffer_im = new double[size];
    public double[] buffer_re = new double[size];
    public static Queue<Byte> byteSend = new LinkedBlockingQueue<Byte>();
    public int generatorIQServerMode = 2;
    public GeneratorIQServer generatorIQServer;

        public static void setGeneratorIQServerModeRx() throws IOException {
        instance.generatorIQServerMode = 1;
        instance.initGeneratorIQServer();

             Runtime.getRuntime().exec("cmd /c  start C:/project.jar");

        }

    public static void setGeneratorIQServerModeTx() throws IOException {
        instance.initGeneratorIQServer();

        Runtime.getRuntime().exec("cmd /c  start C:/project.jar");
    }

    private void initGeneratorIQServer() {
        if (generatorIQServer == null) {
            try {
                generatorIQServer = new GeneratorIQServer(5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



private Core() {
    //machineState = new MachineState(type);
    new UpdateThread().start();


}

    /**
     * Повертає унікальний екземпляр "ядра"
     *
     * @return
     */
    public static Core getInstance() {
        return instance;
    }

    public static Core i() {
        return instance;
    }

    class UpdateThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (running) {
                    update();

                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public HorizonDevice[] init() {

        closeAndSetupConfig();

        dev[0] = initSingleDevice();

        setupTxRx();
        return dev;
    }

    private HorizonDevice initSingleDevice() {
        // Create single device
        String ethernetIp1 = Params.SETTINGS.getString("ethernet_ip", "192.168.0.5");
        String ethernetPort1 = Params.SETTINGS.getString("ethernet_port", "80");

        int rxFreq1 = Integer.parseInt(Params.SETTINGS.getString("rx_freq", "128000"));
        int txFreq1 = Integer.parseInt(Params.SETTINGS.getString("tx_freq", "128000"));

//        String deviceTypeString = Params.SETTINGS.getString("device_type", DeviceType.horizon.name());
//        DeviceType deviceType = DeviceType.valueOf(deviceTypeString);
//
        HorizonChannelConfig deviceConfig = config.copy();


        HorizonDevice rxTxDevice = createHorizonDevice( ethernetIp1, ethernetPort1, rxFreq1, txFreq1,3);
        rxTxDevice.writeCommand(HorizonCommands.disableRx());
        rxTxDevice.writeCommand(HorizonCommands.disableTx());

        return rxTxDevice;
    }

    private HorizonDevice createHorizonDevice(  String ethernetIp, String ethernetPort,
                                               int rxFreq, int txFreq, int device) {
        HorizonDevice result = null;

            result = new Ethernet();

            Map<String, String> settings = new HashMap<>();
            settings.put("ip", ethernetIp);
            settings.put("port", ethernetPort);

            //result.addEventListener(configuration.ui);

            result.init(settings);


        //Set device sampling frequency
        //result.writeCommand(HorizonCommands.setRxDemodulationWidth(deviceConfig.calculateTotalHorizonWidth()));
        //result.writeCommand(HorizonCommands.setTxModulationWidth(deviceConfig.calculateTotalHorizonWidth()));
        //Set RX TX
        if(device == 0) {
            System.out.println("dev tx");
            result.writeCommand(HorizonCommands.setTxModulationFrequency(txFreq*1000));
            //result.writeCommand(HorizonCommands.setRxDemodulationFrequency(rxFreq));
        }
        if(device == 1) {
            System.out.println("dev rx");
            //result.writeCommand(HorizonCommands.setTxModulationFrequency(txFreq));
            result.writeCommand(HorizonCommands.setRxDemodulationFrequency(rxFreq*1000));
        }
        if(device==3){

            System.out.println("dev rx tx"+device);
            result.writeCommand(HorizonCommands.setTxModulationFrequency(txFreq));
            result.writeCommand(HorizonCommands.setRxDemodulationFrequency(rxFreq));
        }
        //Save params
       // result.config(debugMode, txFreq, rxFreq, false, false, false);

        //Setup
        //result.config(deviceConfig);

        return result;
    }
    private void setupTxRx() {
//        if(HardwareSettingsPanel.checkVirial.isSelected()){
//            rxDevice.writeCommand(HorizonCommands.setFormRx(VirialPanel.getFormRx()));
//            rxDevice.writeCommand(HorizonCommands.enableRx());
//        }else{
        dev[0].writeCommand(HorizonCommands.enableRx());
        dev[0].writeCommand(HorizonCommands.enableTx());
        dev[0].writeCommand(HorizonCommands.setTxModulationWidth(HorizonWidth.width3KHz));
        dev[0].writeCommand(HorizonCommands.setRxDemodulationWidth(HorizonWidth.width3KHz));

     //   }

        //Setup error listeners
        boolean singleWorkMode =  Params.SETTINGS.getBoolean("single_device");
        if (singleWorkMode) {
            dev[0].addEventListener(new HorizonDeviceAdapter() {
                @Override
                public void commandReceived(HorizonCommand command, int value) {

                    if (command == HorizonCommand.error) {
                        HorizonError error = HorizonError.getError(value);
                        if (error == null) {
                            System.out.println("RXTX ERROR: " + Integer.toHexString(value));
                        } else {
                            System.out.println("RXTX ERROR: " + error.getDescription());
                        }
                    }
                }
            });
        } else {
            dev[0].addEventListener(new HorizonDeviceAdapter() {
                @Override
                public void commandReceived(HorizonCommand command, int value) {
                    //System.out.println("111112");
                    if (command == HorizonCommand.error) {
                        HorizonError error = HorizonError.getError(value);
                        if (error == null) {
                            System.out.println("RX ERROR: " + Integer.toHexString(value));
                        } else {
                            System.out.println("RX ERROR: " + error.getDescription());
                        }
                    }
                }
            });

            dev[0].addEventListener(new HorizonDeviceAdapter() {
                @Override
                public void commandReceived(HorizonCommand command, int value) {
                    //System.out.println("111113");
                    if (command == HorizonCommand.error) {
                        HorizonError error = HorizonError.getError(value);
                        if (error == null) {
                            System.out.println("TX ERROR: " + Integer.toHexString(value));
                        } else {
                            System.out.println("TX ERROR: " + error.getDescription());
                        }
                    }
                }
            });
        }

        //Setup RX listener
        dev[0].addEventListener(new HorizonDeviceListener() {
            @Override
            public void connectStateChanged(boolean connected) {
            }

            @Override
            public void seansStateChanged(boolean seans) {

            }

            @Override
            public void ethernetParamsReceived(String ip, String mask, int port, String gateway) {

            }

            @Override
            public void commandReceived(HorizonCommand command, int value) {

            }

            @Override
            public void iqReceived(List<Sample> iq) {
                ;

                for(int i = 0; i < iq.size(); i++) {
                    Sample sample = iq.get(i);

                    float I = sample.getI();
                    float Q = sample.getQ();

                    //Add sample
                   horizonModem.addSample(I, Q);
//
//                    if ( generatorIQServer != null) {
//
//                        generatorIQServer.writeSample(new Sample(I , Q ));
//                    }
                }
            }
        });
    }
    public static boolean txConnected() {
        return tx() != null && tx().isConnected();
    }

    public static HorizonDevice tx() {
        return instance.dev[0];
    }

    /**
     * Оновлює ядро з частотою 10 раз в секунду
     */
    public void update() {
       // boolean rxReady = rxConnected();
        boolean txReady = txConnected();
        if (txReady) {
            writeBits();
        }
    }



    private void writeBits() {
        if (horizonModem == null || !txConnected()) {

            return;
        }







        if (byteSendQ.size() > 0) {

            while (true) {
                int sizeQue = byteSendQ.size();
                if (sizeQue == 0) {
                    break;
                }
                boolean[] packetBits = new boolean[sizeQue];

                for (int i = 0; i < sizeQue; i++) {
                    packetBits[i] = byteSendQ.poll();
                    countC++;
                }

//                boolean[] packetBits = byteSendQ.
//                        pq.getCurrentPacket().getRawBits();
                writeBitToChannel(packetBits);

            }
            countC /= 192;
            countC++;
        }

        //Відправляємо дані в канал

        while (countC!=0) {

            //countC!=0
            byte[] iqData = horizonModem.generate();
            dev[0].writeSamples(HorizonCommands.transmitIQ(iqData));

                    if ( generatorIQServer != null) {
                        List<Sample> lastGeneratedSamples = horizonModem.getModulator().getLastResultSamples();
                        for(Sample sample: lastGeneratedSamples) {

                            generatorIQServer.writeSample(sample);
                        }
                    }
        }
    }

//        if (!dev[0].canWriteSamples()) {
//            return;
//        }
//
//        if (pq.hasPackets()) {
//            while (true) {
//                int minChannelIndex = horizonModem.getModulator().getChannelIndexWithMinDataSize();
//
//                int minChannelDataSize = horizonModem.getModulator().getChannelDataSize(minChannelIndex);
////                System.out.println("minChannelDataSize = " + minChannelDataSize);
//                if (minChannelDataSize > 128) {
//                    break;
//                }
//
//                if (!pq.hasData()) {
//                    pq.update(configuration.timeBetweenUpdate);
//
//                    if (!pq.hasData()) {
//                        break;
//                    }
//                }
//
//                AbstractPacket packet = pq.getCurrentPacket();
//                boolean[] packetBits = pq.getCurrentPacket().getRawBits();
//
////                if (packetBits.length >= 320) {
////                    packetHandler.log("");
////                    packetHandler.log("ПАКЕТ №" + pq.getCurrentPacket().getNumber() + " ВІДПРАВЛЕНО");
////                    packetHandler.logBits(packetBits);
////
////                    packetHandler.log("ОРИГІНАЛЬНИЙ ВМІСТ");
////                    packetHandler.logBits(ByteUtils.getBits(pq.getCurrentPacket().getRawBytes()));
////                }
//                int packetName=packet.getNumber();
//                boolean needSendToAllFreeChannels =
//                        (packetName == Protocol.FILE.RECEIVE.FILE_INIT) ||
//                                (packetName == Protocol.FILE.TRANSFER.INIT_CONFIRMED);
//                //|| (packetName == Protocol.SEANS.START_SEANS_COMMUCATION) ||
//                // (packetName == Protocol.SEANS.START_SEANS_CONFIRMED) || (packetName == Protocol.SEANS.END_SEANS_COMMUCATION) ||
//                //(packetName == Protocol.SEANS.END_SEANS_CONFIRMED);
//
//
//
////                boolean needSendToAllFreeChannels =
////                                (packet.getNumber() == Protocol.FILE.RECEIVE.FILE_INIT) ||
////                                (packet.getNumber() == Protocol.FILE.TRANSFER.INIT_CONFIRMED) || (packet.getNumber() == Protocol.SEANS.START_SEANS_COMMUCATION) ||
////                                (packet.getNumber() == Protocol.SEANS.START_SEANS_CONFIRMED) || (packet.getNumber() == Protocol.SEANS.END_SEANS_COMMUCATION) ||
////                                (packet.getNumber() == Protocol.SEANS.END_SEANS_CONFIRMED);
//
//                if (needSendToAllFreeChannels) {
//                    List<Integer> freeChannels = horizonModem.getModulator().getFreeChannelIndeces();
//                    writePacketToMultipleChannels(freeChannels, packetBits);
//                }
//                else if((packetName == Protocol.SMS.RECEIVE.START)|| (packetName == Protocol.SMS.TRANSFER.START_CONFIRMED) || (packetName == Protocol.SMS.RECEIVE.PART)|| (packetName == Protocol.SMS.TRANSFER.RESEND_MESSAGE)|| (packetName == Protocol.SMS.TRANSFER.MESSAGE_CONFIRMED)){
//                    writePacketToSingleChannel((config.getChannelCount()/2), packetBits);
//                }
//
//                else {
//                    //System.out.println("send packet: "+packet.getNumber()+" через канал: "+minChannelIndex);
////System.out.println(ByteUtils.bitsToString(packet.getRawBits()));
//                    writePacketToSingleChannel(minChannelIndex, packetBits);
//                }
//
//                if (packetBits.length % 8 != 0) {
//                    stats().addInt("packets_with_staffing_bits", 1);
//                }
//                pq.nullCurrentPacket();
//            }
//        }
//
//        //Відправляємо дані в канал
//        while (txDevice.canWriteSamples()) {
//            byte[] iqData = horizonModem.generate();
//            txDevice.writeSamples(HorizonCommands.transmitIQ(iqData));
//
//            if (generatorIQServerMode == 2 && generatorIQServer != null) {
//                List<Sample> lastGeneratedSamples = horizonModem.getModulator().getLastResultSamples();
//                for(Sample sample: lastGeneratedSamples) {
//
//                    generatorIQServer.writeSample(sample);
//                }
//            }
//        }


    public static void close() {
        if (getInstance().dev[0] != null) {
            getInstance().dev[0].close();
            getInstance().dev[0] = null;
        }

    }
        private void writeBitToChannel(boolean[] packetData) {
            HorizonModulator modulator = horizonModem.getModulator();

            for(int i = 0; i < packetData.length; i++) {
                boolean bit = packetData[i];
//                if (txInverse) {
//                    bit = !bit;
//                }
                modulator.addChannelData(0, bit);
            }
        }



    private void closeAndSetupConfig() {
        //Close device(s) firstly
        close();

        WorkMode workMode = WorkMode.valueOf(Params.SETTINGS.getString("work_mode", "dss"));

        //Setup global settings - channel count, single channel speed
        config = new HorizonChannelConfig(workMode.getSpeed(), 1);

        //Setup modem
        horizonModem = new HorizonModem();
        horizonModem.config(config);
        //horizonModem.addDemodulatorListener(bitMachine);

        //Setup modulator
        HorizonChannelConfig modemConfig = config.copy();
        horizonModem.getModulator().config(modemConfig);

//        if (WRITE_DEBUG) {
//            horizonModem.addDemodulatorListener((channel, data) -> {
//                for(int i = 0; i < data.length; i++) {
//                    System.out.print(data[i] ? 1 : 0);
//
//                    count++;
//                    if (count >= 20) {
//                        count = 0;
//                        System.out.println();
//                    }
//                }
//
//            });
//        }

    }



    }

