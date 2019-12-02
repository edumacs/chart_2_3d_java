package com.edumacs.chart.chart3d;

/**
 *
 * @author mohammadfaizun
 */
public class Point3 {

    public double X;
    public double Y;
    public double Z;
    public double W = 1.0;

    public Point3() {
    }

    public Point3(double x, double y, double z, double w) {
        X = x;
        Y = y;
        Z = z;
        W = w;
    }

    public void Transform(Matrix3 m) {
        double[] trans = {X, Y, Z, W};
        double[] result = m.VectorMultiply(trans);
        X = result[0];
        Y = result[1];
        Z = result[2];
        W = result[3];
    }

    // edited here Form to GraphicsContext
    public void Transform(Matrix3 m, boolean isbar,
            double xmin, double xmax,
            double ymin, double ymax,
            double zmin, double zmax,
            double width, double height) {
        // Normalize the point: 
        double x1 = (X - xmin) / (xmax - xmin) - 0.5;
        double y1 = (Y - ymin) / (ymax - ymin) - 0.5;
        double z1 = (Z - zmin) / (zmax - zmin) - 0.5;
        double[] trans = {x1, y1, z1, W};
        double[] result = m.VectorMultiply(trans);
        X = result[0];
        Y = result[1];
        // Coordinate transformation from World to Device system: 
        double xShift = 1.05;
        double xScale = 1.0;
        double yShift = 1.05;
        double yScale = 0.9;

        if (isbar) {
            xShift = 0.95;
            xScale = 0.9;
        }

        X = (xShift + xScale * X) * width / 2;
        Y = (yShift - yScale * Y) * height / 2;
    }
}
