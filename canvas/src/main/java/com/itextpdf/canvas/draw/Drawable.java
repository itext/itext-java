package com.itextpdf.canvas.draw;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.Rectangle;

public interface Drawable {
    void draw(PdfCanvas canvas, Rectangle drawArea);
}
