package com.first.lowLevel.command;

/**
 * Режим роботи приймача/передавача
 */
public enum RxTxForm {
    LSB(0, "Передача даних"),
    USB(1, "USB"),
    CWL(2, "CWL"),
    CWU(3, "CWU"),
    DIGL(4, "DIGL"),
    DIGU(5, "DIGU"),
    AM(6, "AM"),
    FM(7, "FM");

    RxTxForm(int value, String description) {
        this.value = value;
        this.description = description;
    }

    private String description;
    private int value;

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description;
    }
}
