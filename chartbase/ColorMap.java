package com.edumacs.chart.base;

/**
 *
 * @author mohammadfaizun
 */
public class ColorMap {

    private int colormapLength = 64;
    private int alphaValue = 200;

    public ColorMap() {
    }

    public ColorMap(int colorLength) {
        colormapLength = colorLength;
    }

    public ColorMap(int colorLength, int alpha) {
        colormapLength = colorLength;
        alphaValue = alpha;
    }

    public int[][] Spring() {
        int[][] cmap = new int[colormapLength][4];
        double[] spring = new double[colormapLength];
        for (int i = 0; i < colormapLength; i++) {
            spring[i] = 1.0 * i / (colormapLength - 1);

            cmap[i][0] = 255;
            cmap[i][1] = (int) (255 * spring[i]);
            cmap[i][2] = 255 - cmap[i][1];
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

    public int[][] Summer() {
        int[][] cmap = new int[colormapLength][4];
        double[] summer = new double[colormapLength];
        for (int i = 0; i < colormapLength; i++) {
            summer[i] = 1.0 * i / (colormapLength - 1);

            cmap[i][0] = (int) (255 * summer[i]);
            cmap[i][1] = (int) (255 * 0.5 * (1 + summer[i]));
            cmap[i][2] = (int) (255 * 0.4);
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

    public int[][] Autumn() {

        int[][] cmap = new int[colormapLength][4];
        double[] autumn = new double[colormapLength];
        for (int i = 0; i < colormapLength; i++) {
            autumn[i] = 1.0 * i / (colormapLength - 1);

            cmap[i][0] = 255;
            cmap[i][1] = (int) (255 * autumn[i]);
            cmap[i][2] = 0;
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

    public int[][] Winter() {
        int[][] cmap = new int[colormapLength][4];
        double[] winter = new double[colormapLength];
        for (int i = 0; i < colormapLength; i++) {
            winter[i] = 1.0 * i / (colormapLength - 1);

            cmap[i][0] = 0;
            cmap[i][1] = (int) (255 * winter[i]);
            cmap[i][2] = (int) (255 * (1.0 - 0.5 * winter[i]));
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

    public int[][] Gray() {

        int[][] cmap = new int[colormapLength][4];
        double[] gray = new double[colormapLength];
        for (int i = 0; i < colormapLength; i++) {
            gray[i] = 1.0 * i / (colormapLength - 1);

            cmap[i][0] = (int) (255 * gray[i]);
            cmap[i][1] = (int) (255 * gray[i]);
            cmap[i][2] = (int) (255 * gray[i]);
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

    public int[][] Jet() {

        int[][] cmap = new int[colormapLength][4];
        double[][] cMatrix = new double[colormapLength][3];

        int n = (int) Math.ceil(colormapLength / 4.0);
        int nMod = 0;
        int fL = 3 * n - 1;
        double[] fArray = new double[fL];
        int[] red = new int[fL];
        int[] green = new int[fL];
        int[] blue = new int[fL];
        if (colormapLength % 4 == 1) {
            nMod = 1;
        }
        for (int i = 0; i < fL; i++) {
            if (i < n) {
                fArray[i] = (double)(i + 1) / n;
            } else if (i >= n && i < 2 * n - 1) {
                fArray[i] = 1.0;
            } else if (i >= 2 * n - 1) {
                fArray[i] = (double) (3 * n - 1 - i) / n;
            }

            green[i] = (int) Math.ceil(n / 2.0) - nMod + i;
            red[i] = green[i] + n;
            blue[i] = green[i] - n;
        }
        int nb = 0;
        for (int i = 0; i < fL; i++) {
            if (blue[i] > 0) {
                nb++;
            }
        }
        for (int i = 0; i < colormapLength; i++) {
            for (int j = 0; j < fL; j++) {
                if (i == red[j] && red[j] < colormapLength) {
                    cMatrix[i][0] = fArray[i - red[0]];
                }
            }
            for (int j = 0; j < fL; j++) {
                if (i == green[j] && green[j] < colormapLength) {
                    cMatrix[i][1] = fArray[i - green[0]];
                }
            }
            for (int j = 0; j < fL; j++) {
                if (i == blue[j] && blue[j] >= 0) {
                    cMatrix[i][2] = fArray[fL - 1 - nb + i];
                }
            }
        }
        for (int i = 0; i < colormapLength; i++) {
            for (int j = 0; j < 3; j++) {
                cmap[i][j] = (int) (cMatrix[i][j] * 255);
            }
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

    public int[][] Hot() {
        int[][] cmap = new int[colormapLength][4];
        int n = 3 * colormapLength / 8;
        double[] red = new double[colormapLength];
        double[] green = new double[colormapLength];
        double[] blue = new double[colormapLength];
        for (int i = 0; i < colormapLength; i++) {
            if (i < n) {
                red[i] = 1.0 * (i + 1) / n;
                green[i] = 0;
                if (i < 2 * n) {
                    blue[i] = 0.0;
                }
            } else if (i >= n) {
                red[i] = 1.0;
            } else if (i >= n && i < 2 * n) {
                green[i] = 1.0 * (i + 1 - n) / n;
            } else {
                green[i] = 1;
                blue[i] = 1.0f * (i + 1 - 2 * n) / (colormapLength - 2 * n);
            }

            cmap[i][0] = (int) (255 * red[i]);
            cmap[i][1] = (int) (255 * green[i]);
            cmap[i][2] = (int) (255 * blue[i]);
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

    public int[][] Cool() {

        int[][] cmap = new int[colormapLength][4];
        double[] cool = new double[colormapLength];
        for (int i = 0; i < colormapLength; i++) {
            cool[i] = 1.0 * i / (colormapLength - 1);
            cmap[i][0] = (int) (255 * cool[i]);
            cmap[i][1] = (int) (255 * (1 - cool[i]));
            cmap[i][2] = 255;
            cmap[i][3] = alphaValue;
        }
        return cmap;
    }

}
