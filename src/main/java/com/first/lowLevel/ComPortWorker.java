package com.first.lowLevel;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ComPortWorker implements SerialPortEventListener {

//    private FirstWindowController firstWindowController;
//
//    public void setFirstWindowController(FirstWindowController firstWindowController) {
//        this.firstWindowController = firstWindowController;
//    }
    SerialPort port;
    public static ConcurrentLinkedQueue<int[]> queue = new ConcurrentLinkedQueue<int[]>();






    public  void connect(String portName, int baudRate) {

        port = new SerialPort(portName);

        try {
            port.openPort();
            port.setParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.setRTS(true);
            port.setDTR(true);
            //Устанавливаем ивент лисенер и маску
            ///port.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

            port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);


            port.addEventListener(this, SerialPort.MASK_RXCHAR);

        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                port.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }

        port = null;
    }


   public  byte[] readBytes(){
       byte [] buffer={};
       try {
            buffer=port.readBytes();
       } catch (SerialPortException e) {
           e.printStackTrace();
       }
       return buffer;
   }

    public boolean isConnected() {
        if (port == null) {
            return false;
        }

        return port.isOpened();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && (serialPortEvent.getEventValue() > 0)) {
            try {
               int[] buffer = port.readIntArray();

                if (buffer != null) {

                    queue.offer(buffer);
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }



}