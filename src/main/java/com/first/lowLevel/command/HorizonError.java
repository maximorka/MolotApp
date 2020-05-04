package com.first.lowLevel.command;

public enum HorizonError {
    modeDemodulation(0x00, "Mode demodulation"),
    widthDemodulation(0x01, "Width demodulation"),
    frequencyDemodulation(0x2, "Frequency demodulation"),
    unknownCommandDemodulation(0x3, "Unknown command demodulation"),
    lostCommandDemodulation(0x4, "Lost command demodulation"),
    reservedDemodulation(0x5, "Reserved demodulation"),
    reservedDemodulation1(0x6, "Reserved demodulation"),
    reservedDemodulation2(0x7, "Reserved demodulation"),
    modeModulation(0x8, "Mode modulation"),
    widthModulation(0x9, "Width modulation"),
    frequencyModulation(0xA, "Frequency modulation"),
    unknownCommand(0xB, "Unknown command"),
    lostCommandModulation(0xC, "Lost command modulation"),
    reservedModulation(0xD, "Reserved modulation"),
    reservedModulation1(0xE, "Reserved modulation"),
    reservedModulation2(0xF, "Reserved modulation"),
    unknownCommandInterface(0x10, "Unknown command interface"),
    interfaceMode(0x11, "Interface mode"),
    lostCommandInterface(0x12, "Lost command interface"),
    notUsedCommandModulation(0x13, "Not used command modulation"),
    notUsedCommandModulation2(0x14, "Not used command modulation"),
    overflowBufferRx(0x15, "Overflow buffer RX"),
    overflowBufferRxDemodulation(0x16, "Overflow buffer RX demodulation"),
    overflowBufferRxModulation(0x17, "Overflow buffer RX modulation"),
    overflowBufferTxDemodulation(0x18, "Overflow buffer TX demodulation"),
    overflowBufferTxModulation(0x19, "Overflow buffer TX modulation"),
    overflowBufferTx(0x1A, "Overflow buffer TX"),
    crashSocket(0x1C, "Crash socket"),
    debugMode(0x1D, "Debug mode");

    HorizonError(int errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    private int errorCode;
    private String description;

    public String getDescription() {
        return Integer.toHexString(errorCode) + " - " + description;
    }

    public static HorizonError getError(int errorCode) {
        for(HorizonError error: values()) {
            if (error.errorCode == errorCode) {
                return error;
            }
        }
        return null;
    }
}
