package com.first.demod;

import com.first.lowLevel.enums.HorizonWidth;

public class HorizonChannelConfig {
    //private DeviceType deviceType = DeviceType.horizon;
    private int channelSpeed;
    private int channelCount;

    public HorizonChannelConfig copy() {
        HorizonChannelConfig result = new HorizonChannelConfig(channelSpeed, channelCount);
        //result.setDeviceType(deviceType);
        return result;
    }

    public HorizonChannelConfig(int channelSpeed, int channelCount) {
        this.channelSpeed = channelSpeed;
        this.channelCount = channelCount;
    }

//    public void setDeviceType(DeviceType deviceType) {
//        this.deviceType = deviceType;
//    }
//
//    public DeviceType getDeviceType() {
//        return deviceType;
//    }

    /**
     * Ширина каналу,Гц
     */
    public int getChannelWidth() {
        return channelSpeed * 2 ;
    }

    /**
     * Мінімальна відстань між каналами
     */
    public int getWidthBetweenChannelEdges() {
        return channelSpeed * 0;
    }

    /**
     * Загальна ширина полоси, Гц, з врахуванням кількості каналів і їх швидкості
     */
    public int getTotalWidth() {
        int spaceCount = channelCount - 1;
        int spaceWidth = getWidthBetweenChannelEdges();
        int totalSpaceWidth = spaceCount * spaceWidth;

        int totalChannelWidth = channelCount * getChannelWidth();

        return totalSpaceWidth + totalChannelWidth;
    }

    public int getChannelOffset(int channelIndex) {
        int leftEdge = -getTotalWidth()/2;

        int spaceCount = channelIndex;

        int channelEdge = leftEdge + spaceCount * getWidthBetweenChannelEdges() + channelIndex * getChannelWidth();
        return channelEdge + getChannelWidth()/2;
    }


    /**
     * Повертає мінімальну допустиму ширину
     */
    public HorizonWidth calculateTotalHorizonWidth() {
        int totalWidth = getTotalWidth();

        for(HorizonWidth width: HorizonWidth.minToHighValues()) {
            if (width.getValue() >= totalWidth) {
                return width;
            }
        }

        throw new IllegalStateException("Недопустимі параметри кількості каналів/швидкості одного каналу");
    }

    /**
     * Загальна швидкість, біт/секунду
     */
    public int getTotalSpeedInBits() {
        return channelSpeed * channelCount;
    }

    public static void main(String[] args) {
        HorizonChannelConfig config = new HorizonChannelConfig(250, 4);

        System.out.println(config.getTotalWidth() + " (" + config.calculateTotalHorizonWidth().getValue() + ")");
        System.out.println(config.getTotalSpeedInBits());


        for(int i = 0; i < config.channelCount; i++) {
            System.out.println("Channel index: " + i + ", OFFSET: " + config.getChannelOffset(i));
        }

    }

    public int getChannelCount() {
        return channelCount;
    }

    public int getChannelSpeed() {
        return channelSpeed;
    }
}
