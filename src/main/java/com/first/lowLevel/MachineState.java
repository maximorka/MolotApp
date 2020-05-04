package com.first.lowLevel;

import com.first.lowLevel.command.HorizonCommand;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by User on 24.06.2019.
 */
public class MachineState {

    private byte[] ByteCollecter = new byte[1024];
    public int size = 4800;
    private int globalCount = 0;
  //  private int state = 1;
    int code = 0;
    private int countByte = 0;
    public int state = 0x20;

    private int controlBytes = 0x00563412;
    private int flagCode = 0;
    public int DataType;
//StateAction stateAction = new StateAction();
    private ConvertAction[] convertActions;
    private StateAction[] StateActions;

    public ConcurrentLinkedQueue<Sample> buf = new ConcurrentLinkedQueue<Sample>();
    public ConcurrentLinkedQueue<Double> buf_re = new ConcurrentLinkedQueue<Double>();
    public ConcurrentLinkedQueue<Double> buf_im = new ConcurrentLinkedQueue<Double>();


    public MachineState(String type) {
        System.out.println(type);
        if (type == "Byte") DataType = 0;
        else if (type == "Int16") DataType = 1;
        else if (type == "Int24") DataType = 2;
        else if (type == "Int32") DataType = 3;
        else if (type == "Float") DataType = 4;
        else if (type == "Double") DataType = 5;

        convertActions = new ConvertAction[6];
        convertActions[0] = new ByteConvertAction();
        convertActions[1] = new ShortConvertAction();
        convertActions[2] = new Int24ConvertAction();
        convertActions[3] = new IntConvertAction();
        convertActions[4] = new FloatConvertAction();
        convertActions[5] = new DoubleConvertAction();

        for (int i = 0; i < convertActions.length; i++) {
            convertActions[i].setMachineState(this);
        }

        StateActions = new StateAction[33];
        StateActions[0] = new State0();
        StateActions[1] = new State1();
        StateActions[2] = new State2();
        StateActions[3] = new State3();
        StateActions[4] = new State4();
        StateActions[5] = new State5();
        StateActions[6] = new State6();
        StateActions[7] = new State7();
        StateActions[8] = new State8();
        StateActions[9] = new State9();
        StateActions[10] = new State10();
        StateActions[11] = new State11();
        StateActions[12] = new State12();
        StateActions[13] = new State13();
        StateActions[14] = new State14();
        StateActions[15] = new State15();
        StateActions[16] = new State16();
        StateActions[17] = new State17();
        StateActions[18] = new State18();
        StateActions[19] = new State19();
        StateActions[20] = new State20();
        StateActions[21] = new State21();
        StateActions[22] = new State22();
        StateActions[23] = new State23();
        StateActions[24] = new State24();
        StateActions[25] = new State25();
        StateActions[26] = new State26();
        StateActions[27] = new State27();
        StateActions[28] = new State28();
        StateActions[29] = new State29();
        StateActions[30] = new State30();
        StateActions[31] = new State31();
        StateActions[32] = new State32();

        for (int i = 0; i < StateActions.length; i++) {
            StateActions[i].setMachineState(this);
        }
    }


    private List<HorizonEventListener> eventListeners = new ArrayList<>();
    public void addEventListener(HorizonEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void write(byte source)
    {
        StateActions[state].state(source);
    }
    public void state0(byte source)
    {
        ByteCollecter[countByte++] = source;
        convertActions[DataType].convert();
    } // [0x00] IQ
    public void state1(byte source)
    {
        state = 0x20;
        StateActions[countByte].state(source);
    } // [0x01] Not used
    public void state2(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            System.out.println("Mode demodulation is " +ByteCollecter[0]);
        }
    } // [0x02] Get mode demodulation
    public void state3(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x03] Not used
    public void state4(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            System.out.println("Width demodulation is " +ByteCollecter[0]);
        }
    } // [0x04] Get width demodulation
    public void state5(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x05] Not used
    public void state6(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            int ip0 = ByteCollecter[0] & 0xff;
            int ip1 = ByteCollecter[1] & 0xff;
            int ip2 = ByteCollecter[2] & 0xff;
            int ip3 = ByteCollecter[3] & 0xff;
            int freq = ip0|(ip1<<8)|(ip2<<16)|(ip3<<24);
            System.out.println("Frequency modulation is " + freq);
        }
    } // [0x06] Get frequency demodulation
    public void state7(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x07] Not used
    public void state8(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x08] Not used
    public void state9(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x09] Not used
    public void state10(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            System.out.println("Mode modulation is " +ByteCollecter[0]);
        }
    } // [0x0A] Get mode modulation
    public void state11(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x0B] Not used
    public void state12(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            System.out.println("Width modulation is " + ByteCollecter[0]);
        }
    } // [0x0C] Get width modulation
    public void state13(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x0D] Not used
    public void state14(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;

        }
    } // [0x0E] Get frequency modulation
    public void state15(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x0F] Not used
    public void state16(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x10] Not used
    public void state17(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            for (HorizonEventListener eventListener : eventListeners) {
                eventListener.commandReceived(HorizonCommand.tx_get_buffer_percent,ByteCollecter[0]);
            }


        }
    } // [0x11] Get buffer percent modulation
    public void state18(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x12] Not used
    public void state19(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x13] Not used
    public void state20(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            System.out.println("Mode interface is " + ByteCollecter[0]);
        }
    } // [0x14] Get mode interface
    public void state21(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x15] Not used
    public void state22(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 16)
        {
            countByte = 0;
            state = 0x20;
            int ip0 = ByteCollecter[0] & 0xff;
            int ip1 = ByteCollecter[1] & 0xff;
            int ip2 = ByteCollecter[2] & 0xff;
            int ip3 = ByteCollecter[3] & 0xff;

            int mask0 = ByteCollecter[4] & 0xff;
            int mask1 = ByteCollecter[5] & 0xff;
            int mask2 = ByteCollecter[6] & 0xff;
            int mask3 = ByteCollecter[7] & 0xff;

            int port0 = ByteCollecter[8] & 0xff;
            int port1 = ByteCollecter[9] & 0xff;

            int gat0 = ByteCollecter[12] & 0xff;
            int gat1 = ByteCollecter[13] & 0xff;
            int gat2 = ByteCollecter[14] & 0xff;
            int gat3 = ByteCollecter[15] & 0xff;
            System.out.println("IP: " + ip3 + "." +ip2 + "." +ip1 + "." + ip0);
            System.out.println("MASK: " + mask3 + "." + mask2 + "." + mask1 + "." + mask0);
            System.out.println("PORT: " + port1 + "." + port0);
            System.out.println("GATEWAY: " + gat3 + "." + gat2 + "." + gat1 + "." + gat0);
        }

    } // [0x16] Get IP MASK PORT GETWAY
    public void state23(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x17] Not used
    public void state24(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x18] Not used
    public void state25(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x19] Not used
    public void state26(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x1A] Not used
    public void state27(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x1B] Not used
    public void state28(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x1C] Not used
    public void state29(byte source)
    {
        state = 0x20;
        StateActions[state].state(source);
    } // [0x1D] Not used
    public void state30(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            System.out.println("Init is " + ByteCollecter[0]);
        }
    } // [0x1E] Get init
    public void state31(byte source)
    {
        ByteCollecter[countByte++] = source;
        if (countByte == 4)
        {
            countByte = 0;
            state = 0x20;
            System.out.println("Error " + ByteCollecter[0]);
        }
    } // [0x1F] Error
    public void state32(byte source)
    {

        code <<= 8;
        code |= source;
        if ((code & 0x00FFFFFF) == controlBytes)
        {
            state = code >> 24;

            if(state>0x20 || state<0 ){
                System.out.println("Receive State " + state);
                state = 0x20;
            }
        }
    } // [0x20] WaitCommand

    public void DataTypeDouble() {
        if (countByte == 16 * 64) {
            countByte = 0;
            state = 0x20;
            //IQ iq;
            for (int i = 0; i < 64; i++) {
                //buf_re.offer(I);
//                buf_im.offer(Q);
               // iq.re = BitConverter.ToDouble(ByteCollecter, 16 * i + 0);
               // iq.im = BitConverter.ToDouble(ByteCollecter, 16 * i + 8);
               // QueueIQ.Enqueue(iq);
            }
        }
    }

    public void DataTypeFloat() {
        if (countByte == 8 * 64) {
            countByte = 0;
            state = 0x20;
           // IQ iq;
            for (int i = 0; i < 64; i++) {
                float I = Float.intBitsToFloat((ByteCollecter[8 * i+3]<<24)|((ByteCollecter[8 * i+2]&0xff)<<16)|((ByteCollecter[8 * i+1]&0xff)<<8)|(ByteCollecter[8 * i + 0]&0xff));
                float Q = Float.intBitsToFloat((ByteCollecter[8 * i+7]<<24)|((ByteCollecter[8 * i+6]&0xff)<<16)|((ByteCollecter[8 * i+7]&0xff)<<8)|(ByteCollecter[8 * i + 4]&0xff));
                buf_re.offer((double)I);
                buf_im.offer((double)Q);
            }
            }
    }

    public void DataTypeInt() {
        if (countByte == 8 * 64) {
            countByte = 0;
            state = 0x20;
            //IQ iq;
            for (int i = 0; i < 64;i++ ) {
                int I = Ints.fromBytes(ByteCollecter[8 * i+3],ByteCollecter[8 * i+2],ByteCollecter[8 * i+1],ByteCollecter[8 * i + 0]);
                int Q = Ints.fromBytes(ByteCollecter[8 * i+7],ByteCollecter[8 * i+6],ByteCollecter[8 * i+5],ByteCollecter[8 * i+4]);
                buf_re.offer((double)I);
                buf_im.offer((double)Q);
            }
        }
    }

    public void DataTypeInt24() {
        if (countByte == 6 * 64) {
            countByte = 0;
            state = 0x20;
            byte first=0;
            int I,Q;
            //IQ iq;
            for (int i = 0; i < 64;i++ ) {
                I=0;
                Q=0;
                int b3I = (ByteCollecter[6 * i + 2]&0xff);
                int b2I = (ByteCollecter[6 * i + 1]&0xff);
                int b1I = (ByteCollecter[6 * i + 0]&0xff);

                int b3Q = (ByteCollecter[6 * i + 5]&0xff);
                int b2Q = (ByteCollecter[6 * i + 4]&0xff);
                int b1Q = (ByteCollecter[6 * i + 3]&0xff);

                 I = (int)((b3I<<16)|(b2I<<8)| b1I);
                 Q = (int)((b3Q<<16)|(b2Q<<8)| b1Q);

                I=I<<8;
                I=I>>8;

                Q=Q<<8;
                Q=Q>>8;

                buf_re.offer((double)I);
                buf_im.offer((double)Q);
            }
        }
    }

    public void DataTypeShort() {

        if (countByte == 4 * 64) {
            List<Sample> result = new ArrayList<>();
            countByte = 0;
            state = 0x20;
           // IQ iq;
            for (int i = 0; i < 64; i++) {
//                byte b2I = ByteCollecter[4 * i + 1];
//                byte b1I = ByteCollecter[4 * i + 0];
//
//                byte b2Q = ByteCollecter[4 * i + 3];
//                byte b1Q = ByteCollecter[4 * i + 2];
//
//                short I = Shorts.fromBytes(b2I, b1I);
       //         short Q = Shorts.fromBytes(b2Q, b1Q);


                int b2I = (ByteCollecter[4 * i + 1]&0xff);
                int b1I = (ByteCollecter[4 * i + 0]&0xff);

                int b2Q = (ByteCollecter[4 * i + 3]&0xff);
                int b1Q = (ByteCollecter[4 * i + 2]&0xff);

                int I = (int)((b2I<<8)| b1I);
                I=I<<16;
                I=I>>16;

                int Q = (int)((b2Q<<8)| b1Q);
                Q=Q<<16;
                Q=Q>>16;


                result.add(new Sample((float) I , (float) Q ));
                //buf_re.offer((double)I);
                //buf_im.offer((double)Q);

                ///Sample temp = new Sample((double)I,(double)Q);
                //buf.add(temp);

               // System.out.println("re+im");
            }
            for (HorizonEventListener eventListener : eventListeners) {
                eventListener.iqReceived(result);
            }
        }
    }

    public void DataTypeByte() {
        if (countByte == 2 * 64) {
            countByte = 0;
            state = 0x20;
            //IQ iq;
            for (int i = 0; i < 64; i++) {

                int I = (ByteCollecter[2 * i + 0]& 0xff);
                int Q = (ByteCollecter[2 * i + 1]& 0xff);
                buf_re.offer((double)I);
                buf_im.offer((double)Q);
               // iq.re = ByteCollecter[2 * i + 0];
              //  iq.im = ByteCollecter[2 * i + 1];
               // QueueIQ.Enqueue(iq);
            }
        }
    }


    public interface HorizonEventListener {
        void ethernetParamsReceived(String ip, String mask, int port, String gateway);
        void commandReceived(HorizonCommand command, int value);
        void iqReceived(List<Sample> iq);
    }
}



    class ConvertAction
    {
        protected MachineState machineState;

        public void setMachineState(MachineState mState)
        {
            machineState = mState;
        }

        public  void convert() { }
    }

    class ByteConvertAction extends ConvertAction
    {
        public  void convert()
        {
            machineState.DataTypeByte();
        }
    }

    class IntConvertAction extends ConvertAction
    {
        public void convert()
        {
            machineState.DataTypeInt();
        }
    }

    class Int24ConvertAction extends ConvertAction
    {
        public void convert()
        {
            machineState.DataTypeInt24();
        }
    }

    class ShortConvertAction extends ConvertAction
    {
        public void convert()
        {
            machineState.DataTypeShort();
        }
    }

    class DoubleConvertAction extends ConvertAction
    {
        public void convert()
        {
            machineState.DataTypeDouble();
        }
    }
    class FloatConvertAction extends ConvertAction
    {
        public void convert()
        {
            machineState.DataTypeFloat();
        }
    }

    class StateAction{
        protected MachineState machineState;

        public void setMachineState(MachineState mState)
        {
            machineState = mState;
        }

        public  void state(byte source) { }
    }
class State0 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state0(source);
    }
}
class State1 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state1(source);
    }
}
class State2 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state2(source);
    }
}
class State3 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state3(source);
    }
}
class State4 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state4(source);
    }
}
class State5 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state5(source);
    }
}
class State6 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state6(source);
    }
}
class State7 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state7(source);
    }
}
class State8 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state8(source);
    }
}
class State9 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state9(source);
    }
}
class State10 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state10(source);
    }
}
class State11 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state11(source);
    }
}
class State12 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state12(source);
    }
}
class State13 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state13(source);
    }
}
class State14 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state14(source);
    }
}
class State15 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state15(source);
    }
}
class State16 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state16(source);
    }
}
class State17 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state17(source);
    }
}
class State18 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state18(source);
    }
}
class State19 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state19(source);
    }
}
class State20 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state20(source);
    }
}
class State21 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state21(source);
    }
}
class State22 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state22(source);
    }
}
class State23 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state23(source);
    }
}
class State24 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state24(source);
    }
}
class State25 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state25(source);
    }
}
class State26 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state26(source);
    }
}
class State27 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state27(source);
    }
}
class State28 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state28(source);
    }
}
class State29 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state29(source);
    }
}
class State30 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state30(source);
    }
}
class State31 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state31(source);
    }
}
class State32 extends StateAction
{
    public   void state(byte source)
    {
        machineState.state32(source);
    }
}


