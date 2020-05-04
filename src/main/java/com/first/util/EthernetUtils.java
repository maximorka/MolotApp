package com.first.util;

import java.util.StringJoiner;

public class EthernetUtils {
    public static byte[] ipToByteArray(String ip) {
        String[] parts = ip.split("\\.");
        byte[] result = new byte[4];
        for(int i = 0; i < parts.length; i++) {
            byte value = ByteUtils.getLastByteFromInt(Integer.parseInt(parts[i]));
            result[i] = value;
        }
        return result;
    }

    public static String convertIntToStringIP(int ipValue) {
        byte[] data = ByteUtils.convertIntToByteArray(ipValue);

        StringJoiner result = new StringJoiner(".");
        for(byte item: data) {
            int intValue = (int) item;
            if (intValue < 0) {
                intValue = 256 + intValue;
            }
            result.add(Integer.toString(intValue));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        String ip = "1.1.1.1";
        byte[] bytes = EthernetUtils.ipToByteArray(ip);
        System.out.println(ByteUtils.bytesToHex(bytes));
    }
}
