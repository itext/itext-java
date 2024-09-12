/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.commons.datastructures.BiMap;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.forms.util.FontSizeUtil;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.DrawContext;

/**
 * This class is used to draw a checkBox icon in PDF mode this is the default strategy for drawing a checkBox.
 */
public final class PdfCheckBoxRenderingStrategy implements ICheckBoxRenderingStrategy {
    public static final BiMap<CheckBoxType, String> ZAPFDINGBATS_CHECKBOX_MAPPING;

    static {
        ZAPFDINGBATS_CHECKBOX_MAPPING = new BiMap<>();
        ZAPFDINGBATS_CHECKBOX_MAPPING.put(CheckBoxType.CHECK, "4");
        ZAPFDINGBATS_CHECKBOX_MAPPING.put(CheckBoxType.CIRCLE, "l");
        ZAPFDINGBATS_CHECKBOX_MAPPING.put(CheckBoxType.CROSS, "8");
        ZAPFDINGBATS_CHECKBOX_MAPPING.put(CheckBoxType.DIAMOND, "u");
        ZAPFDINGBATS_CHECKBOX_MAPPING.put(CheckBoxType.SQUARE, "n");
        ZAPFDINGBATS_CHECKBOX_MAPPING.put(CheckBoxType.STAR, "H");

    }

    /**
     * Creates a new {@link PdfCheckBoxRenderingStrategy} instance.
     */
    public PdfCheckBoxRenderingStrategy() {
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
        Border borderTop = checkBoxRenderer.<Border>getProperty(Property.BORDER_TOP);
        Border borderRight = checkBoxRenderer.<Border>getProperty(Property.BORDER_RIGHT);
        Border borderBottom = checkBoxRenderer.<Border>getProperty(Property.BORDER_BOTTOM);
        Border borderLeft = checkBoxRenderer.<Border>getProperty(Property.BORDER_LEFT);
        rectangle.applyMargins(borderTop == null ? 0 : borderTop.getWidth(),
                borderRight == null ? 0 : borderRight.getWidth(),
                borderBottom == null ? 0 : borderBottom.getWidth(),
                borderLeft == null ? 0 : borderLeft.getWidth(), true);
        final PdfCanvas canvas = drawContext.getCanvas();
        canvas.saveState();
        canvas.setFillColor(ColorConstants.BLACK);
        // matrix transformation to draw the checkbox in the right place
        // because we come here with relative and not absolute coordinates
        canvas.concatMatrix(1, 0, 0, 1, rectangle.getLeft(), rectangle.getBottom());
        final CheckBoxType checkBoxType = checkBoxRenderer.getCheckBoxType();
        if (checkBoxType == CheckBoxType.CROSS || checkBoxType == null) {
            final float customBorderWidth = retrieveBorderWidth(1, borderTop, borderRight, borderBottom, borderLeft);
            DrawingUtil.drawCross(canvas, rectangle.getWidth(), rectangle.getHeight(), customBorderWidth);
        } else {
            final String text = ZAPFDINGBATS_CHECKBOX_MAPPING.getByKey(checkBoxType);
            final PdfFont fontContainingSymbols = loadFontContainingSymbols();
            float fontSize = calculateFontSize(checkBoxRenderer, fontContainingSymbols, text, rectangle,
                    retrieveBorderWidth(CheckBoxRenderer.DEFAULT_BORDER_WIDTH,
                            borderTop, borderRight, borderBottom, borderLeft));
            drawZapfdingbatsIcon(fontContainingSymbols, text, fontSize, rectangle, canvas);
        }

        canvas.restoreState();
    }

    private static float retrieveBorderWidth(float defaultWidth, Border borderTop, Border borderRight, Border borderBottom,
                                      Border borderLeft) {
        if (borderTop == null && borderRight == null && borderBottom == null && borderLeft == null) {
            return defaultWidth;
        }
        float borderWidth = 0;
        if (borderTop != null) {
            borderWidth = Math.max(borderWidth, borderTop.getWidth());
        }
        if (borderRight != null) {
            borderWidth = Math.max(borderWidth, borderRight.getWidth());
        }
        if (borderBottom != null) {
            borderWidth = Math.max(borderWidth, borderBottom.getWidth());
        }
        if (borderLeft != null) {
            borderWidth = Math.max(borderWidth, borderLeft.getWidth());
        }
        return borderWidth;
    }

    private PdfFont loadFontContainingSymbols() {
        try {
            return PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
        } catch (java.io.IOException e) {
            throw new PdfException(e);
        }
    }

    private float calculateFontSize(CheckBoxRenderer checkBoxRenderer, PdfFont fontContainingSymbols, String text,
            Rectangle rectangle, float borderWidth) {
        float fontSize = -1;
        if (checkBoxRenderer.hasProperty(Property.FONT_SIZE)) {
            fontSize = checkBoxRenderer.getPropertyAsUnitValue(Property.FONT_SIZE).getValue();
        }
        if (fontSize <= 0) {
            fontSize = FontSizeUtil.approximateFontSizeToFitSingleLine(fontContainingSymbols,
                    new Rectangle(rectangle.getWidth(), rectangle.getHeight()), text, 0.1F, borderWidth);
        }
        if (fontSize <= 0) {
            throw new PdfException(FormsLogMessageConstants.CHECKBOX_FONT_SIZE_IS_NOT_POSITIVE);
        }
        return fontSize;
    }

    private void drawZapfdingbatsIcon(PdfFont fontContainingSymbols, String text, float fontSize, Rectangle rectangle,
            PdfCanvas canvas) {
        canvas.
                beginText().
                setFontAndSize(fontContainingSymbols, fontSize).
                resetFillColorRgb().
                setTextMatrix(
                        (rectangle.getWidth() - fontContainingSymbols.getWidth(text, fontSize)) / 2,
                        (rectangle.getHeight() - fontContainingSymbols.getAscent(text, fontSize))
                                / 2).
                showText(text).
                endText();
    }

}
