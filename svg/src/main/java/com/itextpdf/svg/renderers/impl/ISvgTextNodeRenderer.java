package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

public interface ISvgTextNodeRenderer extends ISvgNodeRenderer {

    float getTextContentLength(float parentFontSize, PdfFont font);

    float[] getRelativeTranslation();

    boolean containsRelativeMove();

    boolean containsAbsolutePositionChange();

    float[][] getAbsolutePositionChanges();
}
