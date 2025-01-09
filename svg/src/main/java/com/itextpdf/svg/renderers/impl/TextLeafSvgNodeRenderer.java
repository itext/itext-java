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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
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
    @Deprecated
    public float getTextContentLength(float parentFontSize, PdfFont font) {
        float contentLength = 0.0f;
        if (font != null && this.attributesAndStyles != null &&
                this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_CONTENT)) {
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
    public TextRectangle getTextRectangle(SvgDrawContext context, Point startPoint) {
        if (getParent() instanceof TextSvgBranchRenderer && startPoint != null) {
            LineRenderer lineRenderer = layoutText(context);
            if (lineRenderer == null) {
                return null;
            }
            Rectangle textBBox = lineRenderer.getOccupiedAreaBBox();
            final float textLength = textBBox.getWidth();
            final float textHeight = textBBox.getHeight();

            return new TextRectangle((float) startPoint.getX(), (float) startPoint.getY() - lineRenderer.getMaxAscent(),
                    textLength, textHeight, (float) startPoint.getY());
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
        if (this.attributesAndStyles != null &&
                this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_CONTENT)) {
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

    private LineRenderer layoutText(SvgDrawContext context) {
        if (this.attributesAndStyles != null &&
                this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_CONTENT)) {
            // We need to keep all spaces after whitespace processing, so spaces are replaced with SpaceChar to avoid
            // trimming trailing spaces at layout level (they trimmed in the beginning of the paragraph by default,
            // but current text could be somewhere in the middle or end in the final result).
            text.setText(this.attributesAndStyles.get(SvgConstants.Attributes.TEXT_CONTENT).replace(" ", "\u00a0"));
            ((TextSvgBranchRenderer) getParent()).applyFontProperties(text, context);
            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.FORCED_PLACEMENT, true);
            ParagraphRenderer paragraphRenderer = new ParagraphRenderer(paragraph);
            paragraph.setNextRenderer(paragraphRenderer);
            paragraph.add(text);
            PdfFormXObject xObject = new PdfFormXObject(new Rectangle(1e6f, 0));
            try (Canvas canvas = new Canvas(new PdfCanvas(xObject, context.getCurrentCanvas().getDocument()),
                    xObject.getBBox().toRectangle())) {
                canvas.add(paragraph);
            }
            return paragraphRenderer.getLines().get(0);
        }
        return null;
    }
}
