/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.renderer.TextRenderer;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgTextUtil;
import com.itextpdf.svg.utils.TextRectangle;

/**
 * {@link ISvgNodeRenderer} implementation for drawing text to a canvas.
 */
public class TextLeafSvgNodeRenderer extends AbstractSvgNodeRenderer implements ISvgTextNodeRenderer {

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        TextLeafSvgNodeRenderer copy = new TextLeafSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }


    @Override
    public float getTextContentLength(float parentFontSize, PdfFont font) {
        float contentLength = 0.0f;
        if (font != null && this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_CONTENT)) {
            // Use own font-size declaration if it is present, parent's otherwise
            final float fontSize = SvgTextUtil.resolveFontSize(this, parentFontSize);
            final String content = this.attributesAndStyles.get(SvgConstants.Attributes.TEXT_CONTENT);
            contentLength = font.getWidth(content, fontSize);
        }
        return contentLength;
    }

    @Override
    public float[] getRelativeTranslation() {
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public boolean containsRelativeMove() {
        return false; //Leaf text elements do not contain any kind of transformation
    }

    @Override
    public boolean containsAbsolutePositionChange() {
        return false; //Leaf text elements do not contain any kind of transformation
    }

    @Override
    public float[][] getAbsolutePositionChanges() {
        float[] part = new float[]{0f};
        return new float[][]{part, part};
    }

    @Override
    public TextRectangle getTextRectangle(SvgDrawContext context, Point basePoint) {
        if (getParent() instanceof TextSvgBranchRenderer && basePoint != null) {
            final float parentFontSize = ((AbstractSvgNodeRenderer) getParent()).getCurrentFontSize();
            final PdfFont parentFont = ((TextSvgBranchRenderer) getParent()).getFont();
            final float textLength = getTextContentLength(parentFontSize, parentFont);
            final float[] fontAscenderDescenderFromMetrics = TextRenderer
                    .calculateAscenderDescender(parentFont, RenderingMode.HTML_MODE);
            final float fontAscender =
                    FontProgram.convertTextSpaceToGlyphSpace(fontAscenderDescenderFromMetrics[0]) * parentFontSize;
            final float fontDescender = FontProgram.convertTextSpaceToGlyphSpace(
                    fontAscenderDescenderFromMetrics[1]) * parentFontSize;
            // TextRenderer#calculateAscenderDescender returns fontDescender as a negative value so we should subtract this value
            final float textHeight = fontAscender - fontDescender;
            return new TextRectangle((float) basePoint.getX(), (float) basePoint.getY() - fontAscender, textLength,
                    textHeight, (float) basePoint.getY());
        } else {
            return null;
        }
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_CONTENT)) {
            PdfCanvas currentCanvas = context.getCurrentCanvas();
            //TODO(DEVSIX-2507): Support for glyph by glyph handling of x, y and rotate
            if (context.getPreviousElementTextMove() == null) {
                currentCanvas.moveText(context.getTextMove()[0], context.getTextMove()[1]);
            } else {
                currentCanvas.moveText(context.getPreviousElementTextMove()[0],
                        context.getPreviousElementTextMove()[1]);
            }
            currentCanvas.showText(this.attributesAndStyles.get(SvgConstants.Attributes.TEXT_CONTENT));
        }
    }

    @Override
    protected boolean canElementFill() {
        return false;
    }

}
