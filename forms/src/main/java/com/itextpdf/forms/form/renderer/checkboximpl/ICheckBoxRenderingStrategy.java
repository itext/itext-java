package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.renderer.DrawContext;

/**
 * This interface is used to draw a checkBox icon.
 */
public interface ICheckBoxRenderingStrategy {

    /**
     * Draws a check box icon.
     *
     * @param drawContext      the draw context
     * @param checkBoxRenderer the checkBox renderer
     * @param rectangle        the rectangle where the icon should be drawn
     */
    void drawCheckBoxContent(DrawContext drawContext, CheckBoxRenderer checkBoxRenderer, Rectangle rectangle);
}

