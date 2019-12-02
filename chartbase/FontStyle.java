package com.edumacs.chart.base;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author mohammadfaizun
 */
public class FontStyle {

    private Color fontColor = Color.BLACK;
    private int fontSize = 8;
    private String fontType = "Arial Narrow";
    private Font font = new Font(fontType, fontSize);

    public void setColor(Color clr) {
        fontColor = clr;
    }

    public Color getColor() {
        return fontColor;
    }

    public void setSize(int size) {
        fontSize = size;
    }

    public int getSize() {
        return fontSize;
    }

    public void setType(String ftype) {
        fontType = ftype;
    }

    public String getType() {
        return fontType;
    }

    public void setFont(Font fnt) {
        font = fnt;
    }

    public Font getFont() {
        font = new Font(fontType, fontSize);
        return font;
    }

}
