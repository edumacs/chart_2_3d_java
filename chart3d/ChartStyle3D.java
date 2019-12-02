package com.edumacs.chart.chart3d;

import com.edumacs.chart.base.FontStyle;
import com.edumacs.chart.base.LineStyle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * This class is for making Axes
 *
 * @author mohammadfaizun
 */
public class ChartStyle3D {

    private double xMax = 5.0;
    private double xMin = -5.0;
    private double yMax = 3.0;
    private double yMin = -3.0;
    private double zMax = 6.0;
    private double zMin = -6.0;
    private double xTick = 1.0;
    private double yTick = 1.0;
    private double zTick = 3.0;
    private FontStyle tickFont = new FontStyle();
    private String title = "My 3D Chart";
    private FontStyle titleFont = new FontStyle();
    private String xLabel = "X Axis";
    private String yLabel = "Y Axis";
    private String zLabel = "Z Axis";
    private FontStyle labelFont = new FontStyle();
    private double elevation = 30;
    private double azimuth = -37.5;
    private boolean isXGrid = true;
    private boolean isYGrid = true;
    private boolean isZGrid = true;
    private LineStyle gridStyle = new LineStyle();
    private LineStyle axisStyle = new LineStyle();
    private boolean isColorBar = true;
    private final GraphicsContext gc;
    private final double width;
    private final double height;
    private Point3[] pts;
    private Matrix3 m;

    public ChartStyle3D(GraphicsContext gc) {
        this.gc = gc;
        this.width = gc.getCanvas().getWidth();
        this.height = gc.getCanvas().getHeight();
    }

    public void AddChartStyle() {
        pts = CoordinatesOfChartBox();
        m = Matrix3.AzimuthElevation(elevation, azimuth);
        AddAxes();
        AddTicks();
        AddGrids();
        AddLabels();
    }

    private Point3[] CoordinatesOfChartBox() {
        // Create coordinate of the axes: 
        Point3[] pta = new Point3[8];
        pta[0] = new Point3(xMax, yMin, zMin, 1);
        pta[1] = new Point3(xMin, yMin, zMin, 1);
        pta[2] = new Point3(xMin, yMax, zMin, 1);
        pta[3] = new Point3(xMin, yMax, zMax, 1);
        pta[4] = new Point3(xMin, yMin, zMax, 1);
        pta[5] = new Point3(xMax, yMin, zMax, 1);
        pta[6] = new Point3(xMax, yMax, zMax, 1);
        pta[7] = new Point3(xMax, yMax, zMin, 1);

        Point3[] pts_ = new Point3[4];
        int[] npts = new int[]{0, 1, 2, 3};

        if (elevation >= 0) {
            if (azimuth >= -180 && azimuth < -90) {
                npts = new int[]{1, 2, 7, 6};
            } else if (azimuth >= -90 && azimuth < 0) {
                npts = new int[]{0, 1, 2, 3};
            } else if (azimuth >= 0 && azimuth < 90) {
                npts = new int[]{7, 0, 1, 4};
            } else if (azimuth >= 90 && azimuth <= 180) {
                npts = new int[]{2, 7, 0, 5};
            }
        } else if (elevation < 0) {
            if (azimuth >= -180 && azimuth < -90) {
                npts = new int[]{1, 0, 7, 6};
            } else if (azimuth >= -90 && azimuth < 0) {
                npts = new int[]{0, 7, 2, 3};
            } else if (azimuth >= 0 && azimuth < 90) {
                npts = new int[]{7, 2, 1, 4};
            } else if (azimuth >= 90 && azimuth <= 180) {
                npts = new int[]{2, 1, 0, 5};
            }
        }
        for (int i = 0; i < 4; i++) {
            pts_[i] = pta[npts[i]];
        }
        return pts_;
    }

    // Add Axes
    private void AddAxes() {

        gc.setStroke(axisStyle.getLineColor());
        gc.setLineWidth(axisStyle.getThickness());
        gc.setLineDashes(axisStyle.getDashed());
        for (Point3 pt : pts) {
            pt.Transform(m, isColorBar,
                    xMin, xMax,
                    yMin, yMax,
                    zMin, zMax,
                    width, height);
        }
        gc.strokeLine(pts[0].X, pts[0].Y, pts[1].X, pts[1].Y);
        gc.strokeLine(pts[1].X, pts[1].Y, pts[2].X, pts[2].Y);
        gc.strokeLine(pts[2].X, pts[2].Y, pts[3].X, pts[3].Y);
    }

    private void AddTicks() {
        Point3[] pta = new Point3[2];
        gc.setStroke(axisStyle.getLineColor());
        gc.setLineWidth(axisStyle.getThickness());
        gc.setLineDashes(axisStyle.getDashed());
        // Add x ticks:
        double offset = (yMax - yMin) / 40.0;
        double ticklength = offset;
        for (double x = xMin; x <= xMax; x = x + xTick) {
            if (elevation >= 0) {
                if (azimuth >= -90 && azimuth < 90) {
                    ticklength = -offset;
                }
            } else if (elevation < 0) {
                if ((azimuth >= -180 && azimuth < -90) || azimuth >= 90 && azimuth <= 180) {
                    ticklength = -(yMax - yMin) / 30;
                }
            }
            pta[0] = new Point3(x, pts[1].Y + ticklength,
                    pts[1].Z, pts[1].W);
            pta[1] = new Point3(x, pts[1].Y, pts[1].Z, pts[1].W);
            for (Point3 pta1 : pta) {
                pta1.Transform(m, isColorBar,
                        xMin, xMax,
                        yMin, yMax,
                        zMin, zMax,
                        width, height);
            }
            gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
        }
        // Add y ticks:
        offset = (xMax - xMin) / 40.0;
        ticklength = offset;
        for (double y = yMin; y <= yMax; y = y + yTick) {
            pts = CoordinatesOfChartBox();
            if (elevation >= 0) {
                if (azimuth >= -180 && azimuth < 0) {
                    ticklength = -offset;
                }
            } else if (elevation < 0) {
                if (azimuth >= 0 && azimuth < 180) {
                    ticklength = -offset;
                }
            }
            pta[0] = new Point3(pts[1].X + ticklength, y,
                    pts[1].Z, pts[1].W);
            pta[1] = new Point3(pts[1].X, y, pts[1].Z, pts[1].W);
            for (Point3 pta1 : pta) {
                pta1.Transform(m, isColorBar,
                        xMin, xMax,
                        yMin, yMax,
                        zMin, zMax,
                        width, height);
            }
            gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
        }
        // Add z ticks:
        double xoffset = (xMax - xMin) / 40.0;
        double yoffset = (yMax - yMin) / 40.0;
        double xticklength = xoffset;
        double yticklength = yoffset;
        for (double z = zMin; z <= zMax; z = z + zTick) {
            if (elevation >= 0) {
                if (azimuth >= -180 && azimuth < -90) {
                    xticklength = 0;
                    yticklength = yoffset;
                } else if (azimuth >= -90 && azimuth < 0) {
                    xticklength = xoffset;
                    yticklength = 0;
                } else if (azimuth >= 0 && azimuth < 90) {
                    xticklength = 0;
                    yticklength = -yoffset;
                } else if (azimuth >= 90 && azimuth <= 180) {
                    xticklength = -xoffset;
                    yticklength = 0;
                }
            } else if (elevation < 0) {
                if (azimuth >= -180 && azimuth < -90) {
                    yticklength = 0;
                    xticklength = xoffset;
                } else if (azimuth >= -90 && azimuth < 0) {
                    yticklength = -yoffset;
                    xticklength = 0;
                } else if (azimuth >= 0 && azimuth < 90) {
                    yticklength = 0;
                    xticklength = -xoffset;
                } else if (azimuth >= 90 && azimuth <= 180) {
                    yticklength = yoffset;
                    xticklength = 0;
                }
            }
            pta[0] = new Point3(pts[2].X, pts[2].Y, z, pts[2].W);
            pta[1] = new Point3(pts[2].X + yticklength,
                    pts[2].Y + xticklength, z, pts[2].W);
            for (Point3 pta1 : pta) {
                pta1.Transform(m, isColorBar,
                        xMin, xMax,
                        yMin, yMax,
                        zMin, zMax,
                        width, height);
            }
            gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
        }
    }

    // Add GRIDS
    private void AddGrids() {
        Point3[] pta = new Point3[3];
        gc.setStroke(gridStyle.getLineColor());
        gc.setLineWidth(gridStyle.getThickness());
        gc.setLineDashes(gridStyle.getDashed());
        // Draw x gridlines: 
        if (isXGrid) {
            for (double x = xMin; x <= xMax; x = x + xTick) {
                pta[0] = new Point3(x, pts[1].Y, pts[1].Z, pts[1].W);
                if (elevation >= 0) {
                    if ((azimuth >= -180 && azimuth < -90) || (azimuth >= 0 && azimuth < 90)) {
                        pta[1] = new Point3(x, pts[0].Y, pts[1].Z, pts[1].W);

                        pta[2] = new Point3(x, pts[0].Y, pts[3].Z, pts[1].W);

                    } else {
                        pta[1] = new Point3(x, pts[2].Y, pts[1].Z, pts[1].W);
                        pta[2] = new Point3(x, pts[2].Y, pts[3].Z, pts[1].W);
                    }
                } else if (elevation < 0) {
                    if ((azimuth >= -180 && azimuth < -90) || (azimuth >= 0 && azimuth < 90)) {
                        pta[1] = new Point3(x, pts[2].Y, pts[1].Z, pts[1].W);
                        pta[2] = new Point3(x, pts[2].Y, pts[3].Z, pts[1].W);
                    } else {
                        pta[1] = new Point3(x, pts[0].Y, pts[1].Z, pts[1].W);
                        pta[2] = new Point3(x, pts[0].Y, pts[3].Z, pts[1].W);
                    }
                }
                for (Point3 pta1 : pta) {
                    pta1.Transform(m, isColorBar,
                            xMin, xMax,
                            yMin, yMax,
                            zMin, zMax,
                            width, height);
                }
                gc.strokeLine(pta[1].X, pta[1].Y, pta[2].X, pta[2].Y);
                gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);

            }
        }
        // Draw y gridlines: 
        if (isYGrid) {
            for (double y = yMin; y <= yMax; y = y + yTick) {
                pts = CoordinatesOfChartBox();
                pta[0] = new Point3(pts[1].X, y, pts[1].Z, pts[1].W);
                if (elevation >= 0) {
                    if ((azimuth >= -180 && azimuth < -90) || (azimuth >= 0 && azimuth < 90)) {
                        pta[1] = new Point3(pts[2].X, y, pts[1].Z, pts[1].W);
                        pta[2] = new Point3(pts[2].X, y, pts[3].Z, pts[1].W);
                    } else {
                        pta[1] = new Point3(pts[0].X, y, pts[1].Z, pts[1].W);
                        pta[2] = new Point3(pts[0].X, y, pts[3].Z, pts[1].W);
                    }
                } else if (elevation < 0) {
                    if ((azimuth >= -180 && azimuth < -90) || (azimuth >= 0 && azimuth < 90)) {
                        pta[1] = new Point3(pts[0].X, y, pts[1].Z, pts[1].W);
                        pta[2] = new Point3(pts[0].X, y, pts[3].Z, pts[1].W);

                    } else {
                        pta[1] = new Point3(pts[2].X, y, pts[1].Z, pts[1].W);
                        pta[2] = new Point3(pts[2].X, y, pts[3].Z, pts[1].W);
                    }
                }
                for (Point3 pta1 : pta) {
                    pta1.Transform(m, isColorBar,
                            xMin, xMax,
                            yMin, yMax,
                            zMin, zMax,
                            width, height);
                }
                gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
                gc.strokeLine(pta[1].X, pta[1].Y, pta[2].X, pta[2].Y);
            }
        }
        // Draw Z gridlines
        if (isZGrid) {
            for (double z = zMin; z <= zMax; z = z + zTick) {
                pts = CoordinatesOfChartBox();
                pta[0] = new Point3(pts[2].X, pts[2].Y, z, pts[2].W);
                if (elevation >= 0) {
                    if ((azimuth >= -180 && azimuth < -90) || (azimuth >= 0 && azimuth < 90)) {
                        pta[1] = new Point3(pts[2].X, pts[0].Y, z, pts[1].W);
                        pta[2] = new Point3(pts[0].X, pts[0].Y, z, pts[1].W);

                    } else {
                        pta[1] = new Point3(pts[0].X, pts[2].Y, z, pts[1].W);
                        pta[2] = new Point3(pts[0].X, pts[1].Y, z, pts[1].W);
                    }
                } else if (elevation < 0) {
                    if ((azimuth >= -180 && azimuth < -90) || (azimuth >= 0 && azimuth < 90)) {
                        pta[1] = new Point3(pts[0].X, pts[2].Y, z, pts[1].W);
                        pta[2] = new Point3(pts[0].X, pts[0].Y, z, pts[1].W);
                    } else {
                        pta[1] = new Point3(pts[2].X, pts[0].Y, z, pts[1].W);
                        pta[2] = new Point3(pts[0].X, pts[0].Y, z, pts[1].W);
                    }
                }
                for (Point3 pta1 : pta) {
                    pta1.Transform(m, isColorBar,
                            xMin, xMax,
                            yMin, yMax,
                            zMin, zMax,
                            width, height);
                }
                gc.strokeLine(pta[0].X, pta[0].Y, pta[1].X, pta[1].Y);
                gc.strokeLine(pta[1].X, pta[1].Y, pta[2].X, pta[2].Y);
            }
        }
    }

    // ADD LABELS Function
    private void AddLabels() {
        Point3 pt;
        gc.setFill(tickFont.getColor());
        gc.setFont(tickFont.getFont());
        // Add x tick labels:
        double offset = (yMax - yMin) / 10;
        double labelSpace = offset;
        for (double x = xMin + xTick; x < xMax; x = x + xTick) {
            if (elevation >= 0) {
                if (azimuth >= -90 && azimuth < 90) {
                    labelSpace = -offset;
                }
            } else if (elevation < 0) {
                if ((azimuth >= -180 && azimuth < -90) || azimuth >= 90 && azimuth <= 180) {
                    labelSpace = -offset;
                }
            }
            pt = new Point3(x, pts[1].Y + labelSpace, pts[1].Z, pts[1].W);
            pt.Transform(m, isColorBar,
                    xMin, xMax,
                    yMin, yMax,
                    zMin, zMax,
                    width, height);
            gc.fillText("" + x, pt.X, pt.Y);
        }
        // Add y tick labels:
        offset = (xMax - xMin) / 20;
        labelSpace = offset;
        for (double y = yMin + yTick; y < yMax; y = y + yTick) {
            pts = CoordinatesOfChartBox();
            if (elevation >= 0) {
                if (azimuth >= -180 && azimuth < 0) {
                    labelSpace = -offset;
                }
            } else if (elevation < 0) {
                if (azimuth >= 0 && azimuth < 180) {
                    labelSpace = -offset;
                }
            }
            pt = new Point3(pts[1].X + labelSpace, y, pts[1].Z, pts[1].W);
            pt.Transform(m, isColorBar,
                    xMin, xMax,
                    yMin, yMax,
                    zMin, zMax,
                    width, height);
            gc.fillText("" + y, pt.X, pt.Y);
        }
        // Add z tick labels:
        double xoffset = (xMax - xMin) / 15.0;
        double yoffset = (yMax - yMin) / 15.0;
        double xlabelSpace = xoffset;
        double ylabelSpace = yoffset;
        double s = (new Text("A")).getLayoutBounds().getHeight() / 5; // z clear

        for (double z = zMin; z <= zMax; z = z + zTick) {
            pts = CoordinatesOfChartBox();
            if (elevation >= 0) {
                if (azimuth >= -180 && azimuth < -90) {
                    xlabelSpace = 0;
                    ylabelSpace = yoffset;
                } else if (azimuth >= -90 && azimuth < 0) {
                    xlabelSpace = xoffset;
                    ylabelSpace = 0;
                } else if (azimuth >= 0 && azimuth < 90) {
                    xlabelSpace = 0;
                    ylabelSpace = -yoffset;
                } else if (azimuth >= 90 && azimuth <= 180) {
                    xlabelSpace = -xoffset;
                    ylabelSpace = 0;
                }
            } else if (elevation < 0) {
                if (azimuth >= -180 && azimuth < -90) {
                    ylabelSpace = 0;
                    xlabelSpace = xoffset;
                } else if (azimuth >= -90 && azimuth < 0) {
                    ylabelSpace = -yoffset;
                    xlabelSpace = 0;
                } else if (azimuth >= 0 && azimuth < 90) {
                    ylabelSpace = 0;
                    xlabelSpace = -xoffset;
                } else if (azimuth >= 90 && azimuth <= 180) {
                    ylabelSpace = yoffset;
                    xlabelSpace = 0;
                }
            }
            pt = new Point3(pts[2].X + ylabelSpace, pts[2].Y + xlabelSpace, z, pts[2].W);
            pt.Transform(m, isColorBar,
                    xMin, xMax,
                    yMin, yMax,
                    zMin, zMax,
                    width, height);
            gc.fillText("" + z, pt.X - labelSpace, pt.Y - s);
        }
        // Add Title:
        gc.setStroke(titleFont.getColor());
        gc.setFont(titleFont.getFont());
        if (!"No Title".equals(title)) {
            gc.fillText(title, gc.getCanvas().getWidth() / 2
                    - (new Text(title)).getLayoutBounds().getWidth() / 2,
                    gc.getCanvas().getHeight() / 20);
        }

        // Add x axis label:
        gc.setFont(labelFont.getFont());
        offset = (yMax - yMin) / 5;
        labelSpace = offset;
        gc.setStroke(labelFont.getColor());
        double offset1 = (xMax - xMin) / 20;
        double xc = offset1;
        if (elevation >= 0) {
            if (azimuth >= -90 && azimuth < 90) {
                labelSpace = -offset;
            }
            if (azimuth >= 0 && azimuth <= 180) {
                xc = -offset1;
            }
        } else if (elevation < 0) {
            if ((azimuth >= -180 && azimuth < -90) || azimuth >= 90 && azimuth <= 180) {
                labelSpace = -offset;
            }
            if (azimuth >= -180 && azimuth <= 0) {
                xc = -offset1;
            }
        }
        Point3[] pta = new Point3[2];
        pta[0] = new Point3(xMin, pts[1].Y + labelSpace, pts[1].Z, pts[1].W);
        pta[1] = new Point3((xMin + xMax) / 2 - xc, pts[1].Y + labelSpace, pts[1].Z, pts[1].W);
        pta[0].Transform(m, isColorBar,
                xMin, xMax,
                yMin, yMax,
                zMin, zMax,
                width, height);
        pta[1].Transform(m, isColorBar,
                xMin, xMax,
                yMin, yMax,
                zMin, zMax,
                width, height);
        double theta = Math.atan((pta[1].Y - pta[0].Y)
                / (pta[1].X - pta[0].X));
        theta = theta * 180 / Math.PI;
        gc.save();
        gc.translate(pta[1].X, pta[1].Y);
        gc.rotate(theta);
        gc.fillText(xLabel, 0, 0);
        gc.restore();

        // Add y axis label:
        offset = (xMax - xMin) / 5;
        offset1 = (yMax - yMin) / 20;
        labelSpace = offset;
        double yc = yTick;
        if (elevation >= 0) {
            if (azimuth >= -180 && azimuth < 0) {
                labelSpace = -offset;
            }
            if (azimuth >= -90 && azimuth <= 90) {
                yc = -offset1;
            }
        } else if (elevation < 0) {
            yc = -offset1;
            if (azimuth >= 0 && azimuth < 180) {
                labelSpace = -offset;
            }
            if (azimuth >= -90 && azimuth <= 90) {
                yc = offset1;
            }
        }
        pta[0] = new Point3(pts[1].X + labelSpace, yMin, pts[1].Z, pts[1].W);
        pta[1] = new Point3(pts[1].X + labelSpace,
                (yMin + yMax) / 2 + yc, pts[1].Z, pts[1].W);
        pta[0].Transform(m, isColorBar,
                xMin, xMax,
                yMin, yMax,
                zMin, zMax,
                width, height);
        pta[1].Transform(m, isColorBar,
                xMin, xMax,
                yMin, yMax,
                zMin, zMax,
                width, height);
        theta = Math.atan((pta[1].Y - pta[0].Y)
                / (pta[1].X - pta[0].X));
        theta = theta * 180 / Math.PI;
        gc.save();
        gc.translate(pta[1].X, pta[1].Y);
        gc.rotate(theta);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(yLabel, 0, 0);
        gc.restore();

        // Add z axis labels:
        double zticklength = 10.0;
        labelSpace = -1.3 * offset;
        offset1 = (zMax - zMin) / 10;
        double zc = -offset1;
        for (double z = zMin; z < zMax; z = z + zTick) {
            double size = (new Text("" + z)).getLayoutBounds().getWidth();
            if (zticklength < size) {
                zticklength = size;
            }
        }
        double zlength = -zticklength;
        if (elevation >= 0) {
            if (azimuth >= -180 && azimuth < -90) {
                zlength = -zticklength;
                labelSpace = -1.3 * offset;
                zc = -offset1;
            } else if (azimuth >= -90 && azimuth < 0) {
                zlength = zticklength;
                labelSpace = 2 * offset / 3;
                zc = offset1;
            } else if (azimuth >= 0 && azimuth < 90) {
                zlength = zticklength;
                labelSpace = 2 * offset / 3;
                zc = -offset1;
            } else if (azimuth >= 90 && azimuth <= 180) {
                zlength = -zticklength;
                labelSpace = -1.3 * offset;
                zc = offset1;
            }
        } else if (elevation < 0) {
            if (azimuth >= -180 && azimuth < -90) {
                zlength = -zticklength;
                labelSpace = -1.3 * offset;
                zc = offset1;
            } else if (azimuth >= -90 && azimuth < 0) {
                zlength = zticklength;
                labelSpace = 2 * offset / 3;
                zc = -offset1;
            } else if (azimuth >= 0 && azimuth < 90) {
                zlength = zticklength;
                labelSpace = 2 * offset / 3;
                zc = offset1;
            } else if (azimuth >= 90 && azimuth <= 180) {
                zlength = -zticklength;
                labelSpace = -1.3 * offset;
                zc = -offset1;
            }
        }
        pta[0] = new Point3(pts[2].X - labelSpace, pts[2].Y, (zMin + zMax) / 2 + zc, pts[2].W);
        pta[0].Transform(m, isColorBar,
                xMin, xMax,
                yMin, yMax,
                zMin, zMax,
                width, height);
        gc.save();
        gc.translate(pta[0].X - zlength, pta[0].Y);
        gc.rotate(270);
        gc.fillText(zLabel, 0, 0);
        gc.restore();
    }

    // ====== GET and SET functions ======
    public boolean getIsColorBar() {
        return isColorBar;
    }

    public void setIsColorBar(boolean value) {
        isColorBar = value;
    }

    public LineStyle getAxisStyle() {
        return axisStyle;
    }

    public void setAxisStyle(LineStyle lst) {
        axisStyle = lst;
    }

    public LineStyle getGridStyle() {
        return gridStyle;
    }

    public void setGridStyle(LineStyle lst) {
        gridStyle = lst;
    }

    public FontStyle getLabelFont() {
        return labelFont;
    }

    public void setLabelFont(FontStyle lbl) {
        labelFont = lbl;
    }

    public FontStyle getTitleFont() {
        return titleFont;
    }

    public void setTileFont(FontStyle fnt) {
        titleFont = fnt;
    }

    public FontStyle getTickFont() {
        return tickFont;
    }

    public void setTickFont(FontStyle fnt) {
        tickFont = fnt;
    }

    public boolean getIsXGrid() {
        return isXGrid;
    }

    public void setIsXGrid(boolean value) {
        isXGrid = value;
    }

    public boolean getIsYGrid() {
        return isYGrid;
    }

    public void setIsYGrid(boolean value) {
        isYGrid = value;
    }

    public boolean getIsZGrid() {
        return isZGrid;
    }

    public void setIsZGrid(boolean value) {
        isZGrid = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        title = value;
    }

    public String getXLabel() {
        return xLabel;
    }

    public void setXLabel(String value) {
        xLabel = value;
    }

    public String getYLabel() {
        return yLabel;
    }

    public void setYLabel(String value) {
        yLabel = value;
    }

    public String getZLabel() {
        return zLabel;
    }

    public void setZLabel(String value) {
        zLabel = value;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double value) {
        elevation = value;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double value) {
        azimuth = value;
    }

    public double getXMax() {
        return xMax;
    }

    public void setXMax(double value) {
        xMax = Math.round(value) + 1;
    }

    public double getXMin() {
        return xMin;
    }

    public void setXMin(double value) {
        xMin = Math.round(value) - 1;
    }

    public double getYMax() {
        return yMax;
    }

    public void setYMax(double value) {
        yMax = Math.round(value) + 1;
    }

    public double getYMin() {
        return yMin;
    }

    public void setYMin(double value) {
        yMin = Math.round(value) - 1;
    }

    public double getZMax() {
        return zMax;
    }

    public void setZMax(double value) {
        zMax = Math.round(value) + 1;
    }

    public double getZMin() {
        return zMin;
    }

    public void setZMin(double value) {
        zMin = Math.round(value) - 1;
    }

    public double getXTick() {
        return xTick;
    }

    public void setXTick(double value) {
        xTick = value;
    }

    public double getYTick() {
        return yTick;
    }

    public void setYTick(double value) {
        yTick = value;
    }

    public double getZTick() {
        return zTick;
    }

    public void setZTick(double value) {
        zTick = value;
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

}
