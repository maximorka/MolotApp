package com.first.demod.demodulator;

public enum DemodulatedValue {
	TRUE,
	FALSE,
	TRUE_TRUE,
	TRUE_FALSE,
	FALSE_TRUE,
	FALSE_FALSE;
	
	public static boolean[][] POSSIBLE_VALUES =  {
		{true},
		{false},
		{true, true},
		{true, false},
		{false, true},
		{false, false}
	};
	
	public boolean[] getBits() {
		return POSSIBLE_VALUES[ordinal()];
	}
	
	public String print() {
		String result = "";
		for(boolean bit: getBits()) {
			result += bit ? "1" : "0";
		}
		return result;
	}
}
