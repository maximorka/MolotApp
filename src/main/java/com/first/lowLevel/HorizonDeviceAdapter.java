package com.first.lowLevel;

import com.first.lowLevel.command.HorizonCommand;

import java.util.List;

public class HorizonDeviceAdapter implements HorizonDeviceListener {
    @Override
    public void connectStateChanged(boolean connected) {
    }

    @Override
    public void seansStateChanged(boolean seans) {

    }

    @Override
    public void ethernetParamsReceived(String ip, String mask, int port, String gateway) {

    }

    @Override
    public void commandReceived(HorizonCommand command, int value) {
    }

    @Override
    public void iqReceived(List<Sample> iq) {

    }
}
