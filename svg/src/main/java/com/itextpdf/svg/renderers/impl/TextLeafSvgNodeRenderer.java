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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.renderer.TextRenderer;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgTextProperties;
import com.itextpdf.svg.utils.SvgTextUtil;
import com.itextpdf.svg.utils.TextRectangle;

/**
 * {@link ISvgNodeRenderer} implementation for drawing text to a canvas.
 */
public class TextLeafSvgNodeRenderer extends AbstractSvgNodeRenderer implements ISvgTextNodeRenderer {

    private final Text text = new Text("");

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
    @Deprecated
    public float[] getRelativeTranslation() {
        return new float[]{0.0f, 0.0f};
    }

    @Override
    @Deprecated
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
            final float parentFontSize = ((AbstractSvgNodeRenderer) getParent()).getCurrentFontSize(context);
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
            text.setText(this.attributesAndStyles.get(SvgConstants.Attributes.TEXT_CONTENT));

            ((TextSvgBranchRenderer) getParent()).applyFontProperties(text, context);
            ((TextSvgBranchRenderer) getParent()).applyTextRenderingMode(text);
            applyTransform(context);
            applyGraphicsState(context);
            ((TextSvgBranchRenderer) getParent()).addTextChild(text, context);
        }
    }

    @Override
    protected boolean canElementFill() {
        return false;
    }

    private void applyTransform(SvgDrawContext context) {
        AffineTransform transform = context.getRootTransform();
        text.setHorizontalScaling((float) transform.getScaleX());
        text.setProperty(Property.VERTICAL_SCALING, transform.getScaleY());
        text.setProperty(Property.SKEW, new float[]{(float) transform.getShearX(), (float) transform.getShearY()});
    }

    private void applyGraphicsState(SvgDrawContext context) {
        SvgTextProperties textProperties = context.getSvgTextProperties();
        // TODO DEVSIX-8774 support stroke-opacity for text at layout level
        // TODO DEVSIX-8776 support dash-pattern in layout
        text.setFontColor(textProperties.getFillColor());
        text.setStrokeWidth(textProperties.getLineWidth());
        text.setStrokeColor(textProperties.getStrokeColor());
        text.setOpacity(textProperties.getFillOpacity());
    }
}
