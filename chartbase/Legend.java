package com.edumacs.chart.base;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author moham
 */
public class Legend {

    private boolean isLegendVisible;
    private LegendPositionEnum legendPosition;
    private Color textColor;
    private boolean isBorderVisible;
    private Color legendBackColor;
    private Color legendBorderColor;
    private Font legendFont;

    public Legend() {
        legendPosition = LegendPositionEnum.NorthEast;
        textColor = Color.BLACK;
        isLegendVisible = false;
        isBorderVisible = true;
        legendBackColor = Color.WHITE;
        legendBorderColor = Color.BLACK;
        legendFont = Font.getDefault();
    }

    public boolean getIsLegendVisible() {
        return isLegendVisible;
    }

    public void setIsLegendVisible(boolean isvis) {
        isLegendVisible = isvis;
    }

    public LegendPositionEnum getLegendPosition() {
        return legendPosition;
    }

    public void setLegendPosition(LegendPositionEnum pos) {
        legendPosition = pos;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color clr) {
        textColor = clr;
    }

    public boolean getIsBorderVisible() {
        return isBorderVisible;
    }

    public void setIsBorderVisible(boolean vis) {
        isBorderVisible = vis;
    }

    public Color getLegendBackColor() {
        return legendBackColor;
    }

    public void setLegendBackColor(Color clr) {
        legendBackColor = clr;
    }

    public Color getLegendBorderColor() {
        return legendBorderColor;
    }

    public void setLegendBorderColor(Color clr) {
        legendBorderColor = clr;
    }

    public Font getLegendFont() {
        return legendFont;
    }

    public void setLegendFont(Font fnt) {
        legendFont = fnt;
    }

    public enum LegendPositionEnum {
        North, NorthWest,
        West, SouthWest,
        South, SouthEast,
        East, NorthEast
    }

    public void AddLegend(GraphicsContext gc, DataCollection dc, ChartStyle cs) {
        if (dc.getDataSeriesList().size() < 1) {
            return;
        }
        if (!getIsLegendVisible()) {
        }
        int numberOfDataSeries = dc.getDataSeriesList().size();
        String[] legendLabels = new String[numberOfDataSeries];
        int n = 0;
        for (DataSeries ds : dc.getDataSeriesList()) {
            legendLabels[n] = ds.getSeriesName();
            n++;
        }
        double offSet = 10;
        double xc = 0;
        double yc = 0;
        double legendWidth = (new Text(legendLabels[0])).getLayoutBounds().getWidth();
        //finding max length label
        for (String legendLabel : legendLabels) {
            double tmp = (new Text(legendLabel)).getLayoutBounds().getWidth();
            if (legendWidth < tmp) {
                legendWidth = tmp;
            }
        }
        legendWidth = legendWidth + 50;
        double hWidth = legendWidth / 2;
        double legendHeight = 18 * numberOfDataSeries;
        double hHeight = legendHeight / 2;
        switch (legendPosition) {
            case East:
                xc = cs.getPlotArea().getX() + cs.getPlotArea().getWidth()
                        - offSet - hWidth;
                yc = cs.getPlotArea().getY() + cs.getPlotArea().getHeight() / 2;
                break;
            case North:
                xc = cs.getPlotArea().getX() + cs.getPlotArea().getWidth() / 2;
                yc = cs.getPlotArea().getY() + offSet + hHeight;
                break;
            case NorthEast:
                xc = cs.getPlotArea().getX() + cs.getPlotArea().getWidth()
                        - offSet - hWidth;
                yc = cs.getPlotArea().getY() + offSet + hHeight;
                break;
            case NorthWest:
                xc = cs.getPlotArea().getX() + offSet + hWidth;
                yc = cs.getPlotArea().getY() + offSet + hHeight;
                break;
            case South:
                xc = cs.getPlotArea().getX() + cs.getPlotArea().getWidth() / 2;
                yc = cs.getPlotArea().getY() + cs.getPlotArea().getHeight() - offSet - hHeight;
                break;
            case SouthEast:
                xc = cs.getPlotArea().getX() + cs.getPlotArea().getWidth()
                        - offSet - hWidth;
                yc = cs.getPlotArea().getY() + cs.getPlotArea().getHeight() - offSet - hHeight;
                break;
            case SouthWest:
                xc = cs.getPlotArea().getX() + offSet + hWidth;
                yc = cs.getPlotArea().getY() + cs.getPlotArea().getHeight()
                        - offSet - hHeight;
                break;
            case West:
                xc = cs.getPlotArea().getX() + offSet + hWidth;
                yc = cs.getPlotArea().getY() + cs.getPlotArea().getHeight() / 2;
                break;
        }
        DrawLegend(gc, xc, yc, hWidth, hHeight, dc, cs);
    }

    private void DrawLegend(GraphicsContext gc,
            double xCenter, double yCenter, double hWidth, double hHeight,
            DataCollection dc, ChartStyle cs) {

        double spacing = 8;
        double textHeight = 8;
        double htextHeight = textHeight / 2;
        double lineLength = 30;
        // double hlineLength = lineLength / 2;
        gc.setStroke(legendBorderColor);
        gc.setLineWidth(1);
        Rectangle legendRectangle;
        if (isLegendVisible) {
            legendRectangle = new Rectangle(xCenter - hWidth, yCenter - hHeight,
                    2 * hWidth, 2 * hHeight);
            gc.setFill(legendBackColor);
            gc.setLineWidth(0);
            gc.fillRect(legendRectangle.getX(), legendRectangle.getY(),
                    legendRectangle.getWidth(), legendRectangle.getHeight());
            if (isBorderVisible) {
                gc.setLineWidth(0.5);
                gc.setStroke(legendBorderColor);
                gc.strokeRect(legendRectangle.getX(), legendRectangle.getY(),
                        legendRectangle.getWidth(), legendRectangle.getHeight());
            }
            int n = 1;
            for (DataSeries ds : dc.getDataSeriesList()) {
                // double xSymbol = legendRectangle.getX() + spacing + hlineLength;
                double xText = legendRectangle.getX() + 2 * spacing + lineLength;
                double yText = legendRectangle.getY() + n * spacing
                        + (2 * n - 1) * htextHeight;
                gc.setStroke(ds.getLineStyle().getLineColor());
                gc.setLineWidth(ds.getLineStyle().getThickness());
                gc.setLineDashes(ds.getLineStyle().getDashed());
                Point2D ptStart = new Point2D(legendRectangle.getX() + spacing, yText);
                Point2D ptEnd = new Point2D(legendRectangle.getX()
                        + spacing + lineLength, yText);
                gc.strokeLine(ptStart.getX(), ptStart.getY(), ptEnd.getX(), ptEnd.getY());
                // Draw text: 
                gc.setStroke(textColor);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setFill(textColor);
                gc.fillText(ds.getSeriesName(), xText, yText);
                n++;
            }
        }
    }
}
