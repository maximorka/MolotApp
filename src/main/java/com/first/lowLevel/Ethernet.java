package com.first.lowLevel;

import com.first.lowLevel.command.HorizonCommands;
import javafx.scene.control.Alert;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Ethernet extends HorizonDevice {

    private WorkingThread workingThread;
    private ServerThread serverThread;
    private ServerReadWriteThread serverReadWriteThread;
    private  Socket connectionSocket;
    private ServerSocket serverSocket;
    public int isConnectedEth=0;
    private String ip;
    private int port;
    class ServerThread extends Thread{

        private InputStream input;
        private byte[] inputBuffer;
        private byte[] outputBuffer;
        private OutputStream output;
        private boolean runningServer = true;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(80);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (runningServer){
                try {
                    //serverReadWriteThread.close();
                    connectionSocket = serverSocket.accept();
                    System.out.println("Client connect");
                    serverReadWriteThread = new ServerReadWriteThread();
                    serverReadWriteThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void close() {
            runningServer = false;
            if (connectionSocket != null) {
                try {
                    connectionSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class ServerReadWriteThread extends Thread{

        private InputStream input;
        private byte[] inputBuffer;
        private byte[] outputBuffer;
        private OutputStream output;
        private boolean running = true;

        @Override
        public void run() {
            System.out.println("Server started, input output");
            try {
                input = connectionSocket.getInputStream();
                inputBuffer = new byte[2048];
                outputBuffer = new byte[2048];
                output = connectionSocket.getOutputStream();
            } catch (IOException e) {
                // e.printStackTrace();
            }
            while (running){

                if(isConnectedEth==0){
                    close();
                    continue;
                }
                try {
                    System.out.println("HI");
                    int data = input.available();
                    //System.out.println(data);
                    if (data > 0) {
                        int dataSize = input.read(inputBuffer);
                        for (int i = 0; i < dataSize; i++) {
                            //receiver.machineState.write(inputBuffer[i]);

                            System.out.println((inputBuffer[i]));
                        }
                    }

                    //data = receiver.byteSend.size();
                   // if (!receiver.byteSendQ.isEmpty()) {
                        //byte toSend = receiver.byteSendQ.poll();
                       // output.write(55);
                      //  System.out.println("send client");
                    //}


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void close() {
            running = false;
            if (connectionSocket != null) {
                try {
                    connectionSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



  class WorkingThread extends Thread{

      private Socket socket;
      private InputStream input;
      private byte[] inputBuffer;
      private byte[] outputBuffer;
      private OutputStream output;
      private boolean running = true;

      public WorkingThread(Socket socket) {

          this.socket = socket;
          try {
              input = socket.getInputStream();
              inputBuffer = new byte[2048];

              output = socket.getOutputStream();

          } catch (IOException e) {
              e.printStackTrace();
          }
      }

      public void close() {

          running = false;
          if (socket != null) {
              try {
                  socket.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      @Override
      public void run() {
          while (running){
              if(isConnectedEth==0){
                  close();
                  continue;
              }
                  try {
                    readData();
                  } catch (IOException e) {
                     // notifyAboutConnectStateChange(false);
                      e.printStackTrace();
                  }
              try {
                  Thread.sleep(15);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
      }


      private void readData() throws IOException {
          int data = input.available();
          if (data > 0) {
              //lastReceiveTime = System.currentTimeMillis();
              connected = true;

              int dataSize = input.read(inputBuffer);
              for(int i = 0; i < dataSize; i++) {
                  machineState.write(inputBuffer[i]);
                  //horizonCommandFSM.addByte(inputBuffer[i]);
              }
          }
      }
  }


    @Override
    protected boolean doInit() {
        ip = options.get("ip");
        port = Integer.parseInt(options.get("port"));
        return initSocketThread();
    }
    public boolean initSocketThread(){
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip,port),5_000);

            workingThread = new WorkingThread(socket);
            workingThread.start();
            isConnectedEth=1;
            writeBytes(HorizonCommands.setEthernetInterface());

            //lastReceiveTime = System.currentTimeMillis();
            //reconnectThread = new ReconnectThread();
            //reconnectThread.start();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

//    public void doInit() {
//            serverThread = new ServerThread();
//            serverThread.start();
//            // serverReadWriteThread = new ServerReadWriteThread();
//            //serverReadWriteThread.start();
//            isConnectedEth=1;
//
//    }

    public  void infoBox(String infoMessage, String titleBar, String headerMessage)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }


    public boolean closeServer(){
        try {
            serverThread.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  true;
    }

    public void doClose() {
        if (workingThread != null) {
            System.out.println("do");
            workingThread.close();
        }
    }
    protected void writeBytes(byte[] data) {

        if (workingThread != null) {
            try {
                workingThread.output.write(data);
                workingThread.output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
