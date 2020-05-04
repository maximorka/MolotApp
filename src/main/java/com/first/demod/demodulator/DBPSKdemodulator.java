package com.first.demod.demodulator;

import com.first.lowLevel.Sample;

import java.util.Random;

public class DBPSKdemodulator implements IDemodulator {
    private int baudeRate;
    private int bandWidth;
    private int samplesPerSymbol = 0;
    private Sample[] input = new Sample[1024];
    private int point = 0;
    private int mask = 1024 - 1;
    private int count = 0;



    private Sample result = new Sample(0, 0);

    private float error;
    private int endIndex = 0;
    private int index = 0;
    private  float  countSync = 0;
    private int  integratorOneI = 0;
    private int  integratorOneQ = 0;
    private int  integratorTwoI = 0;
    private int  integratorTwoQ = 0;
    private float  totalIntegrator = 0;
    private  boolean  res = false ;

    private Sample[]  buff = new Sample[12+1];

    public DBPSKdemodulator(int baudeRate, int bandWidth) {
        for (int i = 0; i <=1023 ; ++i) {
            input[i] = new Sample(0,0);
        }
        for (int i = 0; i <=12 ; ++i) {
            buff[i] = new Sample(0,0);
        }
        setSpeed(baudeRate);
        setBandWidth(bandWidth);
        setSamplesPerSymbol();
    }

    public static void main(String[] args) {
int size= 10;
        int index =0;
        boolean lastbit =false;
        Random random = new Random();
        Sample[] constelation = {new Sample (0.f,1.f), new Sample(0.f,-1.f)};
        DBPSKdemodulator w = new DBPSKdemodulator(250,3000);
        Sample s = new Sample(0,0);
        boolean [] data = new boolean[size];
        int t =0;
        for (int i = 0; i <size; i++) {
            data[i] = random.nextBoolean();
            System.out.print(", "+data[i]);

        }
        System.out.println("");
        for (int i = 0; i <size ; i++) {


            boolean defData = lastbit ^ data[i];
            lastbit = defData;
            data[i] = defData;
            System.out.print(", "+data[i]);

        }
        System.out.println("");

        //Модулюємо дані
        for (int k= 0; k< data.length; k++) {


        for (int repeat = 0; repeat < 12; repeat++) {
             s.set(constelation[data[k] ? 1 : 0].getI(),constelation[data[k] ? 1 : 0].getQ()); ;


            System.out.print(w.dem(s)?1:0);
                                    index++;
                                    if (index == 100) {
                                        index = 0;
                                        System.out.println();
                                    }



        }
        }



    }
//    private void demodulatorQPSK(qpskDemodulator *parametrs){
//         iqPerSymbol = (parametrs->BandWidth/parametrs->BaudeRate);
//        uint32_t mask = 1024 - 1;
//        static complex temp[1024];
//        static int count=0;
//        static int point = 0;
//        static float countSync = 0;
//        static float error = 0;
//        static complex result = {0,0};
//
//
//        if(count == 64){
//            count = 0;
//        }
//
//        temp[point].i = parametrs->IQ.i;
//        temp[point].q = parametrs->IQ.q;
//
//        result = complexMultiply((complex){parametrs->IQ.i,parametrs->IQ.q},(complex){temp[(point - iqPerSymbol)&mask].i,-temp[(point - iqPerSymbol)&mask].q});
//
//        //complex tpmres = complexMultiply(result,(complex){1.f,1.f});
//
//        uint8_t bit = syncQPSK(result.i,result.q);
//        point++;
//        point &= mask;
//        count++;
//    }
    public void setSamplesPerSymbol(){
        samplesPerSymbol = bandWidth / baudeRate;
        //clearSamplesBuffer();
        //clearBitsBuffer();
    }
private boolean dem(Sample sample){

    input[point].set(sample.getI(),sample.getQ());
    result.set(sample.getI(), sample.getQ());
    result.multiply(input[(point-samplesPerSymbol)&mask]);


    boolean bit = syncQPSK(result);
    point++;
    point &= mask;
//        count++;

    return bit;
}


    @Override
    public boolean demodulate(float sample) {
        return false;
    }

    @Override
    public boolean demodulate(Sample sample) {
        input[point].set(sample.getI(),sample.getQ());
        result.set(sample.getI(), sample.getQ());
        result.multiply(input[(point-samplesPerSymbol)&mask]);


        boolean bit = syncQPSK(result);
        point++;
        point &= mask;
//        count++;

        return bit;
    }

    private boolean syncQPSK(Sample sample){


       buff[index++].set(sample.getI(),sample.getQ());

        countSync += 1.f  ;
        countSync += error;
        totalIntegrator += sample.getI();

        if(countSync >= samplesPerSymbol){

            countSync-=samplesPerSymbol;
            endIndex = ((index * 75)/100);

            integratorOneI = 0;
            integratorOneQ = 0;
            integratorTwoQ = 0;
            integratorTwoI = 0;

            for(int i = 0; i<endIndex; i++){
                integratorOneI += comparator( buff[i].getI());
                integratorOneQ += comparator( buff[i].getQ());
            }

            for(int i = index-endIndex; i < index; i++){
                integratorTwoI += comparator( buff[i].getI());
                integratorTwoQ += comparator( buff[i].getQ());
            }
            if(integratorOneI<0){
                integratorOneI*=-1;
            }
            if(integratorTwoI<0){
                integratorTwoI*=-1;
            }
            error =0;//  integratorOneI - integratorTwoI;


           // error = looopFilter(error);

            index=0;
            res = true;
            if(totalIntegrator>0){
                res = false;
            }
            totalIntegrator = 0;
            //byteforbit(res);
            return res;
        }
        return res;
    }
    int comparator(float IQ){

        if(IQ >= 0.f){
            return 1;
        } else
            return -1;
    }
    private Sample comparatorIQ(Sample sample){
        Sample res = new Sample (-1,-1);
        if(sample.getI() >= 0.f){
            res.setI(1.f);
        }
        if(sample.getQ() >= 0.f){
            res.setQ(1.f);
        }
        return res;
    }
    @Override
    public DemodulatedValue getDemodulatedBits() {
        return null;
    }

    @Override
    public void setSpeed(int baudeRate) {
        this.baudeRate = baudeRate;
        setSamplesPerSymbol();
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public void setBandWidth(int Width) {
        this.bandWidth = Width;

    }

    @Override
    public int getBandWidth() {
        return 0;
    }
}
