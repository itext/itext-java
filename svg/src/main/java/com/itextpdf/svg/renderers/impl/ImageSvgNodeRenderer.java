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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

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
        String uri = this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF);
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

        float width;

        if (attributesAndStyles.containsKey(SvgConstants.Attributes.WIDTH)) {
            width = parseHorizontalLength(attributesAndStyles.get(SvgConstants.Attributes.WIDTH), context);
        } else {
            width = xObject.getWidth();
        }

        float height;

        if (attributesAndStyles.containsKey(SvgConstants.Attributes.HEIGHT)) {
            height = parseVerticalLength(attributesAndStyles.get(SvgConstants.Attributes.HEIGHT), context);
        } else {
            height = xObject.getHeight();
        }

        String preserveAspectRatio = "";

        if (attributesAndStyles.containsKey(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO)) {
            preserveAspectRatio = attributesAndStyles.get(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO);
        } else if (attributesAndStyles.containsKey(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO.toLowerCase())) {
            // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
            preserveAspectRatio = attributesAndStyles.get(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO.toLowerCase());
        }

        preserveAspectRatio = preserveAspectRatio.toLowerCase();
        if (!SvgConstants.Values.NONE.equals(preserveAspectRatio) && !(width == 0 || height == 0)) {
            float normalizedWidth;
            float normalizedHeight;
            if (xObject.getWidth() / width > xObject.getHeight() / height) {
                normalizedWidth = width;
                normalizedHeight = xObject.getHeight() / xObject.getWidth() * width;
            } else {
                normalizedWidth = xObject.getWidth() / xObject.getHeight() * height;
                normalizedHeight = height;
            }

            switch (preserveAspectRatio.toLowerCase()) {
                case SvgConstants.Values.XMIN_YMIN:
                    break;
                case SvgConstants.Values.XMIN_YMID:
                    y += Math.abs(normalizedHeight - height) / 2;
                    break;
                case SvgConstants.Values.XMIN_YMAX:
                    y += Math.abs(normalizedHeight - height);
                    break;
                case SvgConstants.Values.XMID_YMIN:
                    x += Math.abs(normalizedWidth - width) / 2;
                    break;
                case SvgConstants.Values.XMID_YMAX:
                    x += Math.abs(normalizedWidth - width) / 2;
                    y += Math.abs(normalizedHeight - height);
                    break;
                case SvgConstants.Values.XMAX_YMIN:
                    x += Math.abs(normalizedWidth - width);
                    break;
                case SvgConstants.Values.XMAX_YMID:
                    x += Math.abs(normalizedWidth - width);
                    y += Math.abs(normalizedHeight - height) / 2;
                    break;
                case SvgConstants.Values.XMAX_YMAX:
                    x += Math.abs(normalizedWidth - width);
                    y += Math.abs(normalizedHeight - height);
                    break;
                case SvgConstants.Values.DEFAULT_ASPECT_RATIO:
                default:
                    x += Math.abs(normalizedWidth - width) / 2;
                    y += Math.abs(normalizedHeight - height) / 2;
                    break;
            }

            width = normalizedWidth;
            height = normalizedHeight;
        }

        float v = y + height;
        currentCanvas.addXObjectWithTransformationMatrix(xObject, width, 0, 0, -height, x, v);
    }
}
