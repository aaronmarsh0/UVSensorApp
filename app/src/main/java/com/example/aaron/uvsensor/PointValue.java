package com.example.aaron.uvsensor;

public class PointValue {
    private long xValue;
    private int yValue;

    public PointValue() {
    }

    public PointValue(long xValue, int yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public long getxValue() {
        return xValue;
    }

    public int getyValue() {
        return yValue;
    }
}
