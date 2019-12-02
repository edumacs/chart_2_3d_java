package com.edumacs.chart.chart3d;

import com.edumacs.chart.base.DataSeries;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Combining DataSeries (Data) and ChartStyle3D (Axes)
 *
 * @author mohammadfaizun
 */
public class Plot3D {

    private ChartTypeEnum chartType;
    private int[][] cMap;
    private boolean isColorMap = true;
    private boolean isHiddenLine = false;
    private boolean isInterp = false;
    private int numberInterp = 2;
    private int numberContours = 5;
    private SliceEnum xyzSlice = SliceEnum.XSlice;
    private double sliceLocation = 0;
    private boolean isBarSingleColor = true;
    private boolean isBar;
    private final Color lineColor;
    private final double lineThickness;
    private final double dashL;
    private final boolean isLineVis;
    private final DataSeries ds;
    private GraphicsContext gc;
    private final Point3[][] pts;
    private Point3[][] pts1;
    private Point3[][] ptsb; //base
    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;
    private double zmin;
    private double zmax;
    private double elevVal;
    private double azimVal;
    private double width;
    private double height;
    private Font tickFont;
    private final int row;
    private final int col;
    private Matrix3 m;

    public Plot3D(DataSeries ds) {
        this.ds = ds;
        this.lineColor = ds.getLineStyle().getLineColor();
        this.lineThickness = ds.getLineStyle().getThickness();
        this.dashL = ds.getLineStyle().getDashed();
        isLineVis = ds.getLineStyle().getIsVisible();
        this.pts = ds.getPointArray();
        row = this.pts.length;
        col = this.pts[0].length;
    }

    public void AddChart(ChartStyle3D cs) {
        // get dynamic values
        gc = cs.getGraphicsContext();
        xmin = cs.getXMin();
        xmax = cs.getXMax();
        ymin = cs.getYMin();
        ymax = cs.getYMax();
        zmin = cs.getZMin();
        zmax = cs.getZMax();
        width = gc.getCanvas().getWidth();
        height = gc.getCanvas().getHeight();
        elevVal = cs.getElevation();
        azimVal = cs.getAzimuth();
        m = Matrix3.AzimuthElevation(elevVal, azimVal);
        isBar = cs.getIsColorBar();
        tickFont = cs.getTickFont().getFont();
        // Copy the pts to pts1
        pts1 = new Point3[row][col];
        ptsb = new Point3[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                // Make a deep copy the points array:
                pts1[i][j] = new Point3(pts[i][j].X, pts[i][j].Y, pts[i][j].Z, 1);
                ptsb[i][j] = new Point3(pts[i][j].X, pts[i][j].Y, zmin, 1);
                // transform
                pts1[i][j].Transform(m, isBar,
                        xmin, xmax,
                        ymin, ymax,
                        zmin, zmax,
                        width, height);
                ptsb[i][j].Transform(m, isBar,
                        xmin, xmax,
                        ymin, ymax,
                        zmin, zmax,
                        width, height);
            }
        }
        // draw chart
        switch (chartType) {
            case Line:
                AddLine();
                break;
            case Mesh:
                AddMesh();
                AddColorBar();
                break;
            case MeshZ:
                AddMeshZ();
                AddColorBar();
                break;
            case Waterfall:
                AddWaterFall();
                AddColorBar();
                break;
            case Surface:
                AddSurface();
                AddColorBar();
                break;
            case XYColor:
                AddXYColor();
                break;
            case Contour:
                //AddContour();
                AddXYColor3D();   // base
                AddContour3D();
                break;
            case FillContour:
                AddXYColor();
                AddContour();
                break;
            case MeshContour:
                AddXYColor3D();
                AddContour3D();
                AddMesh();
                AddColorBar();
                break;
            case SurfaceContour:
                AddXYColor3D();     // base
                // AddContour3D();   // contour at base
                AddContourL();
                AddSurface();       // surface
                AddColorBar();      //bar
                break;
            case Slice:
                AddSlice();
                AddColorBar();
                break;
            case Bar3D:
                AddBar3D();
                AddColorBar();
                break;
        }
    }

    private void AddLine() {
        // End of Point3 Transformations
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);
        Point3[] ptsL = new Point3[ds.getPoint3List().size()];
        for (int i = 0; i < ptsL.length; i++) {
            ptsL[i] = (Point3) ds.getPoint3List().get(i);
            ptsL[i].Transform(m, isBar,
                    xmin, xmax,
                    ymin, ymax,
                    zmin, zmax,
                    width, height);
        }
        // Draw line:
        for (int i = 1; i < ptsL.length; i++) {
            gc.strokeLine(ptsL[i - 1].X,
                    ptsL[i - 1].Y, ptsL[i].X, ptsL[i].Y);
        }
    }

    public void AddColorBar() {
        if (isBar && isColorMap) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.setTextAlign(TextAlignment.JUSTIFY);
            gc.setFont(tickFont);
            double size = (new Text("A")).getLayoutBounds().getHeight();
            Point3[] ptsL = new Point3[64];
            double Z_min, Z_max;
            if (chartType == ChartTypeEnum.Slice) {
                Z_min = ds.getVDataMin();
                Z_max = ds.getVDataMax();
            } else {
                Z_min = zmin;
                Z_max = zmax;
            }
            double dz = (Z_max - Z_min) / 63;

            int x = 7 * (int) (width / 8);
            int y = (int) (height / 10);
            int width_L = (int) (width / 25);
            int height_L = (int) (8 * height / 10);
            // Add color bar:
            for (int i = 0; i < 64; i++) {
                ptsL[i] = new Point3(x, y, Z_min + i * dz, 1);
            }
            for (int i = 0; i < 63; i++) {
                Color color = AddColor(ptsL[i], Z_min, Z_max);
                double y1 = y + height_L - (ptsL[i].Z - Z_min) * height_L / (Z_max - Z_min);
                double y2 = y + height_L - (ptsL[i + 1].Z - Z_min) * height_L / (Z_max - Z_min);
                double[] xS = new double[]{x, x + width_L, x + width_L, x};
                double[] yS = new double[]{y2, y2, y1, y1};
                gc.setFill(color);
                gc.fillPolygon(xS, yS, 4); //xS.length = 4

            }
            gc.strokeRect(x, y, width_L, height_L);
            // Add ticks and labels to the color bar:
            double ticklength = 0.1 * width_L;
            for (double z = Z_min; z <= Z_max; z = z
                    + (Z_max - Z_min) / 6) {
                double yy = y + height_L - (z - Z_min)
                        * height_L / (Z_max - Z_min);

                gc.strokeLine(x, yy, x + ticklength, yy);
                gc.strokeLine(x + width_L, yy, x + width_L - ticklength, yy);
                Point2D pt1 = new Point2D(x + width_L + 5, yy - size / 2);
                gc.strokeText("" + Math.round(z), pt1.getX(), pt1.getY());
            }
        }
    }

    private Color AddColor(Point3 pt, double z_min, double z_max) {
        int colorLength = cMap.length;
        int cindexMax = colorLength - 1;
        int cindex = (int) Math.round((colorLength * (pt.Z - z_min)
                + (z_max - pt.Z)) / (z_max - z_min));

        if (cindex < 0) {
            cindex = 0;
        }
        if (cindex > cindexMax) {
            cindex = cindexMax;
        }
        Color color = Color.rgb(cMap[cindex][0],
                cMap[cindex][1], cMap[cindex][2],
                (double) (cMap[cindex][3]) / 256);
        return color;
    }

    private void AddMesh() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);
        // Draw mesh:
        for (int i = 0; i < row - 1; i++) {
            for (int j = 0; j < col - 1; j++) {
                int ii = i;
                if (azimVal >= -180 && azimVal < 0) {
                    ii = row - 2 - i;
                }
                double[] xS = new double[]{pts1[ii][j].X, pts1[ii][j + 1].X,
                    pts1[ii + 1][j + 1].X, pts1[ii + 1][j].X};
                double[] yS = new double[]{pts1[ii][j].Y, pts1[ii][j + 1].Y,
                    pts1[ii + 1][j + 1].Y, pts1[ii + 1][j].Y};
                if (!isHiddenLine) {
                    gc.setFill(Color.WHITE);
                    gc.fillPolygon(xS, yS, 4); //xS.length = 4

                }
                if (isColorMap) {
                    Color color = AddColor(pts1[ii][j], zmin, zmax);
                    gc.setStroke(color);
                    gc.setLineWidth(lineThickness);
                    gc.setLineDashes(dashL);
                }
                gc.strokePolygon(xS, yS, 4);
            }
        }
    }

    private void AddMeshZ() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);
        Color color;
        // Draw mesh using Z-order method:
        for (int i = 0; i < row - 1; i++) {
            for (int j = 0; j < col - 1; j++) {
                int ii = i;
                if (azimVal >= -180 && azimVal < 0) {
                    ii = row - 2 - i;
                }
                double[] xS = new double[]{pts1[ii][j].X, pts1[ii][j + 1].X,
                    pts1[ii + 1][j + 1].X, pts1[ii + 1][j].X};
                double[] yS = new double[]{pts1[ii][j].Y, pts1[ii][j + 1].Y,
                    pts1[ii + 1][j + 1].Y, pts1[ii + 1][j].Y};

                gc.setFill(Color.WHITE);
                gc.fillPolygon(xS, yS, 4);
                if (isColorMap) {
                    color = AddColor(pts[ii][j], zmin, zmax);
                    gc.setStroke(color);
                    gc.setLineWidth(lineThickness);
                    gc.setLineDashes(dashL);

                }
                gc.strokePolygon(xS, yS, 4);
            }
        }
        // Draw curtain lines:
        for (int i = 0; i < row; i++) {
            int jj = row - 1;
            if (elevVal >= 0) {
                if (azimVal >= -90 && azimVal <= 90) {
                    jj = 0;
                }
            } else if (elevVal < 0) {
                jj = 0;
                if (azimVal >= -90 && azimVal <= 90) {
                    jj = row - 1;
                }
            }
            if (i < row - 1) {
                double[] xS = new double[]{pts1[i][jj].X, pts1[i + 1][jj].X,
                    ptsb[i + 1][jj].X, ptsb[i][jj].X};
                double[] yS = new double[]{pts1[i][jj].Y, pts1[i + 1][jj].Y,
                    ptsb[i + 1][jj].Y, ptsb[i][jj].Y};
                gc.setFill(Color.WHITE);
                gc.fillPolygon(xS, yS, 4);

                if (isColorMap) {
                    Point3 pt = new Point3(pts1[i][jj].X, pts1[i][jj].Y, pts1[i][jj].Z, 1);
                    color = AddColor(pt, zmin, zmax);
                    gc.setStroke(color);
                    gc.setLineWidth(lineThickness);
                    gc.setLineDashes(dashL);

                }
                gc.strokePolygon(xS, yS, 4);
            }
            for (int j = 0; j < col; j++) {
                int ii = 0;
                if (elevVal >= 0) {
                    if (azimVal >= 0 && azimVal <= 180) {
                        ii = col - 1;
                    }
                } else if (elevVal < 0) {
                    if (azimVal >= -180 && azimVal <= 0) {
                        ii = col - 1;
                    }
                }
                if (j < col - 1) {
                    double[] xS = new double[]{pts1[ii][j].X, pts1[ii][j + 1].X,
                        ptsb[ii][j + 1].X, ptsb[ii][j].X};
                    double[] yS = new double[]{pts1[ii][j].Y, pts1[ii][j + 1].Y,
                        ptsb[ii][j + 1].Y, ptsb[ii][j].Y};

                    gc.setFill(Color.WHITE);
                    gc.fillPolygon(xS, yS, 4);
                    if (isColorMap) {
                        Point3 pt = new Point3(pts1[ii][j].X,
                                pts1[ii][j].Y, pts1[ii][j].Z, 1);
                        color = AddColor(pt, zmin, zmax);
                        gc.setStroke(color);
                        gc.setLineWidth(lineThickness);
                        gc.setLineDashes(dashL);
                    }
                    gc.strokePolygon(xS, yS, 4);
                }
            }
        }
    }

    private void AddWaterFall() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);
        Point3[] pt3 = new Point3[col + 2];
        for (int j = 0; j < col; j++) {
            int jj = j;
            if (elevVal >= 0) {
                if (azimVal >= -90 && azimVal < 90) {
                    jj = col - 1 - j;
                }
            } else if (elevVal < 0) {
                jj = col - 1 - j;
                if (azimVal >= -90 && azimVal < 90) {
                    jj = j;
                }
            }
            for (int i = 0; i < col; i++) {
                pt3[i + 1] = new Point3(pts[i][jj].X, pts[i][jj].Y,
                        pts[i][jj].Z, 1);
                if (i == 0) {
                    pt3[0] = new Point3(pt3[i + 1].X,
                            pt3[i + 1].Y, zmin, 1);
                }
                if (i == col - 1) {
                    pt3[col + 1] = new Point3(pt3[i + 1].X,
                            pt3[i + 1].Y, zmin, 1);
                }
            }
            int nn = pt3.length;
            double[] xS = new double[nn];
            double[] yS = new double[nn];
            for (int i = 0; i < nn; i++) {
                pt3[i].Transform(m, isBar,
                        xmin, xmax,
                        ymin, ymax,
                        zmin, zmax,
                        width, height);
                xS[i] = pt3[i].X;
                yS[i] = pt3[i].Y;
            }
            gc.setFill(Color.WHITE);
            gc.fillPolygon(xS, yS, nn);
            for (int i = 1; i < pt3.length; i++) {
                if (isColorMap) {
                    Color color = AddColor(pt3[i], zmin, zmax);
                    gc.setStroke(color);
                    gc.setLineWidth(lineThickness);
                    gc.setLineDashes(dashL);
                }
                gc.strokeLine(xS[i - 1], yS[i - 1],
                        xS[i], yS[i]);
            }
        }
    }

    private void AddSurface() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);

        // Draw surface:
        if (!isInterp) {
            for (int i = 0; i < row - 1; i++) {
                for (int j = 0; j < col - 1; j++) {
                    int ii = i;
                    if (azimVal >= -180 && azimVal < 0) {
                        ii = row - 2 - i;
                    }

                    double[] xS = new double[]{pts1[ii][j].X, pts1[ii][j + 1].X,
                        pts1[ii + 1][j + 1].X, pts1[ii + 1][j].X};
                    double[] yS = new double[]{pts1[ii][j].Y, pts1[ii][j + 1].Y,
                        pts1[ii + 1][j + 1].Y, pts1[ii + 1][j].Y};
                    Color color = AddColor(pts1[ii][j], zmin, zmax);
                    gc.setFill(color);
                    gc.fillPolygon(xS, yS, 4); //pt3.length = 4;

                    if (isLineVis) {
                        gc.setStroke(lineColor);
                        gc.strokePolygon(xS, yS, 4);
                    }
                }
            }
        } // Draw refined surface:
        else if (isInterp) {
            for (int i = 0; i < row - 1; i++) {
                for (int j = 0; j < col - 1; j++) {
                    int ii = i;
                    if (azimVal >= -180 && azimVal < 0) {
                        ii = row - 2 - i;
                    }
                    Point3[] points = new Point3[4];
                    points[0] = pts[ii][j];
                    points[1] = pts[ii][j + 1];
                    points[2] = pts[ii + 1][j + 1];
                    points[3] = pts[ii + 1][j];
                    Interp(m, points, zmin, zmax, 1);
                    if (isLineVis) {
                        double[] xS = new double[]{pts1[ii][j].X, pts1[ii][j + 1].X,
                            pts1[ii + 1][j + 1].X, pts1[ii + 1][j].X};
                        double[] yS = new double[]{pts1[ii][j].Y, pts[ii][j + 1].Y,
                            pts1[ii + 1][j + 1].Y, pts1[ii + 1][j].Y};
                        gc.setStroke(lineColor);
                        gc.strokePolygon(xS, yS, 4); //points.length = 4;
                    }
                }
            }
        }
    }

    private void Interp(Matrix3 m, Point3[] pta, double Zmin,
            double Zmax, int flag) {
        Point2D[] points = new Point2D[4];
        int npoints = numberInterp;
        Point3[][] ptsL = new Point3[npoints + 1][npoints + 1];
        Point3[][] pts1L = new Point3[npoints + 1][npoints + 1];
        double x0 = pta[0].X;
        double y0 = pta[0].Y;
        double x1 = pta[2].X;
        double y1 = pta[2].Y;
        double dx = (x1 - x0) / npoints;
        double dy = (y1 - y0) / npoints;
        double C00 = pta[0].Z;
        double C10 = pta[3].Z;
        double C11 = pta[2].Z;
        double C01 = pta[1].Z;
        double x, y, C;
        // for Point F
        PointF ptF = new PointF(xmin, xmax,
                ymin, ymax,
                width, height);
        // end of PointF
        Color color;
        switch (flag) {
            // For Surface chart:
            case 1:
                for (int i = 0; i <= npoints; i++) {
                    x = x0 + i * dx;
                    for (int j = 0; j <= npoints; j++) {
                        y = y0 + j * dy;
                        C = (y1 - y) * ((x1 - x) * C00
                                + (x - x0) * C10) / (x1 - x0) / (y1 - y0)
                                + (y - y0) * ((x1 - x) * C01
                                + (x - x0) * C11) / (x1 - x0) / (y1 - y0);
                        ptsL[i][j] = new Point3(x, y, C, 1);
                        ptsL[i][j].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                    }
                }
                for (int i = 0; i < npoints; i++) {
                    for (int j = 0; j < npoints; j++) {
                        color = AddColor(ptsL[i][j], Zmin, Zmax);
                        double[] xS = new double[]{ptsL[i][j].X, ptsL[i + 1][j].X,
                            ptsL[i + 1][j + 1].X, ptsL[i][j + 1].X};
                        double[] yS = new double[]{ptsL[i][j].Y, ptsL[i + 1][j].Y,
                            ptsL[i + 1][j + 1].Y, ptsL[i][j + 1].Y};
                        gc.setFill(color);
                        gc.fillPolygon(xS, yS, 4);
                    }
                }
                break;
            // For XYColor chart:
            case 2:
                for (int i = 0; i <= npoints; i++) {
                    x = x0 + i * dx;
                    for (int j = 0; j <= npoints; j++) {
                        y = y0 + j * dy;
                        C = (y1 - y) * ((x1 - x) * C00
                                + (x - x0) * C10) / (x1 - x0) / (y1 - y0)
                                + (y - y0) * ((x1 - x) * C01
                                + (x - x0) * C11) / (x1 - x0) / (y1 - y0);
                        ptsL[i][j] = new Point3(x, y, C, 1);
                    }
                }
                for (int i = 0; i < npoints; i++) {
                    for (int j = 0; j < npoints; j++) {

                        color = AddColor(ptsL[i][j], Zmin, Zmax);
                        points[0] = ptF.Point(new Point2D(ptsL[i][j].X, ptsL[i][j].Y));
                        points[1] = ptF.Point(new Point2D(ptsL[i + 1][j].X,
                                ptsL[i + 1][j].Y));
                        points[2] = ptF.Point(new Point2D(ptsL[i + 1][j + 1].X,
                                ptsL[i + 1][j + 1].Y));
                        points[3] = ptF.Point(new Point2D(ptsL[i][j + 1].X,
                                ptsL[i][j + 1].Y));
                        double[] xS = new double[]{points[0].getX(), points[1].getX(),
                            points[2].getX(), points[3].getX()};
                        double[] yS = new double[]{points[0].getY(), points[1].getY(),
                            points[2].getY(), points[3].getY()};
                        gc.setFill(color);
                        gc.fillPolygon(xS, yS, points.length);
                    }
                }
                break;
            // For XYColor3D chart:
            case 3:
                for (int i = 0; i <= npoints; i++) {
                    x = x0 + i * dx;
                    for (int j = 0; j <= npoints; j++) {
                        y = y0 + j * dy;
                        C = (y1 - y) * ((x1 - x) * C00
                                + (x - x0) * C10) / (x1 - x0) / (y1 - y0)
                                + (y - y0) * ((x1 - x) * C01
                                + (x - x0) * C11) / (x1 - x0) / (y1 - y0);
                        pts1L[i][j] = new Point3(x, y, C, 1);
                        ptsL[i][j] = new Point3(x, y, zmin, 1);
                        ptsL[i][j].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                    }
                }
                for (int i = 0; i < npoints; i++) {
                    for (int j = 0; j < npoints; j++) {
                        color = AddColor(pts1L[i][j], Zmin, Zmax);
                        double[] xS = new double[]{ptsL[i][j].X, ptsL[i + 1][j].X,
                            ptsL[i + 1][j + 1].X, ptsL[i][j + 1].X};
                        double[] yS = new double[]{ptsL[i][j].Y, ptsL[i + 1][j].Y,
                            ptsL[i + 1][j + 1].Y, ptsL[i][j + 1].Y};
                        gc.setFill(color);
                        gc.fillPolygon(xS, yS, points.length);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void AddXYColor() {
        // for Point F
        PointF ptF = new PointF(xmin, xmax, ymin, ymax, width, height);
        // end of PointF
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);

        Point2D[] pta = new Point2D[4];
        // Draw surface on the XY plane:
        if (!isInterp) {

            for (int i = 0; i < pts[0].length - 1; i++) {
                for (int j = 0; j < pts[1].length - 1; j++) {
                    pta[0] = ptF.Point(new Point2D(pts[i][j].X, pts[i][j].Y));
                    pta[1] = ptF.Point(new Point2D(pts[i][j + 1].X, pts[i][j + 1].Y));
                    pta[2] = ptF.Point(new Point2D(pts[i + 1][j + 1].X, pts[i + 1][j + 1].Y));
                    pta[3] = ptF.Point(new Point2D(pts[i + 1][j].X, pts[i + 1][j].Y));

                    Color color = AddColor(pts[i][j], zmin, zmax);

                    double[] xS = new double[]{pta[0].getX(), pta[1].getX(),
                        pta[2].getX(), pta[3].getX()};
                    double[] yS = new double[]{pta[0].getY(), pta[1].getY(),
                        pta[2].getY(), pta[3].getY()};
                    gc.setFill(color);
                    gc.fillPolygon(xS, yS, 4);

                    if (isLineVis) {
                        gc.setFill(lineColor);
                        gc.strokePolygon(xS, yS, 4);
                    }
                }
            }
        } // Draw refined surface:
        else if (isInterp) {
            Matrix3 mt = new Matrix3(); // identity matrix
            for (int i = 0; i < pts[0].length - 1; i++) {
                for (int j = 0; j < pts[1].length - 1; j++) {
                    Point3[] points = new Point3[4];
                    points[0] = pts[i][j];
                    points[1] = pts[i][j + 1];
                    points[2] = pts[i + 1][j + 1];
                    points[3] = pts[i + 1][j];
                    Interp(mt, points, zmin, zmax, 2);
                    pta[0] = ptF.Point(new Point2D(pts[i][j].X, pts[i][j].Y));
                    pta[1] = ptF.Point(new Point2D(pts[i][j + 1].X, pts[i][j + 1].Y));
                    pta[2] = ptF.Point(new Point2D(pts[i + 1][j + 1].X, pts[i + 1][j + 1].Y));
                    pta[3] = ptF.Point(new Point2D(pts[i + 1][j].X, pts[i + 1][j].Y));
                    double[] xS = new double[]{pta[0].getX(), pta[1].getX(),
                        pta[2].getX(), pta[3].getX()};
                    double[] yS = new double[]{pta[0].getY(), pta[1].getY(),
                        pta[2].getY(), pta[3].getY()};
                    gc.setFill(lineColor);
                    if (isLineVis) {
                        gc.strokePolygon(xS, yS, 4);
                    }
                }
            }
        }
    }

    private void AddXYColor3D() {
        boolean lineVis = ds.getLineStyle().getIsVisible();
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);
        if (!isInterp) {
            for (int i = 0; i < row - 1; i++) {
                for (int j = 0; j < col - 1; j++) {
                    Color color = AddColor(pts[i][j], zmin, zmax);
                    double[] xS = new double[]{ptsb[i][j].X, ptsb[i][j + 1].X,
                        ptsb[i + 1][j + 1].X, ptsb[i + 1][j].X};
                    double[] yS = new double[]{ptsb[i][j].Y, ptsb[i][j + 1].Y,
                        ptsb[i + 1][j + 1].Y, ptsb[i + 1][j].Y};
                    gc.setFill(color);
                    gc.fillPolygon(xS, yS, 4);
                    if (lineVis) {
                        gc.setStroke(lineColor);
                        gc.strokePolygon(xS, yS, 4);
                    }
                }
            }
        } // Draw refined surface:
        else if (isInterp) {
            for (int i = 0; i < row - 1; i++) {
                for (int j = 0; j < col - 1; j++) {
                    Point3[] points = new Point3[4];
                    points[0] = pts[i][j];
                    points[1] = pts[i][j + 1];
                    points[2] = pts[i + 1][j + 1];
                    points[3] = pts[i + 1][j];
                    Interp(m, points, zmin, zmax, 3);
                }
            }
        }
    }

    private void AddContour() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);

        Point3[] pta = new Point3[2];
        double[] zlevels = new double[numberContours];
        for (int i = 0; i < numberContours; i++) {
            zlevels[i] = zmin + i * (zmax - zmin) / (numberContours - 1);
        }
        int i0, i1, i2, j0, j1, j2;
        double zratio;
        // Draw contour on the XY plane:
        for (int i = 0; i < row - 1; i++) {
            for (int j = 0; j < col - 1; j++) {
                if (isColorMap && chartType != ChartTypeEnum.FillContour) {
                    Color colorPen1 = AddColor(pts[i][j], zmin, zmax);
                    gc.setStroke(colorPen1);
                    gc.setLineWidth(lineThickness);
                    gc.setLineDashes(dashL);
                }

                for (int k = 0; k < numberContours; k++) {
                    // Left triangle:
                    i0 = i;
                    j0 = j;
                    i1 = i;
                    j1 = j + 1;
                    i2 = i + 1;
                    j2 = j + 1;
                    if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X, (1 - zratio)
                                * pts[i0][j0].Y + zratio * pts[i1][j1].Y,
                                zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3((1 - zratio)
                                * pts[i1][j1].X + zratio * pts[i2][j2].X,
                                pts[i1][j1].Y, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y,
                                pta[1].X, pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[0] = new Point3((1 - zratio)
                                * pts[i0][j0].X + zratio * pts[i2][j2].X,
                                (1 - zratio) * pts[i0][j0].Y
                                + zratio * pts[i2][j2].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3((1 - zratio)
                                * pts[i1][j1].X + zratio * pts[i2][j2].X,
                                pts[i1][j1].Y, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X, (1 - zratio)
                                * pts[i0][j0].Y + zratio * pts[i1][j1].Y,
                                zmin, 1);
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[1] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    }
                    // right triangle:
                    i0 = i;
                    j0 = j;
                    i1 = i + 1;
                    j1 = j;
                    i2 = i + 1;
                    j2 = j + 1;
                    if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i1][j1].X * zratio,
                                pts[i0][j0].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3(pts[i1][j1].X, pts[i1][j1].Y
                                * (1 - zratio) + pts[i2][j2].Y * zratio,
                                zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3(pts[i1][j1].X,
                                pts[i1][j1].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i1][j1].X * zratio,
                                pts[i0][j0].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[1] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    }
                }
            }
        }
    }

    private void AddContour3D() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);

        Point3[] pta = new Point3[2];
        double[] zlevels = new double[numberContours];
        for (int i = 0; i < numberContours; i++) {
            zlevels[i] = zmin + i * (zmax - zmin) / (numberContours - 1);
        }
        int i0, i1, i2, j0, j1, j2;
        double zratio;
        // Draw contour on the XY plane:
        for (int i = 0; i < pts[0].length - 1; i++) {
            for (int j = 0; j < pts[1].length - 1; j++) {
                for (int k = 0; k < numberContours; k++) {
                    // Left triangle:
                    i0 = i;
                    j0 = j;
                    i1 = i;
                    j1 = j + 1;
                    i2 = i + 1;
                    j2 = j + 1;
                    if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X, (1 - zratio)
                                * pts[i0][j0].Y + zratio * pts[i1][j1].Y,
                                zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3((1 - zratio)
                                * pts[i1][j1].X + zratio * pts[i2][j2].X,
                                pts[i1][j1].Y, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y,
                                pta[1].X, pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[0] = new Point3((1 - zratio)
                                * pts[i0][j0].X + zratio * pts[i2][j2].X,
                                (1 - zratio) * pts[i0][j0].Y
                                + zratio * pts[i2][j2].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3((1 - zratio)
                                * pts[i1][j1].X + zratio * pts[i2][j2].X,
                                pts[i1][j1].Y, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X, (1 - zratio)
                                * pts[i0][j0].Y + zratio * pts[i1][j1].Y,
                                zmin, 1);
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[1] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    }
                    // right triangle:
                    i0 = i;
                    j0 = j;
                    i1 = i + 1;
                    j1 = j;
                    i2 = i + 1;
                    j2 = j + 1;
                    if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i1][j1].X * zratio,
                                pts[i0][j0].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3(pts[i1][j1].X, pts[i1][j1].Y
                                * (1 - zratio) + pts[i2][j2].Y * zratio,
                                zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3(pts[i1][j1].X,
                                pts[i1][j1].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i1][j1].X * zratio,
                                pts[i0][j0].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[1] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    }
                }
            }
        }
    }

    private void AddContourL() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);

        Point3[] pta = new Point3[2];
        int numCont = 4;
        double[] zlevels = new double[numCont];
        for (int i = 0; i < numCont; i++) {
            zlevels[i] = zmin + i * (zmax - zmin) / (numCont - 1);
        }

        /* double sumT = 0.0;
        for (int i = 0; i <= (int) (pts.length / 2); i++) {
            for (int j = 0; j <= (int) (pts[0].length / 2); j++) {
                sumT += pts[i][j].Z;
            }
        }
        int itNum = (int) ((zmax - zmin) / 1);
        double sumL = 0.0;
        double zl = pts[(int) (pts.length / 2)][(int) (pts[0].length / 2)].Z;
        for (int k = 0; k < itNum; k++) {
            for (int i = (int) (pts.length / 2); i < pts.length; i++) {
                for (int j = (int) (pts[0].length / 2); j < pts[0].length; j++) {
                    if (pts[i][j].Z >= zl) {
                        sumL += pts[i][j].Z / sumT;
                    }
                    if (sumL == 0.5) {
                        zlevels[1] = pts[i][j].Z;
                    }
                }
            }
        }*/
        // for iteration
        int i0, i1, i2, j0, j1, j2;
        double zratio;
        // Draw contour on the XY plane:
        for (int i = 0; i < pts[0].length - 1; i++) {
            for (int j = 0; j < pts[1].length - 1; j++) {
                for (int k = 0; k < numCont; k++) {
                    // Left triangle:
                    i0 = i;
                    j0 = j;
                    i1 = i;
                    j1 = j + 1;
                    i2 = i + 1;
                    j2 = j + 1;
                    if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {

                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X, (1 - zratio)
                                * pts[i0][j0].Y + zratio * pts[i1][j1].Y,
                                zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3((1 - zratio)
                                * pts[i1][j1].X + zratio * pts[i2][j2].X,
                                pts[i1][j1].Y, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y,
                                pta[1].X, pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[0] = new Point3((1 - zratio)
                                * pts[i0][j0].X + zratio * pts[i2][j2].X,
                                (1 - zratio) * pts[i0][j0].Y
                                + zratio * pts[i2][j2].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3((1 - zratio)
                                * pts[i1][j1].X + zratio * pts[i2][j2].X,
                                pts[i1][j1].Y, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X, (1 - zratio)
                                * pts[i0][j0].Y + zratio * pts[i1][j1].Y,
                                zmin, 1);
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[1] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    }
                    // right triangle:
                    i0 = i;
                    j0 = j;
                    i1 = i + 1;
                    j1 = j;
                    i2 = i + 1;
                    j2 = j + 1;
                    if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i1][j1].X * zratio,
                                pts[i0][j0].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3(pts[i1][j1].X, pts[i1][j1].Y
                                * (1 - zratio) + pts[i2][j2].Y * zratio,
                                zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)
                            && (zlevels[k] >= pts[i1][j1].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i1][j1].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        zratio = (zlevels[k] - pts[i1][j1].Z)
                                / (pts[i2][j2].Z - pts[i1][j1].Z);
                        pta[1] = new Point3(pts[i1][j1].X,
                                pts[i1][j1].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
                    } else if ((zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i1][j1].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i1][j1].Z)
                            && (zlevels[k] >= pts[i0][j0].Z && zlevels[k]
                            < pts[i2][j2].Z || zlevels[k] < pts[i0][j0].Z
                            && zlevels[k] >= pts[i2][j2].Z)) {
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i1][j1].Z - pts[i0][j0].Z);
                        pta[0] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i1][j1].X * zratio,
                                pts[i0][j0].Y, zmin, 1);
                        zratio = (zlevels[k] - pts[i0][j0].Z)
                                / (pts[i2][j2].Z - pts[i0][j0].Z);
                        pta[1] = new Point3(pts[i0][j0].X
                                * (1 - zratio) + pts[i2][j2].X * zratio,
                                pts[i0][j0].Y * (1 - zratio)
                                + pts[i2][j2].Y * zratio, zmin, 1);
                        pta[0].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        pta[1].Transform(m, isBar,
                                xmin, xmax,
                                ymin, ymax,
                                zmin, zmax,
                                width, height);
                        gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X,
                                pta[1].Y);
                    }
                }
            }
        }
    }

    private void AddBar3D() {
        double xsp = ds.getXSpacing();
        double ysp = ds.getYSpacing();
        double zorigin = ds.getBarStyle().getZOrigin();
        // Check parameters:
        double xlength = ds.getBarStyle().getXLength();
        if (xlength <= 0) {
            xlength = 0.1 * xsp;
        } else if (xlength > 0.5) {
            xlength = 0.5 * xsp;
        } else {
            xlength = xlength * xsp;
        }
        double ylength = ds.getBarStyle().getYLength();
        if (ylength <= 0) {
            ylength = 0.1 * ysp;
        } else if (ylength > 0.5) {
            ylength = 0.5 * ysp;
        } else {
            ylength = ylength * ysp;
        }
        // Draw 3D bars:
        for (int i = 0; i < row - 1; i++) {
            for (int j = 0; j < col - 1; j++) {
                int ii = i;
                int jj = j;
                if (azimVal >= -180 && azimVal < -90) {
                    ii = row - 2 - i;
                    jj = j;
                } else if (azimVal >= -90 && azimVal < 0) {
                    ii = row - 2 - i;
                    jj = col - 2 - j;
                } else if (azimVal >= 0 && azimVal < 90) {
                    ii = i;
                    jj = col - 2 - j;
                } else if (azimVal >= 90 && azimVal <= 180) {
                    ii = i;
                    jj = j;
                }
                DrawBar(m, pts[ii][jj], xlength, ylength,
                        zorigin, zmax, zmin);
            }
        }
    }

    private void DrawBar(Matrix3 m, Point3 pt, double xlength, double ylength,
            double zorign, double Zmax, double Zmin) {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);
        Color colorFill = AddColor(pt, Zmin, Zmax);

        Point3[] ptsL = new Point3[8];
        Point3[] pts1L = new Point3[8];
        Point3[] pt3 = new Point3[4];
        double[] xS;
        double[] yS;
        ptsL[0] = new Point3(pt.X - xlength, pt.Y - ylength, zorign, 1);
        ptsL[1] = new Point3(pt.X - xlength, pt.Y + ylength, zorign, 1);
        ptsL[2] = new Point3(pt.X + xlength, pt.Y + ylength, zorign, 1);
        ptsL[3] = new Point3(pt.X + xlength, pt.Y - ylength, zorign, 1);
        ptsL[4] = new Point3(pt.X + xlength, pt.Y - ylength, pt.Z, 1);
        ptsL[5] = new Point3(pt.X + xlength, pt.Y + ylength, pt.Z, 1);
        ptsL[6] = new Point3(pt.X - xlength, pt.Y + ylength, pt.Z, 1);
        ptsL[7] = new Point3(pt.X - xlength, pt.Y - ylength, pt.Z, 1);
        for (int i = 0; i < ptsL.length; i++) {
            pts1L[i] = new Point3(ptsL[i].X, ptsL[i].Y, ptsL[i].Z, 1);
            ptsL[i].Transform(m, isBar,
                    xmin, xmax,
                    ymin, ymax,
                    zmin, zmax,
                    width, height);
        }
        int[] nconfigs = new int[8];
        if (isBarSingleColor) {
            xS = new double[]{ptsL[4].X, ptsL[5].X, ptsL[6].X, ptsL[7].X};
            yS = new double[]{ptsL[4].Y, ptsL[5].Y, ptsL[6].Y, ptsL[7].Y};
            int nn = 4;
            gc.setFill(colorFill);
            gc.fillPolygon(xS, yS, nn);
            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);
            xS = new double[]{ptsL[0].X, ptsL[1].X, ptsL[2].X, ptsL[3].X};
            yS = new double[]{ptsL[0].Y, ptsL[1].Y, ptsL[2].Y, ptsL[3].Y};

            gc.setFill(colorFill);
            gc.fillPolygon(xS, yS, nn);
            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);

            if (azimVal >= -180 && azimVal < -90) {
                nconfigs = new int[]{1, 2, 5, 6, 1, 0, 7, 6};
            } else if (azimVal >= -90 && azimVal < 0) {
                nconfigs = new int[]{1, 0, 7, 6, 0, 3, 4, 7};
            } else if (azimVal >= 0 && azimVal < 90) {
                nconfigs = new int[]{0, 3, 4, 7, 2, 3, 4, 5};
            } else if (azimVal >= 90 && azimVal <= 180) {
                nconfigs = new int[]{2, 3, 4, 5, 1, 2, 5, 6};
            }
            xS = new double[]{ptsL[nconfigs[0]].X, ptsL[nconfigs[1]].X,
                ptsL[nconfigs[2]].X, ptsL[nconfigs[3]].X};
            yS = new double[]{ptsL[nconfigs[0]].Y, ptsL[nconfigs[1]].Y,
                ptsL[nconfigs[2]].Y, ptsL[nconfigs[3]].Y};

            gc.setFill(colorFill);
            gc.fillPolygon(xS, yS, nn);
            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);
            xS = new double[]{ptsL[nconfigs[4]].X, ptsL[nconfigs[5]].X,
                ptsL[nconfigs[6]].X, ptsL[nconfigs[7]].X};
            yS = new double[]{ptsL[nconfigs[4]].Y, ptsL[nconfigs[5]].Y,
                ptsL[nconfigs[6]].Y, ptsL[nconfigs[7]].Y};

            gc.setFill(colorFill);
            gc.fillPolygon(xS, yS, nn);
            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);
        } else if (!isBarSingleColor && isColorMap) {
            xS = new double[]{ptsL[4].X, ptsL[5].X, ptsL[6].X, ptsL[7].X};
            yS = new double[]{ptsL[4].Y, ptsL[5].Y, ptsL[6].Y, ptsL[7].Y};
            int nn = 4;
            gc.setFill(colorFill);
            gc.fillPolygon(xS, yS, nn);
            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);

            xS = new double[]{ptsL[0].X, ptsL[1].X, ptsL[2].X, ptsL[3].X};
            yS = new double[]{ptsL[0].Y, ptsL[1].Y, ptsL[2].Y, ptsL[3].Y};
            colorFill = AddColor(ptsL[0], Zmin, Zmax);
            gc.setFill(colorFill);
            gc.fillPolygon(xS, yS, nn);
            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);

            double dz = (Zmax - Zmin) / 63;
            if (pt.Z < zorign) {
                dz = -dz;
            }
            int nz = (int) ((pt.Z - zorign) / dz) + 1;
            if (nz < 1) {
                nz = 1;
            }
            double z;// = zorign;
            if (azimVal >= -180 && azimVal < -90) {
                nconfigs = new int[]{1, 2, 1, 0};
            } else if (azimVal >= -90 && azimVal < 0) {
                nconfigs = new int[]{1, 0, 0, 3};
            } else if (azimVal >= 0 && azimVal < 90) {
                nconfigs = new int[]{0, 3, 2, 3};
            } else if (azimVal >= 90 && azimVal <= 180) {
                nconfigs = new int[]{2, 3, 1, 2};
            }
            for (int i = 0; i < nz; i++) {
                z = zorign + i * dz;
                pt3[0] = new Point3(pts1L[nconfigs[0]].X,
                        pts1L[nconfigs[0]].Y, z, 1);
                pt3[1] = new Point3(pts1L[nconfigs[1]].X,
                        pts1L[nconfigs[1]].Y, z, 1);
                pt3[2] = new Point3(pts1L[nconfigs[1]].X,
                        pts1L[nconfigs[1]].Y, z + dz, 1);
                pt3[3] = new Point3(pts1L[nconfigs[0]].X,
                        pts1L[nconfigs[0]].Y, z + dz, 1);
                for (Point3 pt31 : pt3) {
                    pt31.Transform(m, isBar,
                            xmin, xmax,
                            ymin, ymax,
                            zmin, zmax,
                            width, height);
                }
                xS = new double[]{pt3[0].X, pt3[1].X, pt3[2].X, pt3[3].X};
                yS = new double[]{pt3[0].Y, pt3[1].Y, pt3[2].Y, pt3[3].Y};

                colorFill = AddColor(pt3[0], Zmin, Zmax);
                gc.setFill(colorFill);
                gc.fillPolygon(xS, yS, nn);

            }
            pt3[0] = new Point3(pts1L[nconfigs[0]].X, pts1L[nconfigs[0]].Y, zorign, 1);
            pt3[1] = new Point3(pts1L[nconfigs[1]].X, pts1L[nconfigs[1]].Y, zorign, 1);
            pt3[2] = new Point3(pts1L[nconfigs[1]].X, pts1L[nconfigs[1]].Y, pt.Z, 1);
            pt3[3] = new Point3(pts1L[nconfigs[0]].X, pts1L[nconfigs[0]].Y, pt.Z, 1);
            for (Point3 pt31 : pt3) {
                pt31.Transform(m, isBar,
                        xmin, xmax,
                        ymin, ymax,
                        zmin, zmax,
                        width, height);
            }
            xS = new double[]{pt3[0].X, pt3[1].X, pt3[2].X, pt3[3].X};
            yS = new double[]{pt3[0].Y, pt3[1].Y, pt3[2].Y, pt3[3].Y};

            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);
            for (int i = 0; i < nz; i++) {
                z = zorign + i * dz;
                pt3[0] = new Point3(pts1L[nconfigs[2]].X,
                        pts1L[nconfigs[2]].Y, z, 1);
                pt3[1] = new Point3(pts1L[nconfigs[3]].X,
                        pts1L[nconfigs[3]].Y, z, 1);
                pt3[2] = new Point3(pts1L[nconfigs[3]].X,
                        pts1L[nconfigs[3]].Y, z + dz, 1);
                pt3[3] = new Point3(pts1L[nconfigs[2]].X,
                        pts1L[nconfigs[2]].Y, z + dz, 1);
                for (Point3 pt31 : pt3) {
                    pt31.Transform(m, isBar,
                            xmin, xmax,
                            ymin, ymax,
                            zmin, zmax,
                            width, height);
                }
                xS = new double[]{pt3[0].X, pt3[1].X, pt3[2].X, pt3[3].X};
                yS = new double[]{pt3[0].Y, pt3[1].Y, pt3[2].Y, pt3[3].Y};

                colorFill = AddColor(pt3[0], Zmin, Zmax);
                gc.setFill(colorFill);
                gc.fillPolygon(xS, yS, nn);

            }

            pt3[0] = new Point3(pts1L[nconfigs[2]].X,
                    pts1L[nconfigs[2]].Y, zorign, 1);
            pt3[1] = new Point3(pts1L[nconfigs[3]].X,
                    pts1L[nconfigs[3]].Y, zorign, 1);
            pt3[2] = new Point3(pts1L[nconfigs[3]].X,
                    pts1L[nconfigs[3]].Y, pt.Z, 1);
            pt3[3] = new Point3(pts1L[nconfigs[2]].X,
                    pts1L[nconfigs[2]].Y, pt.Z, 1);
            for (Point3 pt31 : pt3) {
                pt31.Transform(m, isBar,
                        xmin, xmax,
                        ymin, ymax,
                        zmin, zmax,
                        width, height);
            }
            xS = new double[]{pt3[0].X, pt3[1].X, pt3[2].X, pt3[3].X};
            yS = new double[]{pt3[0].Y, pt3[1].Y, pt3[2].Y, pt3[3].Y};
            gc.setStroke(lineColor);
            gc.strokePolygon(xS, yS, nn);
        }
    }

    private void AddSlice() {
        gc.setStroke(lineColor);
        gc.setLineWidth(lineThickness);
        gc.setLineDashes(dashL);
        Color colorFill;

        Point4[][][] pts4 = ds.getPoint4Array();
        Point2D[] pta = new Point2D[4];
        double vmin = ds.getVDataMin();
        double vmax = ds.getVDataMax();
        // Perform transformation on points:
        int rows = pts4.length;
        int cols = pts4[0].length;
        int zth = pts4[1].length;
        for (Point4[][] pt : pts4) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < zth; k++) {
                    pt[j][k].point3.Transform(m, isBar,
                            xmin, xmax,
                            ymin, ymax,
                            zmin, zmax,
                            width, height);
                    pt[j][k].point3.Z = pt[j][k].V;
                }
            }
        }
        if (null != xyzSlice)// Select slice:
        {
            switch (xyzSlice) {
                case XSlice: {
                    // Examine the imputer parameters:
                    if (sliceLocation < xmin) {
                        sliceLocation = xmin;
                    } else if (sliceLocation > xmax) {
                        sliceLocation = xmax;
                    }
                    int nfix = (int) ((sliceLocation - xmin)
                            / ds.getXSpacing()) + 1;
                    for (int j = 0; j < cols - 1; j++) {
                        for (int k = 0; k < zth - 1; k++) {
                            pta[0] = new Point2D(pts4[nfix][j][k].point3.X,
                                    pts4[nfix][j][k].point3.Y);
                            pta[1] = new Point2D(pts4[nfix][j + 1][k].point3.X,
                                    pts4[nfix][j + 1][k].point3.Y);
                            pta[2] = new Point2D(pts4[nfix][j + 1][k + 1].point3.X,
                                    pts4[nfix][j + 1][k + 1].point3.Y);
                            pta[3] = new Point2D(pts4[nfix][j][k + 1].point3.X,
                                    pts4[nfix][j][k + 1].point3.Y);
                            colorFill = AddColor(pts4[nfix][j][k].point3,
                                    vmin, vmax);

                            double[] xS = new double[]{pta[0].getX(), pta[1].getX(),
                                pta[2].getX(), pta[3].getX()};
                            double[] yS = new double[]{pta[0].getY(), pta[1].getY(),
                                pta[2].getY(), pta[3].getY()};

                            gc.setFill(colorFill);
                            gc.fillPolygon(xS, yS, 4); //pta.length = 4;

                            if (isLineVis) {
                                gc.setStroke(lineColor);
                                gc.strokePolygon(xS, yS, 4); //pta.length = 4
                            }
                        }
                    }
                    break;
                }
                case YSlice: {
                    if (sliceLocation < ymin) {
                        sliceLocation = ymin;
                    } else if (sliceLocation > ymax) {
                        sliceLocation = ymax;
                    }
                    int nfix = (int) ((sliceLocation - ymin)
                            / ds.getYSpacing()) + 1;
                    for (int i = 0; i < rows - 1; i++) {
                        for (int k = 0; k < zth - 1; k++) {
                            pta[0] = new Point2D(pts4[i][nfix][k].point3.X,
                                    pts4[i][nfix][k].point3.Y);
                            pta[1] = new Point2D(pts4[i + 1][nfix][k].point3.X,
                                    pts4[i + 1][nfix][k].point3.Y);
                            pta[2] = new Point2D(pts4[i + 1][nfix][k + 1].point3.X,
                                    pts4[i + 1][nfix][k + 1].point3.Y);
                            pta[3] = new Point2D(pts4[i][nfix][k + 1].point3.X,
                                    pts4[i][nfix][k + 1].point3.Y);
                            colorFill = AddColor(pts4[i][nfix][k].point3,
                                    vmin, vmax);

                            double[] xS = new double[]{pta[0].getX(), pta[1].getX(),
                                pta[2].getX(), pta[3].getX()};
                            double[] yS = new double[]{pta[0].getY(), pta[1].getY(),
                                pta[2].getY(), pta[3].getY()};
                            gc.setFill(colorFill);
                            gc.fillPolygon(xS, yS, 4); //pta.length = 4;

                            if (ds.getLineStyle().getIsVisible()) {
                                gc.setStroke(lineColor);
                                gc.strokePolygon(xS, yS, 4); //pta.length = 4
                            }
                        }
                    }
                    break;
                }
                case ZSlice: {
                    if (sliceLocation < zmin) {
                        sliceLocation = zmin;
                    } else if (sliceLocation > zmax) {
                        sliceLocation = zmax;
                    }
                    int nfix = (int) ((sliceLocation - zmin)
                            / ds.getZSpacing()) + 1;
                    for (int i = 0; i < rows - 1; i++) {
                        for (int j = 0; j < cols - 1; j++) {
                            pta[0] = new Point2D(pts4[i][j][nfix].point3.X,
                                    pts4[i][j][nfix].point3.Y);
                            pta[1] = new Point2D(pts4[i + 1][j][nfix].point3.X,
                                    pts4[i + 1][j][nfix].point3.Y);
                            pta[2] = new Point2D(pts4[i + 1][j + 1][nfix].point3.X,
                                    pts4[i + 1][j + 1][nfix].point3.Y);
                            pta[3] = new Point2D(pts4[i][j + 1][nfix].point3.X,
                                    pts4[i][j + 1][nfix].point3.Y);
                            colorFill = AddColor(pts4[i][j][nfix].point3,
                                    vmin, vmax);

                            double[] xS = new double[]{pta[0].getX(), pta[1].getX(),
                                pta[2].getX(), pta[3].getX()};
                            double[] yS = new double[]{pta[0].getY(), pta[1].getY(),
                                pta[2].getY(), pta[3].getY()};
                            gc.setFill(colorFill);
                            gc.fillPolygon(xS, yS, 4); //pta.length = 4

                            if (isLineVis) {
                                gc.setStroke(lineColor);
                                gc.strokePolygon(xS, yS, 4); //pta.length = 4
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    // GETTER AND SETTER
    public double getSliceLocation() {
        return sliceLocation;
    }

    public void setSliceLocation(double value) {
        sliceLocation = value;
    }

    public boolean getIsBarSingleColor() {
        return isBarSingleColor;
    }

    public void setIsBarSingleColor(boolean value) {
        isBarSingleColor = value;
    }

    public int getNumberContours() {
        return numberContours;
    }

    public void setNumberContours(int value) {
        numberContours = value;
    }

    public int getNumberInterp() {
        return numberInterp;
    }

    public void setNumberInterp(int value) {
        numberInterp = value;
    }

    public boolean getIsInterp() {
        return isInterp;
    }

    public void setIsInterp(boolean value) {
        isInterp = value;
    }

    public boolean getIsColorMap() {
        return isColorMap;
    }

    public void setIsColorMap(boolean value) {
        isColorMap = value;
    }

    public boolean getIsHiddenLine() {
        return isHiddenLine;
    }

    public void setIsHiddenLine(boolean value) {
        isHiddenLine = value;
    }

    public int[][] getCMap() {
        return cMap;
    }

    public void setCMap(int[][] value) {
        cMap = value;
    }

    public ChartTypeEnum getChartType() {
        return chartType;
    }

    public void setChartType(ChartTypeEnum value) {
        chartType = value;
    }

    public SliceEnum getXYZSlice() {
        return xyzSlice;
    }

    public void setXYZSlice(SliceEnum value) {
        xyzSlice = value;
    }

    public enum SliceEnum {
        XSlice,
        YSlice,
        ZSlice
    }

    public enum ChartTypeEnum {
        Line,
        Mesh,
        MeshZ,
        Waterfall,
        Surface,
        XYColor,
        Contour,
        FillContour,
        MeshContour,
        SurfaceContour,
        Slice,
        Bar3D,
    }
    // end of main class

}
