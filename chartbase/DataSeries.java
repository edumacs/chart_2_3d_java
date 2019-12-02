package com.edumacs.chart.base;

import com.edumacs.algebra.MinMaxValue;
import com.edumacs.chart.chart3d.BarStyle;
import com.edumacs.chart.chart3d.Point3;
import com.edumacs.chart.chart3d.Point4;
import java.util.ArrayList;
import javafx.geometry.Point2D;

/**
 * This Class is for setting data up
 *
 * @author mohammadfaizun
 */
public class DataSeries {

    private ArrayList<Point2D> pointList = new ArrayList();
    private ArrayList<Point3> point3List = new ArrayList();
    private LineStyle lineStyle = new LineStyle();
    private BarStyle barStyle = new BarStyle();
    private SymbolStyle symbol = new SymbolStyle();
    private String seriesName = "Default Name";
    private double xdataMin = -5.0;
    private double ydataMin = -5.0;
    private double zdataMin = -5.0;
    private double xdataMax = 5.0;
    private double ydataMax = 5.0;
    private double zdataMax = 5.0;
    private double xSpacing = 1;
    private double ySpacing = 1;
    private double zSpacing = 1;
    private Point3[][] pointArray;
    private Point4[][][] point4Array;
    private MinMaxValue mx4;

    public DataSeries() {
    }

    // GETTER AND SETTER FUNCTIONS
    public double getXDataMin() {
        return xdataMin;
    }

    public void setXDataMin(double value) {
        xdataMin = value;

    }

    public double getXDataMax() {
        return xdataMax;
    }

    public void setXDataMax(double value) {
        xdataMax = value;

    }

    public double getYDataMax() {
        return ydataMax;
    }

    public void setYDataMax(double value) {
        ydataMax = value;

    }

    public double getYDataMin() {
        return ydataMin;
    }

    public void setYDataMin(double value) {
        ydataMin = value;
    }

    public double getZDataMax() {
        return zdataMax;
    }

    public void setZDataMax(double value) {
        zdataMax = value;

    }

    public double getZDataMin() {
        return zdataMin;
    }

    public void setZDataMin(double value) {
        zdataMin = value;
    }

    public double getVDataMin() {
        mx4 = new MinMaxValue(point4Array);
        return mx4.getMinVal();
    }

    public double getVDataMax() {
        mx4 = new MinMaxValue(point4Array);
        return mx4.getMinVal();
    }

    public void AddPoint3(Point3 pt) {
        point3List.add(pt);
    }

    public void setPoint3List(ArrayList value) {
        point3List = value;
    }

    public Point4[][][] getPoint4Array() {
        return point4Array;
    }

    public void setPoint4Array(Point4[][][] value) {
        point4Array = value;
    }

    public Point3[][] getPointArray() {
        return pointArray;
    }

    public void setPointArray(Point3[][] value) {
        pointArray = value;
    }

    public int getXNumber() {
        return (int) ((xdataMax - xdataMin) / xSpacing) + 1;
    }

    public int getYNumber() {
        return (int) ((ydataMax - ydataMin) / ySpacing) + 1;
    }

    public int getZNumber() {
        return (int) ((zdataMax - zdataMin) / zSpacing) + 1;
    }

    public double getXSpacing() {
        return xSpacing;
    }

    public void setXSpacing(double value) {
        xSpacing = value;
    }

    public double getYSpacing() {
        return ySpacing;
    }

    public void setYSpacing(double value) {
        ySpacing = value;
    }

    public double getZSpacing() {
        return zSpacing;
    }

    public void setZSpacing(double value) {
        zSpacing = value;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle value) {
        lineStyle = value;
    }

    public BarStyle getBarStyle() {
        return barStyle;
    }

    public void setBarStyle(BarStyle value) {
        barStyle = value;
    }

    public ArrayList getPoint3List() {
        return point3List;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String nm) {
        seriesName = nm;
    }

    public ArrayList<Point2D> getPointList() {
        return pointList;
    }

    public void setPointList(ArrayList<Point2D> lst) {
        pointList = lst;
    }

    public void AddPoint(Point2D pt) {
        pointList.add(pt);
    }

    public SymbolStyle getSymbolStyle() {
        return symbol;
    }

    public void getSymbolStyle(SymbolStyle smb) {
        symbol = smb;
    }

}
