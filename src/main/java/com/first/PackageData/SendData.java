package com.first.PackageData;

import com.first.decoder.Kasami;
//import com.molot.lowlevel.rw.ReaderWriter;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by pc3 on 25.03.2020.
 */
public class SendData {
    private static volatile boolean flag = true;
    //new ConcurrentLinkedQueue<String>();
    public static Queue<Boolean> byteSendQ =  new ConcurrentLinkedQueue<Boolean>();
    private static PriorityQueue<Boolean> bitData = new PriorityQueue<>();

    private boolean[] maskNumberCorespondent = Kasami.Kasami_31[10];// Команда номера кореспондента
    private boolean[] maskFrequencyGroup = Kasami.Kasami_31[11];// Команда перевідної групи
    private boolean[] maskComand = Kasami.Kasami_31[12];// Команда Команди
    //private boolean[] maskServiceMessage = Kasami.Kasami_31[13];// Команда службового повідомлення
    private boolean[] maskRagiogram = Kasami.Kasami_31[14];// Команда радіограми
    private boolean[] maskSinh = Kasami.Kasami_31[15];// Синхро

    private byte maskServiceMessage = 13;
    private byte maskrRadiogr = 14;

    private static int countCadr = 0;
    private String numberCorespondent;
    private String frequencyTx;
    private int command;
    private String serviceMessage;
    private String urlRadiogram;

    public SendData() {}

    public String getNumberCorespondent() {
        return numberCorespondent;
    }

    public void setNumberCorespondent(String numberCorespondent) {
        this.numberCorespondent = numberCorespondent;
    }

    public String getFrequencyTx() {
        return frequencyTx;
    }

    public void setFrequencyTx(String frequencyTx) {
        this.frequencyTx = frequencyTx;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getServiceMessage() {
        return serviceMessage;
    }

    public void setServiceMessage(String serviceMessage) {
        this.serviceMessage = serviceMessage;
    }

    public String getUrlRadiogram() {
        return urlRadiogram;
    }

    public void setUrlRadiogram(String urlRadiogram) {
        this.urlRadiogram = urlRadiogram;
    }

    private int repeat = 2;


    public void parsData() {
        Thread sending = new Thread(() -> {
            int numberCor = Integer.parseInt(numberCorespondent);

//ПЕРЕВІДНА ЧАСТОТА
            if (frequencyTx != null) {
                int freqTx = Integer.parseInt(frequencyTx);
                for (int i = 0; i < repeat; i++) {
                    createCadr(numberCor, freqTx);
                }
            }
//КОМАНДА
            if (command > 0 && serviceMessage == null && urlRadiogram == null) {
                for (int i = 0; i < repeat; i++) {
                    createCadr(numberCor,command,true);
                }
            }
//СЛУЖБОВЕ ПОВІДОМЛЕННЯ
            if (serviceMessage != null) {
                String messageText = serviceMessage.replaceAll(" ", "");
                int len = messageText.length();
                byte[] message = new byte[len+1];
                message[0]=maskServiceMessage;

                for (int i = 0; i < len; i++) {
                    int a = Integer.parseInt(String.valueOf(messageText.charAt(i)));
                    message[i+1] = (byte) a;
                }

                createCadr(numberCor, message);
                createCadr(numberCor, 111);// не потрібно
            }

//РАДІОГРАМА
            if (urlRadiogram != null) {
                String messageText = serviceMessage.replaceAll(" ", "");
                int len = messageText.length();
                byte[] message = new byte[len+1];
                message[0]=maskrRadiogr;

                for (int i = 0; i < len; i++) {
                    int a = Integer.parseInt(String.valueOf(messageText.charAt(i)));
                    message[i+1] = (byte) a;
                }

                createCadr(numberCor, message);
                createCadr(numberCor, 111);// не потрібно
            }
        }
        );

            sending.start();
        try {
            sending.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createCadr(int numberCor, int freq){
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskFrequencyGroup, freq);
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskFrequencyGroup, freq);
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskFrequencyGroup, freq);
        createFrame(maskNumberCorespondent, numberCor);
    }
    public void createCadr(int numberCor, int comand, boolean onlyComandFlag){
        createFrame(maskNumberCorespondent, numberCor,maskComand,comand);
        createFrame(maskNumberCorespondent, numberCor,maskComand,comand);
        createFrame(maskNumberCorespondent, numberCor,maskComand,comand);
        createFrame(maskNumberCorespondent, numberCor,maskComand,comand);
        createFrame(maskNumberCorespondent, numberCor,maskComand,comand);
        createFrame(maskNumberCorespondent, numberCor,maskComand,comand);
        createFrame(maskNumberCorespondent, numberCor,maskComand,comand);
    }
    public void createCadr(int numberCor){
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskRagiogram);
        createFrame(maskRagiogram);
        createFrame(maskRagiogram);
    }

    public void createCadr(int numberCor,byte[] message ){//SERVICE MESSAGE, RADIOGRAM
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(maskNumberCorespondent, numberCor);
        createFrame(message);
    }
    public void createFrame (boolean[] comand, int data){
        boolean[] dataTx;
        byte DecThFr = (byte) (data / 10000);
        byte ThFr = (byte) ((data - DecThFr * 10000) / 1000);
        byte SotFR = (byte) ((data - DecThFr * 10000 - ThFr * 1000) / 100);
        byte DecFr = (byte) ((data - DecThFr * 10000 - ThFr * 1000 - SotFR * 100) / 10);
        byte OdFr = (byte) ((data - DecThFr * 10000 - ThFr * 1000 - SotFR * 100 - DecFr * 10));

        for (int i = 0; i < maskSinh.length; i++) {
            byteSendQ.add(maskSinh[i]);
        }
        for (int i = 0; i < comand.length; i++) {
            byteSendQ.add(comand[i]);
        }
        dataTx = Kasami.Kasami_31[DecThFr];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
        dataTx = Kasami.Kasami_31[ThFr];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
        dataTx = Kasami.Kasami_31[SotFR];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
        dataTx = Kasami.Kasami_31[DecFr];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);

        }
        dataTx = Kasami.Kasami_31[OdFr];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
    }
    public void createFrame (boolean[] comand1, int data1, boolean[] comand2, int data2){
        boolean[] dataTx;
        byte Sot = (byte) (data1 / 100);
        byte Dec = (byte) ((data1 - Sot * 100) / 10);
        byte Od = (byte) ((data1 - Sot * 100 - Dec * 10));

        for (int i = 0; i < maskSinh.length; i++) {
            byteSendQ.add(maskSinh[i]);
        }
        for (int i = 0; i < comand1.length; i++) {
            byteSendQ.add(comand1[i]);
        }
        dataTx = Kasami.Kasami_31[Sot];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
        dataTx = Kasami.Kasami_31[Dec];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
        dataTx = Kasami.Kasami_31[Od];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
        for (int i = 0; i < comand2.length; i++) {
            byteSendQ.add(comand2[i]);
        }
        dataTx = Kasami.Kasami_31[data2];
        for (int i = 0; i < dataTx.length; i++) {
            byteSendQ.add(dataTx[i]);
        }
    }
    public void createFrame (byte[] data){
        boolean[] dataTx;
        int count = 0;

        for (int j = 0; j < data.length; ) {
            if (0 == (j % 6)) {
                for (int i = 0; i < maskSinh.length; i++) {
                    byteSendQ.add(maskSinh[i]);
                }
                count++;
            }
            dataTx = Kasami.Kasami_31[data[j++]];
            for (int i = 0; i < dataTx.length; i++) {
                byteSendQ.add(dataTx[i]);
            }
            count++;
        }
        int need = count % 7;
        for (int j = 0; j < 7 - need; j++) {
            dataTx = Kasami.Kasami_31[0];
            for (int i = 0; i < dataTx.length; i++) {
                byteSendQ.add(dataTx[i]);
            }
        }
    }
    public void createFrame (boolean[] comand){
        boolean[] dataTx;

        for (int i = 0; i < maskSinh.length; i++) {
            byteSendQ.add(maskSinh[i]);
        }
        for (int i = 0; i < comand.length; i++) {
            byteSendQ.add(comand[i]);
        }
        for (int i = 0; i < comand.length; i++) {
            byteSendQ.add(comand[i]);
        }
        for (int i = 0; i < comand.length; i++) {
            byteSendQ.add(comand[i]);
        }
        for (int i = 0; i < comand.length; i++) {
            byteSendQ.add(comand[i]);
        }
        for (int i = 0; i < comand.length; i++) {
            byteSendQ.add(comand[i]);
        }
        for (int i = 0; i < comand.length; i++) {
            byteSendQ.add(comand[i]);
        }
    }

    public static int getCountCadr(){
        return countCadr;
    }
}
