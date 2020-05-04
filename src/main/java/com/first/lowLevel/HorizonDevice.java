package com.first.lowLevel;

import com.first.lowLevel.command.HorizonCommand;
import com.first.lowLevel.command.HorizonCommandFSM;
import com.first.lowLevel.command.HorizonCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class HorizonDevice implements MachineState.HorizonEventListener {
    /**
     * Розмір одного семпла на передачу, в байтах
     */
    public static final int TX_SINGLE_SAMPLE_SIZE_IN_BYTES = 3;

    /**
     * Розмір блока семплів на передачу (враховуючи команду)
     */
    public static final int TX_BLOCK_SAMPLE_SIZE_IN_BYTES = TX_SINGLE_SAMPLE_SIZE_IN_BYTES * 64;

    /**
     * Розмір буфера передавача, в семплах
     */
    public static final int TX_BUFFER_SAMPLE_COUNT = 32_768;

    /**
     * Розмір буфера передавача, в байтах
     */
    public static final int TX_BUFFER_SIZE = TX_BUFFER_SAMPLE_COUNT * TX_SINGLE_SAMPLE_SIZE_IN_BYTES;

    protected boolean connected;
    protected List<HorizonDeviceListener> eventListeners;
    protected HorizonCommandFSM horizonCommandFSM;
    protected MachineState machineState;
    protected List<HorizonDeviceListener> eventListenersVy;
    protected Map<String, String> options;

    //Дані для контролю передачі
    protected float txBufferPercent;
   // private Avarage tempAvarage = new Avarage(100);
    //protected HorizonChannelConfig config;
    protected volatile int sampleFreq = 48_000;
    protected volatile int sampleCountPer10ms = sampleFreq / 100;
    protected volatile int byteCountPer10ms = sampleCountPer10ms * 3;
    protected volatile int sampleCountPer1ms = sampleFreq / 1000;
    protected volatile int byteCountPer1ms = sampleCountPer10ms * 3;
    protected volatile byte[] tempByteBuffer;
    protected volatile int level1 = 70;
    protected volatile int level2 = 70;
    protected volatile int level3 = 70;
    protected volatile int level4 = 70;

    public String type = "Int16";
    private int txFreq, rxFreq;
    private boolean enableRx, enableTx;
//    private long start = 0;
//    private float accumByte = 0;
//    private long newdelaystart=0;
    private   boolean finishingWork=true;
//private long oldDelay=0;
    public HorizonCommandFSM getHorizonCommandFSM() {
        return horizonCommandFSM;
    }

    class WriteThread extends Thread {
        private boolean running = true;

        private Queue<Byte> ByteQ;
        private Queue<Byte> commandQ;
        private Queue<DataWrapper> samplesQ;

        WriteThread() {
            samplesQ = new ConcurrentLinkedQueue<>();
            commandQ = new ConcurrentLinkedQueue<>(); //ArrayDeque<>();
        }

        @Override
        public void run() {

            while (running) {
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                //Якщо є пріоритетні дані для передачі - спочатку передаємо їх
//                if (commandQ.size() > 0) {
//                    byte[] commandData = new byte[commandQ.size()];
//                    for (int i = 0; i < commandData.length; i++) {
//                        commandData[i] = commandQ.poll();
//                    }
//
//                    writeBytes(commandData);
//
//                    try {
//                        Thread.sleep(3);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    continue;
//                }
//
//                //Нічого не пишемо, якщо робота завершається
//                if (finishingWork) {
//
//                    if(HardwareSettingsPanel.checkVirial.isSelected()){
//                        writeCommand(HorizonCommands.sendHeardbyte());
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }else
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    continue;
//                }
//
//                //Нічого не пишемо, якщо не проініціалізовано
//                if (tempByteBuffer == null) {
//                    continue;
//                }
//
//                if( txBufferPercent>70){
//                    if(HardwareSettingsPanel.checkVirial.isSelected()) {
//                        try {
//                            writeCommand(HorizonCommands.sendHeardbyte());
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    continue;
//                }
//
//                long resTime = System.currentTimeMillis() - start;
//                start = System.currentTimeMillis();
//                //Визначаємо бажану потрібну кількість семплів
//                float wantedTrasmitByte = byteCountPer1ms * resTime;
//                if(wantedTrasmitByte<100000)
//                accumByte+=wantedTrasmitByte;
//                else
//                    accumByte+=byteCountPer1ms;
//
//                //Визначаємо бажану потрібну кількість семплів
//                int wantedLoopCount = (int)accumByte / TX_BLOCK_SAMPLE_SIZE_IN_BYTES;
//              //  if (byteCountPer10ms%TX_BLOCK_SAMPLE_SIZE_IN_BYTES != 0) {
//                //    wantedLoopCount++;
//                //}
//
//                if (sampleFreq <= 3000){
//                    level1 = 11;
//                    level2 = 12;
//                    level3 = 13;
//                    level4 = 14;
//                } else if (sampleFreq <= 6000){
//                    level1 = 11;
//                    level2 = 12;
//                    level3 = 13;
//                    level4 = 14;
//                } else if (sampleFreq <= 12000){
//                    level1 = 18;
//                    level2 = 20;
//                    level3 = 22;
//                    level4 = 24;
//                } else if (sampleFreq <= 24000){
//                    level1 = 37;
//                    level2 = 40;
//                    level3 = 43;
//                    level4 = 46;
//                } else {
//                    level1 = 65;
//                    level2 = 70;
//                    level3 = 75;
//                    level4 = 80;
//                }
//
//
////                if (txBufferPercent < level1) {
////                    wantedLoopCount++;
////                }
//                if (txBufferPercent > level2) {
//                    //System.out.println("over");
//                   continue;
//                }
//                System.out.println("accum:"+accumByte);
//                //Максимальна кількість семплів, яку можна відправити
//                int availableLoopCount = samplesQ.size();
//
//                //Відправляємо дані
//                int loopCount = Math.min(wantedLoopCount, availableLoopCount);
//                //System.out.print("DataSize:"+loopCount+" ");
//
//                for(int i = 0; i < loopCount; i++) {
//                    writeBytes(samplesQ.poll().data);
//                    accumByte-=192;
//                }
//
//               System.out.println("Speed: "+loopCount*192/(resTime)+" delay: "+(resTime)+" Data size: "+loopCount);

                long start = System.currentTimeMillis();
                //Якщо є пріоритетні дані для передачі - спочатку передаємо їх
                if (commandQ.size() > 0) {
                    byte[] commandData = new byte[commandQ.size()];
                    for (int i = 0; i < commandData.length; i++) {
                        commandData[i] = commandQ.poll();
                    }

                    writeBytes(commandData);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                //Нічого не пишемо, якщо робота завершається
                if (finishingWork) {
                    //System.out.println("HELLO");
//                    if(HardwareSettingsPanel.checkVirial.isSelected()){
//                        writeCommand(HorizonCommands.sendHeardbyte());
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }//else
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    continue;
                }

                //Нічого не пишемо, якщо не проініціалізовано
//                if (tempByteBuffer == null) {
//                    continue;
//                }

                if( txBufferPercent>60){
//                    if(HardwareSettingsPanel.checkVirial.isSelected()) {
//                        try {
//                            writeCommand(HorizonCommands.sendHeardbyte());
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    continue;
                }

                //Визначаємо бажану потрібну кількість семплів
                int wantedLoopCount = byteCountPer10ms / TX_BLOCK_SAMPLE_SIZE_IN_BYTES;
                if (byteCountPer10ms%TX_BLOCK_SAMPLE_SIZE_IN_BYTES != 0) {
                    wantedLoopCount++;

                }

                //Максимальна кількість семплів, яку можна відправити
                int availableLoopCount = samplesQ.size();

                //Відправляємо дані
                int loopCount = Math.min(wantedLoopCount, availableLoopCount);
                //System.out.print("DataSize:"+loopCount+" ");

                for(int i = 0; i < loopCount; i++) {

                   writeBytes(samplesQ.poll().data);
                    //System.out.println("Send");
                }

                float correctionCoefficient = 1f;
                int transmittedByteCount = loopCount * TX_BLOCK_SAMPLE_SIZE_IN_BYTES;
                if (transmittedByteCount > 0) {
                    correctionCoefficient = (float) transmittedByteCount / (float) byteCountPer10ms;
                }

                //Визначаємо затримку
                long delay = (long) (10f * correctionCoefficient);
               // long newdelaystop=System.currentTimeMillis()-newdelaystart;
//long res = newdelaystop-oldDelay;
//delay -=res;
                //newdelaystart=System.currentTimeMillis();
                //Коригуємо час, який ми витратили на виконання попереднього коду
                long executionTime = System.currentTimeMillis() - start;
                //start = System.currentTimeMillis();
                delay -= executionTime;
                if (sampleFreq <= 3000){
                    level1 = 11;
                    level2 = 12;
                    level3 = 13;
                    level4 = 14;
                } else if (sampleFreq <= 6000){
                    level1 = 11;
                    level2 = 12;
                    level3 = 13;
                    level4 = 14;
                } else if (sampleFreq <= 12000){
                    level1 = 18;
                    level2 = 20;
                    level3 = 22;
                    level4 = 24;
                } else if (sampleFreq <= 24000){
                    level1 = 37;
                    level2 = 40;
                    level3 = 43;
                    level4 = 46;
                } else {
                    level1 = 65;
                    level2 = 70;
                    level3 = 75;
                    level4 = 80;
                }
                if (txBufferPercent < level1) {
                    delay -= 4;
                }

                if (txBufferPercent < level2) {
                    delay -= 2;
                }

                if (txBufferPercent > level3) {
                    delay += 1;
                }

                if (txBufferPercent > level4) {
                    delay += 3;
                }

                // Не потрібно вводити затримку меншу, ніж 3 мс
                if (delay < 3) {
                    delay = 3;
                }

                try {
                    //System.out.println("res"+res+"ex"+newdelaystop+"Speed: "+loopCount*192/(delay)+" delay: "+(delay)+" Data size: "+loopCount+" Avarage : "+tempAvarage.getAvarage(loopCount*192/delay));
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private WriteThread writeThread;

    public HorizonDevice config() {
       // this.config = config;

       // horizonCommandFSM.setDeviceType(config.getDeviceType());

       // sampleFreq = config.calculateTotalHorizonWidth().getValue();
        sampleCountPer10ms = sampleFreq / 100;
        byteCountPer10ms = sampleCountPer10ms * 3;
        sampleCountPer1ms = sampleFreq / 1000;
        byteCountPer1ms = sampleCountPer1ms * 3;
        tempByteBuffer = new byte[byteCountPer10ms];

        return this;
    }

    public HorizonDevice() {
        eventListenersVy = new ArrayList<>();
        machineState = new MachineState(type);
        machineState.addEventListener(this);
        //horizonCommandFSM = new HorizonCommandFSM();
        //horizonCommandFSM.addEventListener(this);

        writeThread = new WriteThread();
        writeThread.start();

    }

    public void addEventListener(HorizonDeviceListener eventListener) {
        eventListenersVy.add(eventListener);

        machineState.addEventListener(eventListener);
    }

    public void init(Map<String, String> options) {
        this.options = options;

        connected = doInit();
        for (HorizonDeviceListener deviceListener : eventListenersVy) {
            deviceListener.connectStateChanged(connected);
        }
    }

    protected abstract boolean doInit();

    public void close() {
        connected = false;

        try {
            if (writeThread != null) {
                writeThread.running = false;
            }

            doClose();
        } finally {
            for (HorizonDeviceListener deviceListener : eventListenersVy) {
                deviceListener.connectStateChanged(false);
            }
        }
    }

    protected void notifyAboutConnectStateChange(boolean connected) {
        for (HorizonDeviceListener eventListener : eventListenersVy) {
            eventListener.connectStateChanged(connected);
        }
    }

    public float getTxBufferPercent() {
        return txBufferPercent;
    }

    @Override
    public void commandReceived(HorizonCommand command, int value) {

        if (command == HorizonCommand.tx_get_buffer_percent) {
            txBufferPercent = value;

        }
    }

    @Override
    public void iqReceived(List<Sample> iq) {

    }

    @Override
    public void ethernetParamsReceived(String ip, String mask, int port, String gateway) {

    }

    public boolean isConnected() {
        return connected;
    }

    public void writeSamples(byte[] bytes) {
        DataWrapper wrapper = new DataWrapper();
        wrapper.data = bytes;
        writeThread.samplesQ.add(wrapper);
    }

    public void writeCommand(byte[] bytes) {
        for (byte b : bytes) {
            writeThread.commandQ.add(b);
        }
    }

    protected abstract void doClose();

    protected abstract void writeBytes(byte[] data);

    public boolean canWriteSamples() {
        return writeThread.samplesQ.size() < (TX_BUFFER_SAMPLE_COUNT / 64 / 8); // Має бути запас хоча б в 12% буфера
    }

    public void setTxRxState(boolean enableTx, boolean enableRx) {
        this.enableTx = enableTx;
        this.enableRx = enableRx;
    }



    protected void applyConfig() {
//        if (debugMode == null) {
//            return;
//        }

        //Enable debug
        //writeCommand(HorizonCommands.debug(debugMode));

        //Set device sampling frequency
       // writeCommand(HorizonCommands.setRxDemodulationWidth(config.calculateTotalHorizonWidth()));
       // writeCommand(HorizonCommands.setTxModulationWidth(config.calculateTotalHorizonWidth()));

        //Set RX TX
        writeCommand(HorizonCommands.setTxModulationFrequency(txFreq));
        writeCommand(HorizonCommands.setRxDemodulationFrequency(rxFreq));

        if (enableTx) {
            writeCommand(HorizonCommands.enableTx());
        } else {
            writeCommand(HorizonCommands.disableTx());
        }

        if (enableRx) {
            writeCommand(HorizonCommands.enableRx());
        } else {
            writeCommand(HorizonCommands.disableRx());
        }
    }

    public void setFinishingWork(boolean finishingWork) {
        this.finishingWork = finishingWork;
    }

    class DataWrapper {
        public byte[] data;
    }

}
