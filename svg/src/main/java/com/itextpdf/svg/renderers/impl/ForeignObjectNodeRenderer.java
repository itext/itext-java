/*
	This file is part of the iText (R) project.
	Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.util.WhiteSpaceUtil;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Text-only fallback for {@code foreignObject}.
 */
public class ForeignObjectNodeRenderer extends AbstractSvgNodeRenderer implements IBranchSvgNodeRenderer {

    private final List<ISvgNodeRenderer> children = new ArrayList<>();

    /**
     * Creates a new instance of {@link  ForeignObjectNodeRenderer}
     */
    public ForeignObjectNodeRenderer() {
        ///empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(ISvgNodeRenderer child) {
        if (child instanceof TextLeafSvgNodeRenderer) {
            child.setParent(this);
            children.add(child);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ISvgNodeRenderer> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISvgNodeRenderer createDeepCopy() {
        ForeignObjectNodeRenderer copy = new ForeignObjectNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        for (ISvgNodeRenderer child : children) {
            copy.addChild(child.createDeepCopy());
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return new Rectangle(parseHorizontalLength(getAttribute(SvgConstants.Attributes.X), context),
                parseVerticalLength(getAttribute(SvgConstants.Attributes.Y), context),
                parseHorizontalLength(getAttribute(SvgConstants.Attributes.WIDTH), context),
                parseVerticalLength(getAttribute(SvgConstants.Attributes.HEIGHT), context));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        Rectangle box = getObjectBoundingBox(context);
        if (box.getWidth() <= 0 || box.getHeight() <= 0) {
            return;
        }

        PdfCanvas pdfCanvas = context.getCurrentCanvas();
        pdfCanvas.saveState();
        try {
            Paragraph paragraph = createParagraph(context);
            paragraph.setHeight(box.getHeight());
            pdfCanvas.rectangle(box).clip().endPath();
            // SVG's y axis points down; layout text needs an upright coordinate system.
            pdfCanvas.concatMatrix(1, 0, 0, -1, box.getX(), box.getY());
            try (Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, -box.getHeight(),
                    box.getWidth(), box.getHeight()))) {
                canvas.add(paragraph);
            }
        } finally {
            pdfCanvas.restoreState();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void preDraw(SvgDrawContext context) {
        //The rendering is handled in the doDraw
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void postDraw(SvgDrawContext context) {
        //The rendering is handled in the doDraw
    }

    Paragraph createParagraph(SvgDrawContext context) {
        StringBuilder text = new StringBuilder();
        for (ISvgNodeRenderer child : children) {
            String content = child.getAttribute(SvgConstants.Attributes.TEXT_CONTENT);
            if (content != null) {
                text.append(content);
            }
        }
        Paragraph paragraph = new Paragraph(WhiteSpaceUtil.processWhitespaces(text.toString(), true, true).trim());
        paragraph.setMargin(0);
        float fontSize = getCurrentFontSize(context);
        paragraph.setFontSize(fontSize);
        paragraph.setProperty(Property.FONT_PROVIDER, context.getFontProvider());
        paragraph.setProperty(Property.FONT_SET, context.getTempFonts());
        paragraph.setFontFamily(getAttributeOrDefault(CommonCssConstants.FONT_FAMILY, ""));
        paragraph.setProperty(Property.FONT_WEIGHT, getAttribute(CommonCssConstants.FONT_WEIGHT));
        paragraph.setProperty(Property.FONT_STYLE, getAttribute(CommonCssConstants.FONT_STYLE));

        paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);
        paragraph.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);

        TransparentColor color = CssDimensionParsingUtils.parseColor(
                getAttributeOrDefault(CommonCssConstants.COLOR, "black"));
        paragraph.setFontColor(color.getColor(), color.getOpacity());
        String background = getAttribute(CommonCssConstants.BACKGROUND_COLOR);
        if (background != null) {
            TransparentColor backgroundColor = CssDimensionParsingUtils.parseColor(
                    CommonCssConstants.CURRENTCOLOR.equals(background)
                            ? getAttributeOrDefault(CommonCssConstants.COLOR, "black") : background);
            paragraph.setBackgroundColor(backgroundColor.getColor(), backgroundColor.getOpacity());
        }

        String lineHeight = getAttribute(CommonCssConstants.LINE_HEIGHT);
        if (lineHeight == null || CommonCssConstants.NORMAL.equals(lineHeight)) {
            paragraph.setMultipliedLeading(1.2F);
        } else if (CssTypesValidationUtils.isNumber(lineHeight)) {
            paragraph.setFixedLeading(Float.parseFloat(lineHeight) * fontSize);
        } else {
            paragraph.setFixedLeading(SvgCssUtils.parseAbsoluteLength(this, lineHeight,
                    fontSize, fontSize * 1.2F, context));
        }
        return paragraph;
    }
}
