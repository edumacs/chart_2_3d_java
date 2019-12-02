package com.edumacs.chart.chart3d;

import javafx.geometry.Point2D;

/**
 * Transform a point to the
 *
 * @author mohammadfaizun
 */
public class PointF {

    private final double xmin, xmax, ymin, ymax, width, height;

    public PointF(double xmin, double xmax,
            double ymin, double ymax,
            double width, double height) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.width = width;
        this.height = height;

    }

    public Point2D Point(Point2D pt) {
        if (pt.getX() < xmin || pt.getX() > xmax
                || pt.getY() < ymin || pt.getY() > ymax) {
            pt = new Point2D(Double.NaN, Double.NaN);
        }
        double x = (pt.getX() - xmin) * width / (xmax - xmin);
        double y = height - (pt.getY() - ymin) * height / (ymax - ymin);
        return (new Point2D(x, y));
    }
}
