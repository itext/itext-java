package com.itextpdf.canvas.draw;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.basics.geom.Rectangle;

public class SolidLine implements Drawable {
    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState();
        canvas.setLineWidth(0.5f);
        canvas
                .moveTo(drawArea.getX(), drawArea.getY())
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY())
                .stroke()
                .restoreState();
    }
}
