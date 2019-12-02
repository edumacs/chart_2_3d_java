package com.edumacs.chart.base;

import javafx.scene.paint.Color;

/**
 * @author moham
 */
public class LineStyle {

    //dash line
    private Color lineColor = Color.BLACK;
    private double LineThickness = 0.5;
    private boolean isVisible = true;
    private double dashed = 1.0;
    private PlotLinesMethodEnum pltLineMethod = PlotLinesMethodEnum.Lines;

    public LineStyle() {

    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean vis) {
        isVisible = vis;
    }

    public double getDashed() {
        return dashed;
    }

    public void setDashed(double dsh) {
        dashed = dsh;
    }

    public double getThickness() {
        return LineThickness;
    }

    public void setThickness(double th) {
        LineThickness = th;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color clr) {
        lineColor = clr;
    }

    public PlotLinesMethodEnum getPlotMethod() {
        return pltLineMethod;
    }

    public void setPlotMethod(PlotLinesMethodEnum mtd) {
        pltLineMethod = mtd;
    }

    public enum PlotLinesMethodEnum {
        Lines,
        Splines
    }
}
