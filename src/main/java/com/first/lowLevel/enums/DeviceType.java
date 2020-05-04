package com.first.lowLevel.enums;

public enum DeviceType {
    horizon("Горизонт", 64*3, 2),
    virial("Віріал", 64*4, 3);


    DeviceType(String description, int txIQDataSizeInBytes, int rxIQByteCount) {
        this.rxIQByteCount = rxIQByteCount;
        this.txIQDataSizeInBytes = txIQDataSizeInBytes;
        this.description = description;
    }

    private int rxIQByteCount;
    private int txIQDataSizeInBytes;
    private String description;

    public int getTxIQDataSizeInBytes() {
        return txIQDataSizeInBytes;
    }

    public int getRxIQByteCount() {
        return rxIQByteCount;
    }

    @Override
    public String toString() {
        return description;
    }
}
