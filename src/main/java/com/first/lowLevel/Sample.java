package com.first.lowLevel;

public class Sample {
    private float i=0;
    private float q=0;

    public Sample(float i, float q){
        set(i,q);
    }

    public float getI() {

        return i;
    }

    public float getQ() {
        return q;
    }

    public void set(float i,float q) {
        this.i = i;
        this.q = q;
    }
    public void setI(float i) {
        this.i = i;

    }
    public void setQ(float q) {
        this.q = q;

    }

    public void multiply(Sample sample){
        this.set(
            this.getI()*sample.getI() - this.getQ()*sample.getQ(),
            this.getQ()*sample.getI() + this.getI()*sample.getQ()
        );
    }

    public Sample copy() {
        return new Sample(i, q);
    }

    @Override
    public String toString() {
        return "i = "+i+" q = "+q;
    }
}
