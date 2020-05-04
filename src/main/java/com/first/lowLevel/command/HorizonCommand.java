package com.first.lowLevel.command;

public enum HorizonCommand {
    rx_iq(0x0, 256, "IQ Receive"),
    tx_iq(0x0, 192, "IQ Transmit"),

    //RX
    rx_set_mode_demodulation(0x01, 4, "Set mode demodulation"),
    rx_get_mode_demodulation(0x02, 4, "Get mode demodulation"),
    rx_set_width_demodulation(0x03, 4, "Set width demodulation (0 - Max, 1 - Max/2, 2 - Max/4, 3 - Max/8, 4 - Max/16)"),
    rx_get_width_demodulation(0x04, 4, "Get width demodulation (0 - Max, 1 - Max/2, 2 - Max/4, 3 - Max/8, 4 - Max/16)"),
    rx_set_freq_demodulation(0x05, 4, "Set frequency_demodulation"),
    rx_get_freq_demodulation(0x06, 4, "Get frequency demodulation"),
    rx_set_default_settings(0x07, 4, "Set default demodulator settings"),

    //TX
    tx_set_mode_modulation(0x9, 4, "Set mode modulation (0 - disable, 1 - enable)"),
    tx_get_mode_modulation(0xA, 4, "Get mode modulation (0 - disable, 1 - enable)"),
    tx_set_width_modulation(0xB, 4, "Set width modulation (0 - Max, 1 - Max/2, 2 - Max/4, 3 - Max/8, 4 - Max/16)"),
    tx_get_width_modulation(0xC, 4, "Get width modulation (0 - Max, 1 - Max/2, 2 - Max/4, 3 - Max/8, 4 - Max/16)"),
    tx_set_freq_modulation(0xD, 4, "Set frequency modulation"),
    tx_get_freq_modulation(0xE, 4, "Get frequency modulation"),
    tx_set_default_settings(0xF, 4, "Set default modulator settings"),
    tx_clear_buffer(0x10, 4, "Clear modulator buffer"),
    tx_get_buffer_percent(0x11, 4, "Get buffer percent"),

    //Device
    help(0x12, 4, "Help (print list of commands)"),
    set_mode_interface(0x13, 4, "Set interface (1 - Ethernet, 0 - USB)"),
    get_mode_interface(0x14, 4, "Get interface (1 - Ethernet, 0 - USB)"),

    //Ethernet
    set_ethernet_params(0x15, 16, "Set ALL Ethernet params"),
    get_ethernet_params(0x16, 16, "Get ALL Ethernet params"),

    //Common
    set_default_interface_settings(0x1D, 4, "Set default interface settings"),
    init(0x1E, 4, "Get init code"),
    error(0x1F, 4, "Error happened"),
    debug(0x1F, 4, "Debug"),

    //VIRIAL - приемник
    //Установка и чтение режима приемника
    setFormRx(0x20, 4, "Set Form RX"),
    getFormRx(0x21, 4, "Get Form RX"),

    //Установка и чтение фильтра приемника
    setFilterRx(0x22, 4, "Set Filter RX"),
    getFilterRx(0x23, 4, "Get Filter RX"),

    //Установка и чтение AF приемника
    setVolumeRx(0x24, 4, "Set Volume RX"),
    getVolumeRx(0x25, 4, "Get Volume RX"),

    //Установка и чтение предусилителя приемника
    setPreampRx(0x26, 4, "Set Preamp RX"),
    getPreamRx(0x27, 4, "Get Preamp RX"),

    //Установка и чтение аттенюатора приемника
    setAttenRx(0x28, 4, "Set Atten RX"),
    getAttenRx(0x29, 4, "Get Atten RX"),

    //VIRIAL - передатчик
    //Установка и чтение режима передатчика
    setFormTx(0x2A, 4, "Set Form TX"),
    getFormTx(0x2B, 4, "Get Form TX"),

    //Установка и чтение мощности передатчика
    setDriverLevelTx(0x2C, 4, "Set Driver Level TX"),
    getDriverLevelTx(0x2D, 4, "Get Driver Level TX"),

    setPitch(0x2E, 4, "Set Pitch"),
    getPitch(0x2F, 4, "Get Pitch"),

    //Unknow command
    unknown(0xFF, 4, "Unknown command");

    private int code;
    private int dataSize;
    private String description;

    HorizonCommand(int code, int dataSize, String description) {
        this.code = code;
        this.dataSize = dataSize;
        this.description = description;
    }

    public boolean isIQ() {
        return this == tx_iq || this == rx_iq;
    }

    public static HorizonCommand getCommandByByte(byte value) {
        for(HorizonCommand cmd: values()) {
            if (cmd.getCode() == value) {
                return cmd;
            }
        }

        return HorizonCommand.unknown;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getDataSizeInBytes() {
        return dataSize;
    }
}
