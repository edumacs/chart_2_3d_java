package com.edumacs.chart.base;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author moham
 */
public class ChartStyle {

    private Rectangle chartArea;
    private Rectangle plotArea;
    private double leftmargin, rightmargin, topmargin, bottommargin;
    private Color chartBackColor;
    private Color chartBorderColor = Color.BLACK;
    private Color plotBackColor;
    private Color plotBorderColor = Color.BLACK;
    private Color gridColor = Color.GRAY;
    private double gridLineThickness = 1;
    private boolean isXGrid = true;
    private boolean isYGrid = true;
    private String xLabel = "X Axis";
    private String yLabel = "Y Axis";
    private String sTitle = "Title";
    private Font labelFont = new Font("Arial", 10);
    private Color labelFontColor = Color.BLACK;
    private Font titleFont = new Font("Arial", 12);
    private Color titleFontColor = Color.BLACK;
    private double xTick = 1;
    private double yTick = 0.5;
    private Font tickFont;
    private Color tickFontColor = Color.BLACK;
    private double xLimMin = 0;
    private double xLimMax = 10;
    private double yLimMin = 0;
    private double yLimMax = 10;
    private boolean xDecimal = true;
    private boolean yDecimal = true;
    private double aspectRatio = 1;
    private double xScale, yScale;

    public ChartStyle() {
        chartArea = new Rectangle();
        chartBackColor = Color.WHITE;
        plotBackColor = Color.WHITE;
        tickFont = Font.getDefault();
        leftmargin = (new Text("" + xLimMax)).getLayoutBounds().getWidth() + 40
                + labelFont.getSize();
        rightmargin = 0;
        topmargin = 0;
        bottommargin = 0;

    }

    public void AddChartStyle(GraphicsContext gc) {
        Point2D pt1, pt0;
        double chartAreaWidth = chartArea.getWidth();
        double chartAreaHeight = chartArea.getHeight();
        double plotAreaWidth = chartAreaWidth - (leftmargin + rightmargin);
        double plotAreaHeight = chartAreaHeight - (topmargin + bottommargin);
        double xStart = chartArea.getX() + leftmargin;
        double yStart = chartArea.getY() + topmargin;
        xScale = plotAreaWidth / (xLimMax - xLimMin);
        aspectRatio = plotAreaHeight * (xLimMax - xLimMin)
                / ((yLimMax - yLimMin) * plotAreaWidth);
        /* if (plotAreaHeight < xScale * aspectRatio * (yLimMax - yLimMin)) {
            aspectRatio = plotAreaHeight * (xLimMax - xLimMin)
                    / ((yLimMax - yLimMin) * plotAreaWidth);
            JOptionPane.showMessageDialog(null, "Chart height too big ,"
                    + "aspect ratio was set to:" + aspectRatio + "!");
        }*/
        yScale = aspectRatio * xScale;
        plotAreaHeight = yScale * (yLimMax - yLimMin);
        // set PLOT AREA
        plotArea = new Rectangle(xStart, yStart, plotAreaWidth, plotAreaHeight);
        gc.setStroke(chartBorderColor);
        gc.strokeRect(chartArea.getX(), chartArea.getY(),
                chartArea.getWidth(), chartArea.getHeight());
        gc.setStroke(plotBorderColor);
        gc.strokeRect(plotArea.getX(), plotArea.getY(),
                plotArea.getWidth(), plotArea.getHeight());
        // vertical ticks
        double fX, fY;
        gc.setStroke(gridColor);
        gc.setLineWidth(gridLineThickness);

        if (isYGrid == true) {
            for (fX = xLimMin + xTick; fX < xLimMax; fX += xTick) {
                pt0 = Point(new Point2D(fX, yLimMin));
                pt1 = Point(new Point2D(fX, yLimMax));
                gc.strokeLine(pt0.getX(), pt0.getY(), pt1.getX(), pt1.getY());
            }
        }
        //horizontal ticks
        if (isXGrid == true) {
            for (fY = yLimMin + yTick; fY < yLimMax; fY += yTick) {
                pt0 = Point(new Point2D(xLimMin, fY));
                pt1 = Point(new Point2D(xLimMax, fY));
                gc.strokeLine(pt0.getX(), pt0.getY(), pt1.getX(), pt1.getY());
            }
        }
        // X axis's tickmarks
        Point2D yAxisPoint, xAxisPoint;
        double fontHight = tickFont.getSize();
        double fontWidth;
        double xx, yy;
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);
        for (fX = xLimMin; fX <= xLimMax; fX += xTick) {
            fontWidth = (new Text("" + fX)).getLayoutBounds().getWidth();
            yAxisPoint = Point(new Point2D(fX, yLimMin));
            xx = yAxisPoint.getX();
            yy = yAxisPoint.getY();
            gc.strokeLine(xx, yy, xx, yy + 2f);
            if (xDecimal) {
                gc.fillText("" + fX, xx - fontWidth / 2, yy + 2f + fontHight);
            } else if (!xDecimal) {
                gc.fillText("" + (int) fX, xx - fontWidth / 2, yy + 2f + fontHight);
            }
        }
        // Y axis's tickmarks
        for (fY = yLimMin; fY <= yLimMax; fY += yTick) {
            fontWidth = (new Text("" + fY)).getLayoutBounds().getWidth() + 5f;
            xAxisPoint = Point(new Point2D(xLimMin, fY));
            xx = xAxisPoint.getX();
            yy = xAxisPoint.getY();
            gc.strokeLine(xx, xAxisPoint.getY(), xx - 2f, yy);
            if (yDecimal) {
                gc.fillText("" + fY, xx - fontWidth, yy - 2f + fontHight / 2);
            } else if (!yDecimal) {
                gc.fillText("" + (int) fY, xx - fontWidth, yy - 2f + fontHight / 2);
            }

        }
        // Add X labels
        fontHight = labelFont.getSize();
        fontWidth = (new Text(xLabel)).getLayoutBounds().getWidth();
        pt0 = Point(new Point2D((xLimMax + xLimMin) / 2, yLimMin));
        gc.fillText(xLabel, pt0.getX() - fontWidth / 2, pt0.getY() + 4 * fontHight);
        // Add Y labels
        fontWidth = (new Text("" + xLimMax)).getLayoutBounds().getWidth();
        pt0 = Point(new Point2D(xLimMin, (yLimMax + yLimMin) / 2));
        gc.save();
        gc.translate(pt0.getX() - 2 * fontWidth + 10, pt0.getY() + fontHight);
        gc.rotate(-90);
        gc.fillText(yLabel, 0, 0);
        gc.restore();
        //Add title
        fontHight = titleFont.getSize();
        fontWidth = (new Text(sTitle)).getLayoutBounds().getWidth();
        pt0 = Point(new Point2D((xLimMax + xLimMin) / 2, yLimMax));
        if (sTitle != null) {
            sTitle = sTitle.toUpperCase();
            gc.fillText(sTitle, pt0.getX() - fontWidth / 2, pt0.getY() - 2 * fontHight);
        }
    }

    //point transformation
    public Point2D Point(Point2D pt) {
        Point2D pnt;
        double x = pt.getX();
        double y = pt.getY();
        double X, Y;

        if (x < xLimMin || x > xLimMax || y < yLimMin || y > yLimMax) {
            pnt = new Point2D(Double.NaN, Double.NaN);
        } else {
            X = leftmargin + (x - xLimMin) * xScale;
            Y = topmargin + yScale * (yLimMax - yLimMin) - (y - yLimMin) * yScale;
            pnt = new Point2D(X, Y);
        }
        return pnt;
    }

    //--------- get and set function -------------//
    public Rectangle getChartArea() {
        return chartArea;
    }

    public void setChartArea(Rectangle rect) {
        chartArea = rect;
    }

    public Rectangle getPlotArea() {
        return plotArea;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double val) {
        aspectRatio = val;
    }

    public double getLeftMargin() {
        return leftmargin;
    }

    public void setLeftMargin(double val) {
        leftmargin = val;
    }

    public double getRightMargin() {
        return rightmargin;
    }

    public void setRightMargin(double val) {
        rightmargin = val;
    }

    public double getTopMargin() {
        return topmargin;
    }

    public void setTopMargin(double val) {
        topmargin = val;
    }

    public double getBottomMargin() {
        return bottommargin;
    }

    public void setBottomMargin(double val) {
        bottommargin = val;
    }

    public Color getChartBackColor() {
        return chartBackColor;
    }

    public void setChartBackColor(Color val) {
        chartBackColor = val;
    }

    public Color getChartBorderColor() {
        return chartBorderColor;
    }

    public void setChartBorderColor(Color val) {
        chartBorderColor = val;
    }

    public Color getPlotBorderColor() {
        return plotBorderColor;
    }

    public void setPlotBorderColor(Color val) {
        plotBorderColor = val;
    }

    public Color getPlotBackColor() {
        return plotBackColor;
    }

    public void setPlotBackColor(Color val) {
        plotBackColor = val;
    }

    public double getXLimMin() {
        return xLimMin;
    }

    public void setXLimMin(double x) {
        xLimMin = x;
    }

    public double getXLimMax() {
        return xLimMax;
    }

    public void setXLimMax(double y) {
        xLimMax = y;
    }

    public double getYLimMin() {
        return yLimMin;
    }

    public void setYLimMin(double y) {
        yLimMin = y;
    }

    public double getYLimMax() {
        return yLimMax;
    }

    public void setYLimMax(double y) {
        yLimMax = y;
    }
//----------

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color clr) {
        gridColor = clr;
    }

    public double getGridLineThickness() {
        return gridLineThickness;
    }

    public void setGridLineThickness(double th) {
        gridLineThickness = th;
    }

    public boolean getIsXGrid() {
        return isXGrid;
    }

    public void setIsXGrid(boolean bl) {
        isXGrid = bl;
    }

    public boolean getIsYGrid() {
        return isYGrid;
    }

    public void setIsYGrid(boolean bl) {
        isYGrid = bl;
    }

    public String getXLabel() {
        return xLabel;
    }

    public void setXLabel(String lbl) {
        xLabel = lbl;
    }

    public String getYLabel() {
        return yLabel;
    }

    public void setYLabel(String lbl) {
        yLabel = lbl;
    }

    public String getTitle() {
        return sTitle;
    }

    public void setTitle(String ttl) {
        sTitle = ttl;
    }

    public Font getLabelFont() {
        return labelFont;
    }

    public void setLabelFont(Font fnt) {
        labelFont = fnt;
    }

    public Color getLabelFontColor() {
        return labelFontColor;
    }

    public void setLabelFontColor(Color clr) {
        labelFontColor = clr;
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font fnt) {
        titleFont = fnt;
    }

    public Color getTitleFontColor() {
        return titleFontColor;
    }

    public void setTitleFontColor(Color clr) {
        titleFontColor = clr;
    }

    public double getXTick() {
        return xTick;
    }

    public void setXTick(double tc) {
        xTick = tc;
    }

    public double getYTick() {
        return yTick;
    }

    public void setYTick(double tc) {
        yTick = tc;
    }

    public Font getTickFont() {
        return tickFont;
    }

    public void setTickFont(Font fnt) {
        tickFont = fnt;
    }

    public Color getTickFontColor() {
        return tickFontColor;
    }

    public void setTickFontColor(Color clr) {
        tickFontColor = clr;
    }

    public void setXValueDecimal(boolean inp) {
        this.xDecimal = inp;
    }

    public void setYValueDecimal(boolean inp) {
        this.yDecimal = inp;
    }
}
