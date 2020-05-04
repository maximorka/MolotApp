package com.first.lowLevel.enums;

public enum HorizonInterface {
    usbSerial(0, "USB"),
    ethernet(1, "Ethernet");

    private int commandValue;
    private String description;

    HorizonInterface(int commandValue, String description) {
        this.commandValue = commandValue;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public int getCommandValue() {
        return commandValue;
    }

    public String getDescription() {
        return description;
    }
}
