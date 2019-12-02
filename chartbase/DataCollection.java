package com.edumacs.chart.base;

import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author moham
 */
public class DataCollection {

    private ArrayList<DataSeries> dataSeriesList;
    private int dataSeriesIndex = 0;

    public DataCollection() {
        dataSeriesList = new ArrayList();
    }

    public ArrayList<DataSeries> getDataSeriesList() {
        return dataSeriesList;
    }

    public void setDataSeriesList(ArrayList<DataSeries> lst) {
        dataSeriesList = lst;
    }

    public int getDataSeriesIndex() {
        return dataSeriesIndex;
    }

    public void setDataSeriesIndex(int idx) {
        dataSeriesIndex = idx;
    }

    public void Add(DataSeries ds) {
        dataSeriesList.add(ds);
        if ("Default Name".equals(ds.getSeriesName())) {
            ds.setSeriesName("DataSeries" + dataSeriesList.size());
        }
    }

    public void Insert(int dataSeriesIndex, DataSeries ds) {
        dataSeriesList.add(dataSeriesIndex, ds);
        if ("Default Name".equals(ds.getSeriesName())) {
            dataSeriesIndex = dataSeriesIndex + 1;
            ds.setSeriesName("DataSeries" + dataSeriesIndex);
        }
    }

    public void Remove(String dataSeriesName) {
        if (dataSeriesList != null) {
            for (int i = 0; i < dataSeriesList.size(); i++) {
                DataSeries ds = dataSeriesList.get(i);
                if (ds.getSeriesName().equals(dataSeriesName)) {
                    dataSeriesList.remove(i);
                }
            }
        }
    }

    public void RemoveAll() {
        dataSeriesList.clear();
    }

    public void AddLines(GraphicsContext gc, ChartStyle cs) {
        dataSeriesList.stream().map((ds) -> {
            if (ds.getLineStyle().getIsVisible() == true) {
                gc.setStroke(ds.getLineStyle().getLineColor());
                gc.setLineWidth(ds.getLineStyle().getThickness());
                gc.setLineDashes(ds.getLineStyle().getDashed());
                if (ds.getLineStyle().getPlotMethod() == LineStyle.PlotLinesMethodEnum.Lines) {
                    Point2D pt1, pt0;
                    for (int i = 1; i < ds.getPointList().size(); i++) {
                        pt0 = cs.Point(ds.getPointList().get(i - 1));
                        pt1 = cs.Point(ds.getPointList().get(i));
                        gc.strokeLine(pt0.getX(), pt0.getY(), pt1.getX(), pt1.getY());
                    }
                } else if (ds.getLineStyle().getPlotMethod() == LineStyle.PlotLinesMethodEnum.Splines) {
                    ArrayList<Point2D> al = new ArrayList();
                    for (int i = 0; i < ds.getPointList().size(); i++) {
                        Point2D pt = ds.getPointList().get(i);
                        if (pt.getX() >= cs.getXLimMin()
                                && pt.getX() <= cs.getXLimMax()
                                && pt.getY() >= cs.getYLimMin()
                                && pt.getY() <= cs.getYLimMax()) {
                            al.add(pt);
                        }
                    }
                    double[] ptsX = new double[al.size()];
                    double[] ptsY = new double[al.size()];
                    for (int i = 0; i < ptsX.length; i++) {
                        ptsX[i] = cs.Point(al.get(i)).getX();
                        ptsY[i] = cs.Point(al.get(i)).getY();
                    }
                    gc.strokePolyline(ptsX, ptsY, al.size());
                }
            }
            return ds;
        }).forEachOrdered((ds) -> {
            for (int i = 0; i < ds.getPointList().size(); i++) {
                Point2D pt = ds.getPointList().get(i);
                if (pt.getX() >= cs.getXLimMin()
                        && pt.getX() <= cs.getXLimMax()
                        && pt.getY() >= cs.getYLimMin()
                        && pt.getY() <= cs.getYLimMax()) {
                    ds.getSymbolStyle().DrawSymbol(gc, cs.Point(pt));
                }
            }
        });
    }
}
