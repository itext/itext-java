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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.xobject.SvgImageXObject;

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
        if (context == null || this.attributesAndStyles == null) {
            return null;
        }

        PdfXObject xObject = retrieveImage(context.getResourceResolver());
        ImageParameters imageParameters = calculateImageParameters(context, xObject);
        return imageParameters == null ? null : imageParameters.imageRectangle;
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        ResourceResolver resourceResolver = context.getResourceResolver();

        if (resourceResolver == null || this.attributesAndStyles == null) {
            return;
        }

        PdfXObject xObject = retrieveImage(resourceResolver);
        ImageParameters imageParameters = calculateImageParameters(context, xObject);
        if (imageParameters == null) {
            return;
        }

        PdfCanvas currentCanvas = context.getCurrentCanvas();
        Rectangle imageRectangle = imageParameters.imageRectangle;
        if (SvgConstants.Values.SLICE.equals(imageParameters.meetOrSlice)) {
            currentCanvas.saveState()
                    .rectangle(imageParameters.viewPort)
                    .clip()
                    .endPath()
                    .addXObjectWithTransformationMatrix(xObject, imageRectangle.getWidth(), 0, 0,
                            -imageRectangle.getHeight(), imageRectangle.getX(), imageRectangle.getTop())
                    .restoreState();
            return;
        }
        currentCanvas.addXObjectWithTransformationMatrix(xObject, imageRectangle.getWidth(), 0, 0,
                -imageRectangle.getHeight(), imageRectangle.getX(), imageRectangle.getTop());
    }

    private PdfXObject retrieveImage(ResourceResolver resourceResolver) {
        if (resourceResolver == null || this.attributesAndStyles == null) {
            return null;
        }
        String uri = this.attributesAndStyles.get(SvgConstants.Attributes.HREF);
        if (uri == null) {
            uri = this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF);
        }
        return resourceResolver.retrieveImage(uri);
    }

    private ImageParameters calculateImageParameters(SvgDrawContext context, PdfXObject xObject) {
        if (xObject == null) {
            return null;
        }

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

        if (width <= 0 || height <= 0) {
            return null;
        }

        String[] alignAndMeet = retrieveAlignAndMeet();
        String align = alignAndMeet[0];
        String meetOrSlice = alignAndMeet[1];

            Rectangle currentViewPort = new Rectangle(0, 0, width, height);
            Rectangle viewBox;
            if (xObject.getWidth() <= 0 || xObject.getHeight() <= 0) {
                viewBox = new Rectangle(currentViewPort);
                // TODO DEVSIX-4107 - we do not support svg inside svg yet.
                // But at least we should not produce corrupted PDF files with form xobjects without BBox
                if (xObject instanceof SvgImageXObject) {
                    ((SvgImageXObject) xObject).setBBox(new PdfArray(viewBox));
                }
            } else {
                viewBox = new Rectangle(0, 0, xObject.getWidth(), xObject.getHeight());
            }

            Rectangle appliedViewBox = SvgCoordinateUtils.applyViewBox(viewBox, currentViewPort, align, meetOrSlice);

        float scaleWidth = appliedViewBox.getWidth() / viewBox.getWidth();
        float scaleHeight = appliedViewBox.getHeight() / viewBox.getHeight();

        float origX = x;
        float origY = y;
        x += appliedViewBox.getX() / scaleWidth - viewBox.getX();
        y += appliedViewBox.getY() / scaleHeight - viewBox.getY();

        Rectangle imageRectangle = new Rectangle(x, y, appliedViewBox.getWidth(), appliedViewBox.getHeight());
        Rectangle clipRectangle = new Rectangle(origX, origY, width, height);
        return new ImageParameters(imageRectangle, clipRectangle, meetOrSlice);
    }

    private static final class ImageParameters {
        final Rectangle imageRectangle;
        final Rectangle viewPort;
        final String meetOrSlice;

        ImageParameters(Rectangle imageRectangle, Rectangle viewPort, String meetOrSlice) {
            this.imageRectangle = imageRectangle;
            this.viewPort = viewPort;
            this.meetOrSlice = meetOrSlice;
        }
    }
}
