package com.first.lowLevel.enums;

public enum DebugMode {
    off(0),
    on(1),
    bridge_mode(2),
    sniffer_modulate(3),
    sniffer_demodulate(4);

    DebugMode(int commandValue) {
        this.commandValue = commandValue;
    }

    private int commandValue;

    public int getCommandValue() {
        return commandValue;
    }
}
