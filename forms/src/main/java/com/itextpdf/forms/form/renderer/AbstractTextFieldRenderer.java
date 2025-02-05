/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.fields.AbstractPdfFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.forms.util.BorderStyleUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract {@link AbstractFormFieldRenderer} for form fields with text content.
 */
public abstract class AbstractTextFieldRenderer extends AbstractFormFieldRenderer {

    /**
     * The font to be used for the text.
     */
    protected PdfFont font;

    /**
     * Creates a new {@link AbstractTextFieldRenderer} instance.
     *
     * @param modelElement the model element
     */
    AbstractTextFieldRenderer(IFormField modelElement) {
        super(modelElement);
    }

    /**
     * Creates a paragraph renderer.
     *
     * @param defaultValue the default value
     * @return the renderer
     */
    IRenderer createParagraphRenderer(String defaultValue) {
        if (defaultValue.isEmpty()) {
            defaultValue = "\u00a0";
        }

        Text text = new Text(defaultValue);
        FormFieldValueNonTrimmingTextRenderer nextRenderer = new FormFieldValueNonTrimmingTextRenderer(text);
        text.setNextRenderer(nextRenderer);

        return new Paragraph(text).setMargin(0).createRendererSubTree();
    }

    /**
     * Applies the default field properties.
     *
     * @param inputField the input field
     */
    void applyDefaultFieldProperties(PdfFormField inputField) {
        inputField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_NONE);
        TransparentColor color = getPropertyAsTransparentColor(Property.FONT_COLOR);
        if (color != null) {
            inputField.setColor(color.getColor());
        }
        inputField.setJustification(this.<TextAlignment>getProperty(Property.TEXT_ALIGNMENT));
        BorderStyleUtil.applyBorderProperty(this, inputField.getFirstFormAnnotation());
        Background background = this.<Background>getProperty(Property.BACKGROUND);
        if (background != null) {
            inputField.getFirstFormAnnotation().setBackgroundColor(background.getColor());
        }
    }

    float getHeightRowsBased(List<LineRenderer> lines, Rectangle bBox, int rows) {
        float averageLineHeight = bBox.getHeight() / lines.size();
        return averageLineHeight * rows;
    }

    /**
     * Updates the font.
     *
     * @param renderer the renderer
     */
    void updatePdfFont(ParagraphRenderer renderer) {
        Object retrievedFont;
        if (renderer != null) {
            List<LineRenderer> lines = renderer.getLines();
            if (lines != null) {
                for (LineRenderer line : lines) {
                    for (IRenderer child : line.getChildRenderers()) {
                        retrievedFont = child.<Object>getProperty(Property.FONT);
                        if (retrievedFont instanceof PdfFont) {
                            font = (PdfFont) retrievedFont;
                            return;
                        }
                    }
                }
            }
            retrievedFont = renderer.<Object>getProperty(Property.FONT);
            if (retrievedFont instanceof PdfFont) {
                font = (PdfFont) retrievedFont;
            }
        }
    }

    /**
     * Approximates font size to fit occupied area if width anf height are specified.
     *
     * @param layoutContext layout context that specifies layout area.
     * @param lFontSize minimal font size value.
     * @param rFontSize maximum font size value.
     *
     * @return fitting font size or -1 in case it shouldn't be approximated.
     */
    float approximateFontSize(LayoutContext layoutContext, float lFontSize, float rFontSize) {
        IRenderer flatRenderer = createFlatRenderer().setParent(this);

        Float areaWidth = retrieveWidth(layoutContext.getArea().getBBox().getWidth());
        Float areaHeight = retrieveHeight();
        if (areaWidth == null || areaHeight == null) {
            return -1;
        }
        flatRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(AbstractPdfFormField.DEFAULT_FONT_SIZE));
        LayoutContext newLayoutContext = new LayoutContext(new LayoutArea(1,
                new Rectangle((float) areaWidth, (float) areaHeight)));
        if (flatRenderer.layout(newLayoutContext).getStatus() == LayoutResult.FULL) {
            return -1;
        } else {
            final int numberOfIterations = 6;
            return calculateFittingFontSize(flatRenderer, lFontSize, rFontSize, newLayoutContext, numberOfIterations);
        }
    }

    float calculateFittingFontSize(IRenderer renderer, float lFontSize, float rFontSize,
                                   LayoutContext newLayoutContext, int numberOfIterations) {
        for (int i = 0; i < numberOfIterations; i++) {
            float mFontSize = (lFontSize + rFontSize) / 2;
            renderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(mFontSize));
            LayoutResult result = renderer.layout(newLayoutContext);
            if (result.getStatus() == LayoutResult.FULL) {
                lFontSize = mFontSize;
            } else {
                rFontSize = mFontSize;
            }
        }
        return lFontSize;
    }

    // The width based on cols of textarea and size of input isn't affected by box sizing, so we emulate it here.
    float updateHtmlColsSizeBasedWidth(float width) {
        if (BoxSizingPropertyValue.BORDER_BOX == this.<BoxSizingPropertyValue>getProperty(Property.BOX_SIZING)) {
            Rectangle dummy = new Rectangle(width, 0);
            applyBorderBox(dummy, true);
            applyPaddings(dummy, true);
            return dummy.getWidth();
        }
        return width;
    }

    /**
     * Adjust number of content lines.
     *
     * @param lines the lines that need to be rendered
     * @param bBox  the bounding box
     * @param rows  the desired number of lines
     */
    void adjustNumberOfContentLines(List<LineRenderer> lines, Rectangle bBox, int rows) {
        if (lines.size() != rows) {
            float rowsHeight = getHeightRowsBased(lines, bBox, rows);
            adjustNumberOfContentLines(lines, bBox, rows, rowsHeight);
        }
    }

    /**
     * Adjust number of content lines.
     *
     * @param lines  the lines that need to be rendered
     * @param bBox   the bounding box
     * @param height the desired height of content
     */
    void adjustNumberOfContentLines(List<LineRenderer> lines, Rectangle bBox, float height) {
        float averageLineHeight = bBox.getHeight() / lines.size();
        if (averageLineHeight > EPS) {
            int visibleLinesNumber = (int) Math.ceil(height / averageLineHeight);
            adjustNumberOfContentLines(lines, bBox, visibleLinesNumber, height);
        }
    }

    /**
     * Gets the value of the lowest bottom coordinate for all field's children recursively.
     *
     * @return the lowest child bottom.
     */
    float getLowestChildBottom(IRenderer renderer, float value) {
        float lowestChildBottom = value;
        for (IRenderer child : renderer.getChildRenderers()) {
            lowestChildBottom = getLowestChildBottom(child, lowestChildBottom);
            if (child.getOccupiedArea() != null &&
                    child.getOccupiedArea().getBBox().getBottom() < lowestChildBottom) {
                lowestChildBottom = child.getOccupiedArea().getBBox().getBottom();
            }
        }
        return lowestChildBottom;
    }

    private static void adjustNumberOfContentLines(List<LineRenderer> lines, Rectangle bBox,
            int linesNumber, float height) {
        bBox.moveUp(bBox.getHeight() - height);
        bBox.setHeight(height);
        if (lines.size() > linesNumber) {
            List<LineRenderer> subList = new ArrayList<>(lines.subList(0, linesNumber));
            lines.clear();
            lines.addAll(subList);
        }
    }
}
