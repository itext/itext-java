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
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;

/**
 * Responsible for drawing Images to the canvas.
 * Referenced SVG images aren't supported yet. TODO DEVSIX-2277
 */
public class ImageSvgNodeRenderer extends AbstractSvgNodeRenderer {

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        ImageSvgNodeRenderer copy = new ImageSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        ResourceResolver resourceResolver = context.getResourceResolver();

        if (resourceResolver == null || this.attributesAndStyles == null) {
            return;
        }

        String uri = this.attributesAndStyles.get(SvgConstants.Attributes.HREF);
        if (uri == null) {
            uri = this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF);
        }

        PdfXObject xObject = resourceResolver.retrieveImage(uri);

        if (xObject == null) {
            return;
        }

        PdfCanvas currentCanvas = context.getCurrentCanvas();

        float x = 0;
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
            x = parseHorizontalLength(attributesAndStyles.get(SvgConstants.Attributes.X), context);
        }

        float y = 0;
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
            y = parseVerticalLength(attributesAndStyles.get(SvgConstants.Attributes.Y), context);
        }

        float width = -1;
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.WIDTH)) {
            width = parseHorizontalLength(attributesAndStyles.get(SvgConstants.Attributes.WIDTH), context);
        }
        if (width < 0) {
            width = CssUtils.convertPxToPts(xObject.getWidth());
        }

        float height = -1;
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.HEIGHT)) {
            height = parseVerticalLength(attributesAndStyles.get(SvgConstants.Attributes.HEIGHT), context);
        }
        if (height < 0) {
            height = CssUtils.convertPxToPts(xObject.getHeight());
        }

        if (width != 0 && height != 0) {
            String[] alignAndMeet = retrieveAlignAndMeet();
            String align = alignAndMeet[0];
            String meetOrSlice = alignAndMeet[1];

            Rectangle currentViewPort = new Rectangle(0, 0, width, height);
            Rectangle viewBox = new Rectangle(0, 0, xObject.getWidth(), xObject.getHeight());
            Rectangle appliedViewBox = SvgCoordinateUtils.applyViewBox(viewBox, currentViewPort, align, meetOrSlice);

            float scaleWidth = appliedViewBox.getWidth() / viewBox.getWidth();
            float scaleHeight = appliedViewBox.getHeight() / viewBox.getHeight();

            float xOffset = appliedViewBox.getX() / scaleWidth - viewBox.getX();
            float yOffset = appliedViewBox.getY() / scaleHeight - viewBox.getY();

            x += xOffset;
            y += yOffset;
            width = appliedViewBox.getWidth();
            height = appliedViewBox.getHeight();

            if (SvgConstants.Values.SLICE.equals(meetOrSlice)) {
                currentCanvas.saveState()
                        .rectangle(currentViewPort)
                        .clip()
                        .endPath()
                        .addXObjectWithTransformationMatrix(xObject, width, 0, 0, -height, x, y + height)
                        .restoreState();
                return;
            }
        }
        currentCanvas.addXObjectWithTransformationMatrix(xObject, width, 0, 0, -height, x, y + height);
    }
}
