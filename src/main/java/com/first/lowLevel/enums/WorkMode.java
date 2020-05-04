package com.first.lowLevel.enums;

public enum WorkMode {
    DBPSK100(100, "DBPSK-100"),
    DBPSK250(250, "DBPSK-250"),
    DBPSK500(500, "DBPSK-500"),
    DBPSK1000(1000, "DBPSK-1000");

    WorkMode(int speed, String description) {
        this.speed = speed;
        this.description = description;
    }

    private int speed;
    private String description;

    @Override
    public String toString() {
        return description;
    }

    public int getSpeed() {
        return speed;
    }
}
