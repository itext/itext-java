package com.itextpdf.canvas.draw;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;

public interface Drawable {
    void draw(PdfCanvas canvas, Rectangle drawArea);
}
