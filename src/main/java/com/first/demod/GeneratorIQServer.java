package com.first.demod;


import com.first.Core;
import com.first.lowLevel.Sample;
import com.first.lowLevel.command.HorizonCommands;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class GeneratorIQServer {
    private List<Socket> sockets = new CopyOnWriteArrayList<>();
    private AcceptConnectionThread acceptConnectionThread;
    private ServerWriteThread writeThread;

    public GeneratorIQServer(int port) throws IOException {
        startServerSocket(port);

        writeThread = new ServerWriteThread();
        writeThread.start();
    }

    private void startServerSocket(int port) {
        closeSockets();

        if (acceptConnectionThread != null) {
            acceptConnectionThread.close();
        }

        acceptConnectionThread = new AcceptConnectionThread(port);
        acceptConnectionThread.start();
    }

    class AcceptConnectionThread extends Thread {
        private ServerSocket serverSocket;
        private boolean running = true;

        public void close() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
        }

        public AcceptConnectionThread(int port) {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (running) {
                try {
                    if (serverSocket == null || serverSocket.isClosed()) {
                        return;
                    }

                    sockets.add(serverSocket.accept());
                    System.out.println("socket accepted, count: " + sockets.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!running) {
                    closeSockets();
                }
            }
        }
    }


    public void writeSample(Sample sample) {
        if (writeThread != null && writeThread.sampleQ.size() < 2048) {
            writeThread.sampleQ.add(sample);
        }
    }

    public void setPort(int port) {
        startServerSocket(port);
    }

    public void close() {
        if (writeThread != null) {
            writeThread.close();
            writeThread = null;
        }

        if (acceptConnectionThread != null) {
            acceptConnectionThread.close();
            acceptConnectionThread = null;
        }

        closeSockets();
    }

    private void closeSockets() {
        try {
            for (Socket socket : sockets) {
                if (!socket.isClosed()) {
                    socket.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sockets.clear();
    }

    class ServerWriteThread extends Thread {
        private int writeBufferLength = 4 + 64 * 8;
        private byte[] writeBuffer = new byte[writeBufferLength];
        private boolean running = true;

        private Queue<Sample> sampleQ = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {
            while (running) {
                if (sampleQ.size() < 64) {
                    continue;
                }

                //Write command
                writeBuffer[0] = 0;
                System.arraycopy(HorizonCommands.COMMAND_MASK, 0,
                        writeBuffer, 1,
                        3);

                //Write samples
                for(int i = 0; i < 64; i++) {
                    int offset = 4 + i * 8;
                    Sample sample = sampleQ.poll();
                    byte[] sampleData = ByteBuffer.allocate(8)
                            .putFloat((float) sample.getI())
                            .putFloat((float) sample.getQ())
                            .array();

                    //Write I
                    writeBuffer[offset] = sampleData[3];
                    writeBuffer[offset + 1] = sampleData[2];
                    writeBuffer[offset + 2] = sampleData[1];
                    writeBuffer[offset + 3] = sampleData[0];

                    //Write Q
                    writeBuffer[offset + 4] = sampleData[7];
                    writeBuffer[offset + 5] = sampleData[6];
                    writeBuffer[offset + 6] = sampleData[5];
                    writeBuffer[offset + 7] = sampleData[4];
                }

                for (Socket socket : sockets) {
                    try {
                        socket.getOutputStream().write(writeBuffer);
                        socket.getOutputStream().flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Remove socket");
                        Core.getInstance().generatorIQServerMode = 0;
                        sockets.remove(socket);
                        break;
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        public void close() {
            running = false;
        }
    }
}
