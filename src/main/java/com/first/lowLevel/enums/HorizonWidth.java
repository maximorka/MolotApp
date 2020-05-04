package com.first.lowLevel.enums;

public enum HorizonWidth {
    width48KHz(48_000, 0, "48 КГц"),
    width24KHz(24_000, 1, "24 КГц"),
    width12KHz(12_000, 2, "12 КГц"),
    width6KHz(6_000, 3, "6 КГц"),
    width3KHz(3_000, 4, "3 КГц");

    private int value;
    private int commandValue;
    private String description;

    HorizonWidth(int value, int commandValue, String description) {
        this.value = value;
        this.commandValue = commandValue;
        this.description = description;
    }

    public int getCommandValue() {
        return commandValue;
    }

    public String getDescription() {
        return description;
    }

    public static HorizonWidth getByCommandValue(int commandValue) {
        for(HorizonWidth item: values()) {
            if (item.getCommandValue() == commandValue) {
                return item;
            }
        }

        return HorizonWidth.width48KHz;
    }

    public int getValue() {
        return value;
    }

    public static HorizonWidth[] minToHighValues() {
        return new HorizonWidth[] {
                width3KHz, width6KHz, width12KHz, width24KHz, width48KHz
        };
    }

    @Override
    public String toString() {
        return description;
    }
}
