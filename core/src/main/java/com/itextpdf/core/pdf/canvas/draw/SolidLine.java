package com.itextpdf.core.pdf.canvas.draw;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link Drawable} which draws a solid horizontal line along
 * the bottom edge of the specified rectangle.
 */
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
