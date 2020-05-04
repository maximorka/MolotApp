package com.first.demod;

import com.first.demod.demodulator.HorizonDemodulator;
import com.first.demod.demodulator.HorizonDemodulatorListener;
import com.first.demod.modulator.HorizonModulator;

public class HorizonModem {
    private HorizonModulator modulator;
    private HorizonDemodulator demodulator;

   private HorizonChannelConfig config;

    public HorizonModem() {
        modulator = new HorizonModulator();
        demodulator = new HorizonDemodulator();
    }

    public HorizonModem config(HorizonChannelConfig config) {
        this.config = config;

        setup();

        return this;
    }

    private void setup() {
        modulator.config(config);
        demodulator.config(config);
    }

    public void addDemodulatorListener(HorizonDemodulatorListener listener) {
        demodulator.addDataListener(listener);
    }

    public void addSample(float I, float Q) {
        demodulator.addSample(I, Q);
    }

    public byte[] generate() {
        return modulator.generate();
    }

    public HorizonModulator getModulator() {
        return modulator;
    }

//    public HorizonDemodulator getDemodulator() {
//        return demodulator;
//    }

//    public void close() {
//        if (demodulator != null) {
//            demodulator.close();
//        }
//    }
}
