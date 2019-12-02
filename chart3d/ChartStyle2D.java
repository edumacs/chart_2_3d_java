package com.edumacs.chart.chart3d;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mohammadfaizun
 */
public class ChartStyle2D {

    private Rectangle chartArea;
    private Color chartBackColor;
    private Color chartBorderColor;
    private Color plotBackColor = Color.WHITE;
    private Color plotBorderColor = Color.BLACK;
    // private final GraphicsContext gc;

    public ChartStyle2D() {

        chartBackColor = Color.WHITE;
        chartBorderColor = Color.BLACK;
    }

    public void AddChartStyle2D(GraphicsContext gc, ChartStyle3D cs3d) {
        // for Point F
        double xmin = cs3d.getXMin();
        double xmax = cs3d.getXMax();
        double ymin = cs3d.getYMin();
        double ymax = cs3d.getYMax();
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        PointF ptF = new PointF(xmin, xmax, ymin, ymax, width, height);
        // end of PointF

        /* chartArea = new Rectangle(gc.getCanvas().getLayoutX(),
                gc.getCanvas().getLayoutY(),
                gc.getCanvas().getWidth(),
                gc.getCanvas().getHeight()); */
        Color colorPen = Color.BLACK;
        gc.setStroke(colorPen);
        gc.setLineWidth(1);
        gc.setFont(cs3d.getTickFont().getFont());

        // Create vertical gridlines:
        double fX, fY;
        if (cs3d.getIsYGrid() == true) {
            colorPen = cs3d.getGridStyle().getLineColor();
            gc.setStroke(colorPen);
            gc.setLineWidth(1);
            gc.setLineDashes(cs3d.getGridStyle().getDashed());

            for (fX = xmin + cs3d.getXTick(); fX < xmax;
                    fX += cs3d.getXTick()) {
                Point2D pt1 = ptF.Point(new Point2D(fX, ymin));
                Point2D pt2 = ptF.Point(new Point2D(fX, ymax));
                gc.strokeLine(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY());
            }

        }
        // Create horizontal gridlines:
        if (cs3d.getIsXGrid() == true) {

            for (fY = ymin + cs3d.getYTick(); fY < ymax;
                    fY += cs3d.getYTick()) {
                Point2D pt1 = ptF.Point(new Point2D(xmin, fY));
                Point2D pt2 = ptF.Point(new Point2D(xmax, fY));
                gc.strokeLine(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY());
            }
        }
        // Create the x-axis tick marks:
        for (fX = xmin; fX <= xmax; fX += cs3d.getXTick()) {
            gc.setStroke(Color.BLACK);
            Point2D yAxisPoint = ptF.Point(new Point2D(fX, ymin));
            Point2D pt2 = new Point2D(yAxisPoint.getX(), yAxisPoint.getY() - 5);
            gc.strokeLine(yAxisPoint.getX(), yAxisPoint.getY(), pt2.getX(), pt2.getY());

        }
        // Create the y-axis tick marks:
        for (fY = xmin; fY <= ymax; fY += cs3d.getYTick()) {
            Point2D xAxisPoint = ptF.Point(new Point2D(ymin, fY));
            Point2D pt2 = new Point2D(xAxisPoint.getX() + 5f, xAxisPoint.getY());
            gc.strokeLine(xAxisPoint.getX(), xAxisPoint.getY(), pt2.getX(), pt2.getY());
        }
    }

    private void AddLabels(GraphicsContext gl, ChartStyle3D cs3d) {
        // for Point F
        double xmin = cs3d.getXMin();
        double xmax = cs3d.getXMax();
        double ymin = cs3d.getYMin();
        double ymax = cs3d.getYMax();
        double width = gl.getCanvas().getWidth();
        double height = gl.getCanvas().getHeight();
        PointF ptF = new PointF(xmin, xmax, ymin, ymax, width, height);
        // end of PointF
        double xOffset = chartArea.getWidth() / 30.0;
        double yOffset = chartArea.getHeight() / 30.0;
        double labelFontSize = cs3d.getLabelFont().getSize();
        double titleFontSize = cs3d.getTitleFont().getSize();
        double tickFontSize = cs3d.getTickFont().getSize();
        gl.setStroke(cs3d.getTickFont().getColor());
        // Create the x-axis tick marks:
        for (double fX = cs3d.getXMin(); fX <= xmax;
                fX += cs3d.getXTick()) {
            Point2D yAxisPoint = ptF.Point(new Point2D(fX, ymin));
            // sFormat.Alignment = StringAlignment.Far;
            double sizeXTick = (new Text("" + fX)).getLayoutBounds().getWidth();
            gl.setTextAlign(TextAlignment.JUSTIFY);
            Point2D pt = new Point2D(yAxisPoint.getX()
                    + sizeXTick / 2 + gl.getCanvas().getLayoutX(),//left
                    yAxisPoint.getY() + 4.0 + gl.getCanvas().getLayoutY()); //top

            gl.fillText("" + fX, pt.getX(), pt.getY());

        }
        // Create the y-axis tick marks:
        for (double fY = cs3d.getYMin(); fY <= ymax; fY
                += cs3d.getYTick()) {
            Point2D xAxisPoint = ptF.Point(new Point2D(xmin, fY));
            Point2D pt2 = new Point2D(xAxisPoint.getX() - 3.0
                    + gl.getCanvas().getLayoutX(), //left
                    xAxisPoint.getY() - tickFontSize / 2
                    + gl.getCanvas().getLayoutY());
            gl.fillText("" + fY, pt2.getX(), pt2.getY());

        }
        // Add horizontal axis label:

        gl.setStroke(cs3d.getLabelFont().getColor());
        gl.setFont(cs3d.getLabelFont().getFont());
        double stringSize = (new Text(cs3d.getXLabel())).getLayoutBounds().getWidth();

        Point2D pt = new Point2D(gl.getCanvas().getLayoutBounds().getMinX()
                + gl.getCanvas().getLayoutBounds().getWidth() / 2 // width
                - stringSize / 2, chartArea.getLayoutBounds().getMaxY() //bottom
                - yOffset - labelFontSize);

        gl.fillText(cs3d.getXLabel(), pt.getX(), pt.getY());

        // Add y-axis label:
        gl.setTextAlign(TextAlignment.CENTER);
        gl.save();
        gl.translate(xOffset, yOffset + titleFontSize + yOffset / 3
                + gl.getCanvas().getLayoutBounds().getHeight() / 2);
        gl.rotate(-90);
        gl.fillText(cs3d.getYLabel(), 0, 0);
        // Restore it: 
        gl.restore();

        // Add title:
        gl.setStroke(cs3d.getTitleFont().getColor());
        gl.setFont(cs3d.getTitleFont().getFont());
        stringSize = (new Text(cs3d.getTitle())).getLayoutBounds().getWidth();
        if (!"NO TITLE".equals(cs3d.getTitle().toUpperCase())) {
            pt = new Point2D(gl.getCanvas().getLayoutX()
                    + gl.getCanvas().getWidth() / 2
                    - stringSize / 2, chartArea.getLayoutY()//top
                    + yOffset);
            gl.fillText(cs3d.getTitle(), pt.getX(), pt.getY());
        }
    }

    public void SetPlotArea(GraphicsContext gs, ChartStyle3D cs3d) {
        // Draw chart area:
        gs.setFill(chartBackColor);
        gs.fillRect(chartArea.getX(), chartArea.getY(),
                chartArea.getWidth(), chartArea.getHeight());
        gs.setLineWidth(2.0);
        gs.setStroke(chartBorderColor);
        gs.strokeRect(chartArea.getX(), chartArea.getY(),
                chartArea.getWidth(), chartArea.getHeight());

        // Set PlotArea:
        double xOffset = chartArea.getWidth() / 30.0;
        double yOffset = chartArea.getHeight() / 30.0;

        gs.setFont(cs3d.getLabelFont().getFont());
        double labelFontWidth = (new Text("A")).getLayoutBounds().getWidth();
        double labelFontHeight = (new Text("A")).getLayoutBounds().getHeight();
        gs.setFont(cs3d.getTitleFont().getFont());
        double titleFontHeight = (new Text("A")).getLayoutBounds().getHeight();
        gs.setFont(cs3d.getTickFont().getFont());
        double yTickSize = (new Text("A")).getLayoutBounds().getWidth();

        if ("NO TITLE".equals(cs3d.getTitle().toUpperCase())) {
            titleFontHeight = 8.0;
        }
        double xSpacing = xOffset / 3.0;
        double ySpacing = yOffset / 3.0;
        double tickSpacing = 2.0;

        double tickFontHeight = (new Text("A")).getLayoutBounds().getHeight();

        for (double yTick = cs3d.getYMin(); yTick <= cs3d.getYMax();
                yTick += cs3d.getYTick()) {
            double tempSize = (new Text("" + yTick)).getLayoutBounds().getWidth();

            if (yTickSize < tempSize) {
                yTickSize = tempSize;
            }
        }

        double leftMargin = xOffset + labelFontWidth
                + xSpacing + yTickSize + tickSpacing;
        double rightMargin = 2 * xOffset;
        double topMargin = yOffset
                + titleFontHeight + ySpacing;
        double bottomMargin = yOffset + labelFontHeight
                + ySpacing + tickSpacing + tickFontHeight;
        // Define the plot area:
        int plotX = (int) (chartArea.getX() + leftMargin);
        int plotY = (int) (chartArea.getY() + topMargin);

        int plotWidth = (int) (chartArea.getWidth() - leftMargin - rightMargin);
        int plotHeight = (int) (chartArea.getHeight() - topMargin - bottomMargin);
        gs.getCanvas().setLayoutX(plotX);
        gs.getCanvas().setLayoutY(plotY);
        if (cs3d.getIsColorBar()) {
            gs.getCanvas().setWidth(25 * plotWidth / 30);
        } else {
            gs.getCanvas().setWidth(plotWidth);
        }
        gs.getCanvas().setHeight(plotHeight);
        AddLabels(gs, cs3d);
    }

    // GETTER AND SETTER FUNCTIONS
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

    public Color getChartBackColor() {
        return chartBackColor;
    }

    public void setChartBackColor(Color value) {
        chartBackColor = value;
    }

    public Color getChartBorderColor() {
        return chartBorderColor;
    }

    public void setChartBorderColor(Color value) {
        chartBorderColor = value;
    }

    public Rectangle getChartArea() {
        return chartArea;
    }

    public void setChartArea(Rectangle value) {
        chartArea = value;
    }

}
