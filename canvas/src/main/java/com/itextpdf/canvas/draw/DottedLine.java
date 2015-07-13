package com.itextpdf.canvas.draw;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.PdfCanvasConstants;
import com.itextpdf.core.geom.Rectangle;

public class DottedLine implements Drawable{

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState();
        canvas.setLineDash(0, 4, 4 / 2);
        canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);
        canvas
                .moveTo(drawArea.getX(), drawArea.getY())
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY())
                .stroke()
                .restoreState();
    }
}
