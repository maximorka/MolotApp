package com.first.demod.demodulator;

public interface HorizonDemodulatorListener {
    void dataReceived(int channelIndex, boolean[] data);
}
