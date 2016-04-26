package com.itextpdf.layout.property;

import com.itextpdf.kernel.color.Color;

public class Underline {
    protected Color color;
    protected float thickness;
    protected float thicknessMul;
    protected float yPosition;
    protected float yPositionMul;
    protected int lineCapStyle;

    public Underline(Color color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int lineCapStyle) {
        this.color = color;
        this.thickness = thickness;
        this.thicknessMul = thicknessMul;
        this.yPosition = yPosition;
        this.yPositionMul = yPositionMul;
        this.lineCapStyle = lineCapStyle;
    }

    public Color getColor() {
        return color;
    }

    public float getThickness(float fontSize) {
        return thickness + thicknessMul * fontSize;
    }

    public float getYPosition(float fontSize) {
        return yPosition + yPositionMul * fontSize;
    }

    public float getYPositionMul() {
        return yPositionMul;
    }
}
