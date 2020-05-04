package com.first.demod.demodulator;

//import com.first.demod.generator.Sample;

import com.first.lowLevel.Sample;

public interface IDemodulator {
	
	boolean demodulate(float sample);
	boolean demodulate(Sample sample);
	DemodulatedValue getDemodulatedBits();
	void setSpeed(int baudeRate);
	int getSpeed();
	void setBandWidth(int Width);
	int getBandWidth();
}
