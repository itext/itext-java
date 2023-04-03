package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.renderer.DrawContext;

/**
 * This class is used to draw a checkBox icon in HTML mode.
 */
public final class HtmlCheckBoxRenderingStrategy implements ICheckBoxRenderingStrategy {

    public HtmlCheckBoxRenderingStrategy() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCheckBoxContent(DrawContext drawContext, CheckBoxRenderer checkBoxRenderer, Rectangle rectangle) {
        if (!checkBoxRenderer.isBoxChecked()) {
            return;
        }
        final PdfCanvas canvas = drawContext.getCanvas();
        canvas.saveState();
        canvas.setFillColor(ColorConstants.BLACK);
        DrawingUtil.drawPdfACheck(canvas, rectangle.getWidth(), rectangle.getHeight(),
                rectangle.getLeft(), rectangle.getBottom());
        canvas.restoreState();
    }
}
