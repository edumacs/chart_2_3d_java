package com.edumacs.chart.chart3d;

/**
 *
 * @author mohammadfaizun
 */
public class BarStyle {

    private double xLength = 0.5;
    private double yLength = 0.5;
    private double zOrigin = 0;
    private boolean isBarSingleColor = true;

    public BarStyle() {
    }

    public boolean getIsBarSingleColor() {
        return isBarSingleColor;
    }

    public void setIsBarSingleColor(boolean value) {
        isBarSingleColor = value;
    }

    public double getZOrigin() {
        return zOrigin;
    }

    public void setZOrigin(double value) {
        zOrigin = value;
    }

    public double getYLength() {
        return yLength;
    }

    public void setYLength(double value) {
        yLength = value;
    }

    public double getXLength() {
        return xLength;
    }

    public void setXLength(double value) {
        xLength = value;
    }

}
