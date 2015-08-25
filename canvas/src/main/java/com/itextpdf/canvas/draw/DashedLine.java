package com.itextpdf.canvas.draw;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.basics.geom.Rectangle;

public class DashedLine implements Drawable{
    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState();
        canvas.setLineWidth(0.5f);
        canvas.setLineDash(2, 2);
        canvas
                .moveTo(drawArea.getX(), drawArea.getY() + drawArea.getHeight() / 2)
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY() + drawArea.getHeight() / 2)
                .stroke();
        canvas.restoreState();
    }
}
