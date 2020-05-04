package com.first.lowLevel.command;

import com.first.lowLevel.Sample;
import com.first.lowLevel.enums.DeviceType;
import com.first.util.ByteUtils;
import com.first.util.EthernetUtils;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

import java.util.ArrayList;
import java.util.List;

public class HorizonCommandFSM {
    private static final int STATE_WAIT_CMD = 0;
    private static final int STATE_RECEIVE_CMD = 1;

    private List<Byte> waitForCommandData = new ArrayList<>(4);
    private List<Byte> commandData = new ArrayList<>();

    private int state;

    private HorizonCommand currentCommand;

    private DeviceType deviceType = DeviceType.virial;

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;

        waitForCommandData.clear();
        commandData.clear();

        state = STATE_WAIT_CMD;
    }

    private List<HorizonEventListener> eventListeners = new ArrayList<>();


    public void addEventListener(HorizonEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void reset() {
        state = STATE_WAIT_CMD;
    }

    public void addByte(byte value) {
        if (deviceType == null) {
            return;
        }

        switch (state) {
            case STATE_WAIT_CMD:
                addByteWhileWaitingCommand(value);
                break;
            case STATE_RECEIVE_CMD:
                addByteWhileReceivingCommand(value);
                break;
        }

    }

    private void addByteWhileWaitingCommand(byte value) {
        if (waitForCommandData.size() >= 4) {
            waitForCommandData.remove(0);
        }

        waitForCommandData.add(value);

        if (waitForCommandData.size() == 4) {
            boolean isCommand = true;

            for(int i = 0; i < 3; i++) {
                if (HorizonCommands.COMMAND_MASK[i] != waitForCommandData.get(i + 1)) {
                    isCommand = false;
                }
            }

            if (isCommand) {
                currentCommand = HorizonCommand.getCommandByByte(waitForCommandData.get(0));

                commandData.clear();
                state = STATE_RECEIVE_CMD;
            }
        }
    }

    private List<Sample> makeIQFromData() {
        List<Sample> result = new ArrayList<>();

        try {
            int sampleCount = 64;
            int byteOffset = deviceType.getRxIQByteCount() * 2;

            for(int i = 0; i < sampleCount; i++) {
                int offset = byteOffset * i;

                float I;
                float Q;

                if (byteOffset == 4) { //Для Горизонта
                    I = Shorts.fromBytes(commandData.get(offset + 1), commandData.get(offset)) / (float) 32768f;
                    Q = Shorts.fromBytes(commandData.get(offset + 3), commandData.get(offset + 2)) / (float) 32768f;
                } else { //Для віріала
                    I = (float) convertInt24(commandData.get(offset), commandData.get(offset + 1), commandData.get(offset + 2));
                    Q = (float)convertInt24(commandData.get(offset + 3), commandData.get(offset + 4), commandData.get(offset + 5));
                }

                if (Math.abs(I) > 1 || Math.abs(Q) > 1) {
                    System.out.println("BAD IQ: " + I + ", " + Q);
                }

                result.add(new Sample(I, Q));
            }
        } catch (Exception ex) {

        }

        return result;
    }

    //b3 - high
    private double convertInt24(byte b1, byte b2, byte b3) {
        int intValue;

        if (b3 < 0) {
            intValue = Ints.fromBytes((byte) -1, b3, b2, b1);
        } else {
            intValue = Ints.fromBytes((byte) 0, b3, b2, b1);
        }

        return (double) intValue / 8388608d;
    }

    private void addByteWhileReceivingCommand(byte value) {
        commandData.add(value);
        int iqByteCount = 64 * deviceType.getRxIQByteCount() * 2;

        if (currentCommand == HorizonCommand.rx_iq && commandData.size() == iqByteCount) {
            List<Sample> samples = makeIQFromData();
            for (HorizonEventListener eventListener : eventListeners) {
                eventListener.iqReceived(samples);
            }

            state = STATE_WAIT_CMD;
            waitForCommandData.clear();
        } else if (currentCommand.getDataSizeInBytes() == commandData.size()) {
            if (currentCommand.getDataSizeInBytes() == 4) {
                byte[] cmdValue = new byte[4];
                for (int i = 0; i < 4; i++) {
                    cmdValue[i] = commandData.get(3 - i);
                }
                int cmdValueInt = ByteUtils.getInt(cmdValue);
                for (HorizonEventListener eventListener : eventListeners) {
                    eventListener.commandReceived(currentCommand, cmdValueInt);
                }

                state = STATE_WAIT_CMD;
                waitForCommandData.clear();
            } else if (currentCommand == HorizonCommand.get_ethernet_params) {
                String ip = "";
                String mask = "";
                int port = 0;
                String gateway = "";

                for (int i = 0; i < 4; i++) {
                    int offset = i * 4;

                    byte[] cmdValue = new byte[4];
                    for (int j = 0; j < 4; j++) {
                        cmdValue[j] = commandData.get(offset + j);
                    }

                    invertBytes(cmdValue);
                    int cmdValueInt = ByteUtils.getInt(cmdValue);

                    switch (i) {
                        case 0:
                            ip = EthernetUtils.convertIntToStringIP(cmdValueInt);
                            break;
                        case 1:
                            mask = EthernetUtils.convertIntToStringIP(cmdValueInt);
                            break;
                        case 2:
                            port = cmdValueInt;
                            break;
                        case 3:
                            gateway = EthernetUtils.convertIntToStringIP(cmdValueInt);
                            break;
                    }
                }

                for (HorizonEventListener eventListener : eventListeners) {
                    eventListener.ethernetParamsReceived(ip, mask, port, gateway);
                }

                state = STATE_WAIT_CMD;
                waitForCommandData.clear();

            }

        }
    }

    public void invertBytes(byte[] data) {
        byte b0 = data[0];
        byte b1 = data[1];
        byte b2 = data[2];
        byte b3 = data[3];

        data[0] = b3;
        data[1] = b2;
        data[2] = b1;
        data[3] = b0;
    }

    public static void main(String[] args) {
        HorizonCommandFSM fsm = new HorizonCommandFSM();
//        0x00, 0x00, 0x80,	/* I, sign minus */
//                0xff, 0xff, 0x7f	/* Q, sign plus */
        System.out.println(fsm.convertInt24((byte) 0x00, (byte) 0x00, (byte) 0x80));
    }

    public interface HorizonEventListener {
        void ethernetParamsReceived(String ip, String mask, int port, String gateway);
        void commandReceived(HorizonCommand command, int value);
        void iqReceived(List<Sample> iq);
    }
}
