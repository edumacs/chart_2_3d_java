package com.edumacs.chart.chart3d;

/**
 *
 * @author mohammadfaizun
 *
 */
public class Matrix3 {

    public double[][] M = new double[4][4];

    public Matrix3() {
        Identity3();
    }

    public Matrix3(double m00, double m01, double m02, double m03,
            double m10, double m11, double m12, double m13,
            double m20, double m21, double m22, double m23,
            double m30, double m31, double m32, double m33) {
        M[0][0] = m00;
        M[0][1] = m01;
        M[0][2] = m02;
        M[0][3] = m03;
        M[1][0] = m10;
        M[1][1] = m11;
        M[1][2] = m12;
        M[1][3] = m13;
        M[2][0] = m20;
        M[2][1] = m21;
        M[2][2] = m22;
        M[2][3] = m23;
        M[3][0] = m30;
        M[3][1] = m31;
        M[3][2] = m32;
        M[3][3] = m33;
    }

    //Identity Matrix
    public final void Identity3() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    M[i][j] = 1;
                } else {
                    M[i][j] = 0;
                }
            }
        }
    }

    // Multiply two matrices together:
    public static Matrix3 mult(Matrix3 m1, Matrix3 m2) {
        Matrix3 result = new Matrix3();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double element = 0;
                for (int k = 0; k < 4; k++) {
                    element += m1.M[i][k] * m2.M[k][j];
                }
                result.M[i][j] = element;
            }
        }
        return result;
    }

    // Apply a transformation to a vector (point): 
    public double[] VectorMultiply(double[] vector) {
        double[] result = new double[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i] += M[i][j] * vector[j];
            }
        }
        return result;
    }

    // Create a scaling matrix:
    public static Matrix3 Scale3(double sx, double sy, double sz) {
        Matrix3 result = new Matrix3();
        result.M[0][0] = sx;
        result.M[1][1] = sy;
        result.M[2][2] = sz;
        return result;

    }

    public static Matrix3 AzimuthElevation(double elevation, double azimuth) {

        Matrix3 result = new Matrix3();
        // make sure elevation in the range of [-90, 90]: 
        if (elevation > 90) {
            elevation = 90;
        } else if (elevation < -90) {
            elevation = -90;
        }
        // Make sure azimuth in the range of [-180, 180]:
        if (azimuth > 180) {
            azimuth = 180;
        } else if (azimuth < -180) {
            azimuth = -180;
        }
        elevation = elevation * Math.PI / 180.0;
        double sne = Math.sin(elevation);
        double cne = Math.cos(elevation);
        azimuth = azimuth * Math.PI / 180.0;
        double sna = Math.sin(azimuth);
        double cna = Math.cos(azimuth);
        result.M[0][0] = cna;
        result.M[0][1] = sna;
        result.M[0][2] = 0;
        result.M[1][0] = -sne * sna;
        result.M[1][1] = sne * cna;
        result.M[1][2] = cne;
        result.M[2][0] = cne * sna;
        result.M[2][1] = -cne * cna;
        result.M[2][2] = sne;
        return result;
    }
}
