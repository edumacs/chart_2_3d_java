package com.edumacs.chart.chart3d;

import com.edumacs.chart.base.DataSeries;
import com.edumacs.algebra.MinMaxValue;
import javafx.scene.paint.Color;

/**
 *
 * @author mohammadfaizun
 */
public class ChartFunctions {

    public ChartFunctions() {
    }

    public void Line3D(DataSeries ds, ChartStyle3D cs) {
        cs.getGridStyle().setLineColor(Color.LIGHTBLUE);
        cs.setXMin(-1);
        cs.setXMax(1);
        cs.setYMin(-1);
        cs.setYMax(1);
        cs.setZMin(0);
        cs.setZMax(30);
        cs.setXTick(0.5);
        cs.setYTick(0.5);
        cs.setZTick(5.0);
        cs.setIsXGrid(true);
        cs.setIsYGrid(true);
        cs.setIsZGrid(true);
        cs.setTitle("Paijo");
        cs.AddChartStyle();
        ds.getPoint3List().clear();
        for (int i = 0; i < 300; i++) {
            double t = 0.1 * i;
            double x = Math.exp(-t / 30)
                    * Math.cos(t);
            double y = Math.exp(-t / 30) * Math.sin(t);
            double z = t;
            ds.AddPoint3(new Point3(x, y, z, 1));
        }

    }

    public void Peak3D(DataSeries ds, ChartStyle3D cs) {
        cs.setXMin(-3);
        cs.setXMax(3);
        cs.setYMin(-3);
        cs.setYMax(3);
        cs.setZMin(-8);
        cs.setZMax(8);
        cs.setXTick(1.0);
        cs.setYTick(1.0);
        cs.setZTick(4.0);
        ds.setXSpacing(0.1);
        ds.setYSpacing(0.1);
        ds.setZSpacing(0.1);
        ds.setXDataMin(cs.getXMin());
        ds.setYDataMin(cs.getYMin());
        cs.AddChartStyle();
        Point3[][] pts = new Point3[ds.getXNumber()][ds.getYNumber()];
        for (int i = 0; i < ds.getXNumber(); i++) {
            for (int j = 0; j < ds.getYNumber(); j++) {
                double x = ds.getXDataMin() + i * ds.getXSpacing();
                double y = ds.getYDataMin() + j * ds.getYSpacing();
                double zz = 3 * Math.pow((1 - x), 2)
                        * Math.exp(-x * x - (y + 1) * (y + 1)) - 10
                        * (0.2 * x - Math.pow(x, 3) - Math.pow(y, 5))
                        * Math.exp(-x * x - y * y) - 1 / 3
                        * Math.exp(-(x + 1) * (x + 1) - y * y);
                double z = zz;
                pts[i][j] = new Point3(x, y, z, 1);
            }
        }
        ds.setPointArray(pts);
    }

    public void Image(double[][] img, DataSeries ds, ChartStyle3D cs) {
        //
        int row = img.length;
        int col = img[0].length;
        cs.setXMin(0);
        cs.setXMax(row - 1);
        cs.setYMin(0);
        cs.setYMax(col - 1);
        cs.setZMin(0);
        ds.setZSpacing(1);
        MinMaxValue mx = new MinMaxValue(img);
        cs.setZMax((int) mx.getMaxVal());
        //System.out.println(mx.getMaxVal());
        cs.setXTick(1.0);
        cs.setYTick(1.0);
        cs.setZTick(1.0);
        cs.setIsColorBar(false);
        ds.setXDataMin(cs.getXMin());
        ds.setYDataMin(cs.getYMin());
        Point3[][] pts = new Point3[ds.getXNumber()][ds.getYNumber()];
        for (int i = 0; i < ds.getXNumber(); i++) {
            for (int j = 0; j < ds.getYNumber(); j++) {
                double x = ds.getXDataMin() + i;
                double y = ds.getYDataMin() + j;
                double z = img[(int) (i * row / ds.getXNumber())][(int) (j * col / ds.getYNumber())];
                pts[i][j] = new Point3(x, y, z, 1);
            }
        }
        ds.setPointArray(pts);

    }

    public void SinROverR3D(DataSeries ds, ChartStyle3D cs) {
        cs.setXMin(-8);
        cs.setXMax(8);
        cs.setYMin(-8);
        cs.setYMax(8);
        cs.setZMin(-1);
        cs.setZMax(1);
        cs.setXTick(4.0);
        cs.setYTick(4.0);
        cs.setZTick(0.5);
        cs.setIsXGrid(true);
        cs.setIsYGrid(true);
        cs.getGridStyle().setLineColor(Color.BLUE);
        cs.getGridStyle().setDashed(2);
        cs.setTitle("NTitle");
        //cs.setIsColorBar(false);
        ds.setXSpacing(0.2);
        ds.setYSpacing(0.2);
        ds.setZSpacing(0.2);
        ds.getLineStyle().setIsVisible(false);
        // dc.setIsInterp(false);
        ds.setXDataMin(cs.getXMin());
        ds.setYDataMin(cs.getYMin());
        cs.AddChartStyle();  // add axes
        // ds.setXSpacing(1.0);
        // ds.setYSpacing(1.0);
        Point3[][] pts = new Point3[ds.getXNumber()][ds.getYNumber()];
        for (int i = 0; i < ds.getXNumber(); i++) {
            for (int j = 0; j < ds.getYNumber(); j++) {
                double x = ds.getXDataMin() + i * ds.getXSpacing();
                double y = ds.getYDataMin() + j * ds.getYSpacing();
                double r = Math.sqrt(x * x + y * y)
                        + 0.000001;
                double z = Math.sin(r) / r;
                pts[i][j] = new Point3(x, y, z, 1);
            }
        }
        ds.setPointArray(pts);
    }

    public void Exp4D(DataSeries ds, ChartStyle3D cs) {
        cs.setXMin(2);
        cs.setXMax(2);
        cs.setYMin(-2);
        cs.setYMax(2);
        cs.setZMin(-2);
        cs.setZMax(2);
        cs.setXTick(1.0);
        cs.setYTick(1.0);
        cs.setZTick(1.0);
        cs.AddChartStyle();
        ds.setXDataMin(cs.getXMin());
        ds.setYDataMin(cs.getYMin());
        Point4[][][] pts = new Point4[ds.getXNumber()][ds.getYNumber()][ds.getZNumber()];
        for (int i = 0; i < ds.getXNumber(); i++) {
            for (int j = 0; j < ds.getYNumber(); j++) {
                for (int k = 0; k < ds.getZNumber(); k++) {
                    double x = ds.getXDataMin() + i * ds.getXSpacing();
                    double y = ds.getYDataMin() + j * ds.getYSpacing();
                    double z = cs.getZMin() + k * ds.getZSpacing();
                    double v = z * Math.exp(-x * x - y * y - z * z);
                    pts[i][j][k] = new Point4(new Point3(x, y, z, 1), v);
                }
            }
        }
        ds.setPoint4Array(pts);
    }

}
