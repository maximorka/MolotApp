package com.first.lowLevel;

public interface HorizonDeviceListener extends MachineState.HorizonEventListener {
    void connectStateChanged(boolean connected);
    void seansStateChanged(boolean seans);
}
