package com.edumacs.chart.chart3d;

/**
 *
 * @author mohammadfaizun
 */
public class Point4 {

    public Point3 point3 = new Point3();
    public double V = 0;

    public Point4() {
    }

    public Point4(Point3 pt3, double v) {
        point3 = pt3;
        V = v;
    }
}
