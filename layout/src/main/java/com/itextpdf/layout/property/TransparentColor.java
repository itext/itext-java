package com.itextpdf.layout.property;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;

public class TransparentColor {
    private Color color;
    private float opacity;
    
    public TransparentColor(Color color) {
        this.color = color;
        this.opacity = 1f;
    }
    
    public TransparentColor(Color color, float opacity) {
        this.color = color;
        this.opacity = opacity;
    }

    public Color getColor() {
        return color;
    }

    public float getOpacity() {
        return opacity;
    }

    public void applyFillTransparency(PdfCanvas canvas) {
        applyTransparency(canvas, false);

    }

    public void applyStrokeTransparency(PdfCanvas canvas) {
        applyTransparency(canvas, true);        
    }

    private void applyTransparency(PdfCanvas canvas, boolean isStroke) {
        if (isTransparent()) {
            PdfExtGState extGState = new PdfExtGState();
            if (isStroke) {
                extGState.setStrokeOpacity(opacity);
            } else {
                extGState.setFillOpacity(opacity);
            }
            canvas.setExtGState(extGState);
        }
    }

    private boolean isTransparent() {
        return opacity < 1f;
    }

}
