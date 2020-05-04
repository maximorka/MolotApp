package com.first.lowLevel.command;

//import com.integer.horizon.lowlevel.rw.horizon.enums.DebugMode;

import com.first.lowLevel.enums.HorizonInterface;
import com.first.lowLevel.enums.HorizonWidth;
import com.first.util.ByteUtils;
import com.first.util.EthernetUtils;

//import com.integer.horizon.lowlevel.util.EthernetUtils;

public class HorizonCommands {
    private static final byte[] EMPTY_ARRAY = new byte[0];

    public static final byte[] COMMAND_MASK = new byte[] {
            ByteUtils.getLastByteFromInt(0x56),
            ByteUtils.getLastByteFromInt(0x34),
            ByteUtils.getLastByteFromInt(0x12)
    };

    private static final byte[] TEMP_COMMAND_DATA = new byte[4];

    private static final byte[] BYTE_4_ARGUMENTS_COMMAND = new byte[8];

    static {
        for(int i = 0; i < COMMAND_MASK.length; i++) {
            TEMP_COMMAND_DATA[i + 1] = COMMAND_MASK[i];
        }
    }

//    public static byte[] debug(DebugMode debugMode) {
//        return makeCommand(HorizonCommand.debug, debugMode.getCommandValue());
//    }

    public static byte[] transmitIQ(byte[] iqData) {
        return makeCommand(HorizonCommand.tx_iq, iqData);
    }

    /**
     * Ввімкнути демодулятор
     */
    public static byte[] disableRx() {
        return makeCommand(HorizonCommand.rx_set_mode_demodulation, 0);
    }

    /**
     * Вимкнути демодулятор
     */
    public static byte[] enableRx() {
        return makeCommand(HorizonCommand.rx_set_mode_demodulation, 1);
    }

    /**
     * Отримати стан демодулятора
     */
    public static byte[] getRxState() {
        return makeCommand(HorizonCommand.rx_get_mode_demodulation);
    }

    /**
     * Встановити ширину полоси демодулятора
     */
    public static byte[] setRxDemodulationWidth(HorizonWidth width) {
        return makeCommand(HorizonCommand.rx_set_width_demodulation, width.getCommandValue());
    }

    /**
     * Відправити запит на отримання ширини полоси демодулятора
     */
    public static byte[] getRxDemodulationWidth() {
        return makeCommand(HorizonCommand.rx_get_width_demodulation);
    }

    /**
     * Встановити частоту демодулятора
     */
    public static byte[] setRxDemodulationFrequency(int frequency) {
        return makeCommand(HorizonCommand.rx_set_freq_demodulation, frequency);
    }

    /**
     * Відправити запит на отримання частоти демодулятора
     */
    public static byte[] getRxDemodulationFrequency() {
        return makeCommand(HorizonCommand.rx_get_freq_demodulation);
    }

    /**
     * Скинути налаштування демодулятора
     */
    public static byte[] resetRxSettings() {
        return makeCommand(HorizonCommand.rx_set_default_settings);
    }

    /**
     * Вимкнути модулятор
     */
    public static byte[] disableTx() {
        return makeCommand(HorizonCommand.tx_set_mode_modulation, 0);
    }

    /**
     * Ввімкнути модулятор
     */
    public static byte[] enableTx() {
        return makeCommand(HorizonCommand.tx_set_mode_modulation, 1);
    }

    /**
     * Ввімкнути модулятор для генерації 1Кгц
     */
    public static byte[] txGenerate1Khz() {
        return makeCommand(HorizonCommand.tx_set_mode_modulation, 2);
    }

    /**
     * Повертає стан модулятора
     */
    public static byte[] getTxState() {
        return makeCommand(HorizonCommand.tx_get_mode_modulation);
    }

    /**
     * Задати ширину демодуляції
     */
    public static byte[] setTxModulationWidth(HorizonWidth width) {
       return makeCommand(HorizonCommand.tx_set_width_modulation, width.getCommandValue());
    }

    /**
     * Відправити запит на отримання ширини модуляції
     */
    public static byte[] getTxModulationWidth() {
        return makeCommand(HorizonCommand.tx_get_width_modulation);
    }

    /**
     * Встановити частоту модуляції
     */
    public static byte[] setTxModulationFrequency(int frequency) {
        return makeCommand(HorizonCommand.tx_set_freq_modulation, frequency);
    }

    /**
     * Відправити запит на отримання частоти модуляції
     */
    public static byte[] getTxModulationFrequency() {
        return makeCommand(HorizonCommand.tx_get_freq_modulation);
    }

    /**
     * Скинути налаштування модулятора
     */
    public static byte[] resetTx() {
        return makeCommand(HorizonCommand.tx_set_default_settings);
    }

    /**
     * Очистити буфер модулятора
     */
    public static byte[] clearTxBuffer() {
        return makeCommand(HorizonCommand.tx_clear_buffer);
    }

    /**
     * Відправити запит на отримання відсотка наповненості буфера
     */
    public static byte[] getTxBufferPercent() {
        return makeCommand(HorizonCommand.tx_get_buffer_percent);
    }

    /**
     * Відправити запит на отримання справки
     */
    public static byte[] help() {
        return makeCommand(HorizonCommand.help);
    }

    /**
     * Встановити режим роботи "USB"
     */
    public static byte[] setUSBInterface() {
        return setWorkingInterface(HorizonInterface.usbSerial);
    }

    /**
     * Встановити режим роботи "Ethernet"
     */
    public static byte[] setEthernetInterface() {
        return setWorkingInterface(HorizonInterface.ethernet);
    }

    /**
     * Встановити режим роботи
     */
    public static byte[] setWorkingInterface(HorizonInterface horizonInterface) {
        return makeCommand(HorizonCommand.set_mode_interface, horizonInterface.getCommandValue());
    }

    public static byte[] setEthernetParams(String ip, String mask, int port, String gateway) {
        byte[] ipBytes = EthernetUtils.ipToByteArray(ip);
        byte[] maskBytes = EthernetUtils.ipToByteArray(mask);
        byte[] portBytes = ByteUtils.convertIntToByteArray(port);
        byte[] gatewayBytes = EthernetUtils.ipToByteArray(gateway);

        invertBytes(ipBytes);
        invertBytes(maskBytes);
        invertBytes(portBytes);
        invertBytes(gatewayBytes);

        byte[] finalCommand = ByteUtils.concatArrays(ipBytes, maskBytes, portBytes, gatewayBytes);

        return makeCommand(HorizonCommand.set_ethernet_params, finalCommand);
    }

    public static byte[] getEthernetParams() {
        return makeCommand(HorizonCommand.get_ethernet_params);
    }

    /**
     * Скинути налаштування інтерфейсу
     */
    public static byte[] resetInterfaceSettings() {
        return makeCommand(HorizonCommand.set_default_interface_settings);
    }

    /**
     * Відправити команду INIT
     */
    public static byte[] init() {
        return makeCommand(HorizonCommand.init);
    }

    /**
     * Встановити режим роботи приймача
     */
    public static byte[] setFormRx(RxTxForm form) {
        return makeCommand(HorizonCommand.setFormRx, form.getValue());
    }

    /**
     * Отримати режим роботи приймача
     */
    public static byte[] getFormRx() {
        return makeCommand(HorizonCommand.getFormRx);
    }

    /**
     * Встановити режим роботи передатчика
     */
    public static byte[] setFormTx(RxTxForm form) {
        return makeCommand(HorizonCommand.setFormTx, form.getValue());
    }

    /**
     * Отримати режим роботи передатчика
     */
    public static byte[] getFormTx() {
        return makeCommand(HorizonCommand.getFormTx);
    }

    /**
     * Встановити фільтр приймача (0 - найширший, 9 - найвужчий)
     */
    public static byte[] setFilterRx(int value) {
        return makeCommand(HorizonCommand.setFilterRx, value);
    }

    /**
     * Отримати значення фільтра приймача (0 - найширший, 9 - найвужчий)
     */
    public static byte[] getFilterRx() {
        return makeCommand(HorizonCommand.getFilterRx);
    }

    /**
     * Встановити значення гучності (AF) приймача (0..100), відсотки
     */
    public static byte[] setVolumeRx(int value) {
        return makeCommand(HorizonCommand.setVolumeRx, value);
    }

    /**
     * Отримати значення гучності (AF) приймача (0..100), відсотки
     */
    public static byte[] getVolumeRx() {
        return makeCommand(HorizonCommand.getVolumeRx);
    }

    /**
     * Встановити значення попереднього підсилювача приймача (1 - ввімкнено, 0 - вимкнено)
     */
    public static byte[] setPreamRx(int enabled) {
        return makeCommand(HorizonCommand.setPreampRx, enabled);
    }

    /**
     * Прочитати значення попереднього підсилювача приймача (1 - ввімкнено, 0 - вимкнено)
     */
    public static byte[] getPreamRx() {
        return makeCommand(HorizonCommand.getPreamRx);
    }

    /**
     * Встановити значення аттенюатора приймача, Дб (0..31)
     */
    public static byte[] setAttenRx(int db) {
        return makeCommand(HorizonCommand.setAttenRx, db);
    }

    /**
     * Прочитати значення аттенюатора приймача, Дб (0..31)
     */
    public static byte[] getAttenRx() {
        return makeCommand(HorizonCommand.getAttenRx);
    }

    /**
     * Встановити значення потужності передавача (0..100), відсотки
     */
    public static byte[] setDriverLevelTx(int percent) {
        return makeCommand(HorizonCommand.setDriverLevelTx, percent);
    }

    /**
     * Прочитати значення потужності передавача (0..100), відсотки
     */
    public static byte[] getDriverLevelTx() {
        return makeCommand(HorizonCommand.getDriverLevelTx);
    }

    /**
     * Встановити значення PITCH, Hz (400..1500)
     */
    public static byte[] setPitch(int hz) {
        return makeCommand(HorizonCommand.setPitch, hz);
    }

    /**
     * Прочитати значення PITCH, Hz (400..1500)
     */
    public static byte[] getPitch() {
        return makeCommand(HorizonCommand.getPitch);
    }

//    setPitch(0x2E, 4, "Set Pitch"),
//    getPitch(0x2F, 4, "Get Pitch"),


    /**
     * Повертає стан модулятора
     */
    public static byte[] sendHeardbyte() {
        return makeCommand(HorizonCommand.unknown);
    }
    public static byte[] makeCommand(HorizonCommand command) {
        return makeCommand(command, EMPTY_ARRAY);
    }

    public static byte[] makeCommand(HorizonCommand command, int param) {
        return makeCommand(command, ByteUtils.convertIntToByteArray(param));
    }

    public static byte[] makeCommand(HorizonCommand command, byte[] data) {
        TEMP_COMMAND_DATA[0] = ByteUtils.getLastByteFromInt(command.getCode());

        if (!command.isIQ() && data.length == 4) { //Let's swap
            byte b0 = data[0];
            byte b1 = data[1];
            byte b2 = data[2];
            byte b3 = data[3];

            data[0] = b3;
            data[1] = b2;
            data[2] = b1;
            data[3] = b0;
        }

        if (data.length <= 4) {

            for(int i = 0; i < TEMP_COMMAND_DATA.length; i++) {
                BYTE_4_ARGUMENTS_COMMAND[i] = TEMP_COMMAND_DATA[i];
            }

            for(int i = 0; i < data.length; i++) {
                BYTE_4_ARGUMENTS_COMMAND[i + 4] = data[i];
            }

            return BYTE_4_ARGUMENTS_COMMAND;
        }

        byte[] result = new byte[4 + data.length];

        for(int i = 0; i < 4; i++) {
            result[i] = TEMP_COMMAND_DATA[i];
        }

        for(int i = 0; i < data.length; i++) {
            result[i + 4] = data[i];
        }

        return result;
    }

    public static void invertBytes(byte[] data) {
        byte b0 = data[0];
        byte b1 = data[1];
        byte b2 = data[2];
        byte b3 = data[3];

        data[0] = b3;
        data[1] = b2;
        data[2] = b1;
        data[3] = b0;
    }

}
