package com.first.util;

public class Averager {
    private int blockLength;
    private int blockCount;

    private int activeBlockCount;
    private int blockIndex;
    private double[][] data;

    private double[] averaged;

    public void init(int blockLength, int blockCount) {
        this.blockLength = blockLength;
        this.blockCount = blockCount;

        data = new double[blockCount][];
        for(int i = 0; i < blockCount; i++) {
            data[i] = new double[blockLength];
        }

        averaged = new double[blockLength];

        blockIndex = 0;
        activeBlockCount = 0;
    }

    public void addBlock(double[] block) {
        double[] toInsert = data[blockIndex++];
        for(int i = 0; i < blockLength; i++) {
            toInsert[i] = block[i];
        }

        if (blockIndex >= data.length) {
            blockIndex = 0;
        }

        if (activeBlockCount < blockCount) {
            activeBlockCount++;
        }
    }

    public double[] calcuateAverage() {
        for(int itemIndex = 0; itemIndex < blockLength; itemIndex++) {
            double avg = 0;
            for(int blockIndex = 0; blockIndex < activeBlockCount; blockIndex++) {
                avg += data[blockIndex][itemIndex];
            }

            avg /= (double) activeBlockCount;

            averaged[itemIndex] = avg;
        }

        return averaged;
    }

    public double calculateEnergy() {
        return 0;
    }

    public static void main(String[] args) {
        double[] a1 = new double[] {1, 1, 1};
        double[] a2 = new double[] {6, 5, 1};

        Averager avg = new Averager();
        avg.init(3, 2);

        avg.addBlock(a1);
        avg.addBlock(a2);

        printDouble(avg.calcuateAverage());
    }

    private static void printDouble(double[] array) {
        for(int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
}
