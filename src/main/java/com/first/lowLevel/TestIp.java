package com.first.lowLevel;

import java.net.InetAddress;

/**
 * Created by pc3 on 14.04.2020.
 */
public class TestIp {
    public static void main(String[] args) {
        try {
            String ipAddress = "192.168.0.2";
            InetAddress inet = InetAddress.getByName(ipAddress);
            System.out.println("Sending Ping Request to " + ipAddress);
            if (inet.isReachable(5000)) {
                System.out.println(ipAddress + " is reachable.");
            } else {
                System.out.println(ipAddress + " NOT reachable.");
            }
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }
    }
}
