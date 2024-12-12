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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.ImageRenderer;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.element.SvgImage;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.xobject.SvgImageXObject;

/**
 * Represents a renderer for the {@link SvgImage} layout element.
 */
public class SvgImageRenderer extends ImageRenderer {
    /**
     * Creates an SvgImageRenderer from its corresponding layout object.
     *
     * @param image the {@link SvgImage} which this object should manage
     */
    public SvgImageRenderer(SvgImage image) {
        super(image);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        SvgImage svgImage = (SvgImage) modelElement;
        if (svgImage.getSvgImageXObject().isRelativeSized()) {
            calculateRelativeSizedSvgSize(svgImage, layoutContext.getArea().getBBox());
        }

        return super.layout(layoutContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        ((SvgImage) modelElement).getSvgImageXObject().generate(drawContext.getDocument());
        super.draw(drawContext);
    }

    private void calculateRelativeSizedSvgSize(SvgImage svgImage, Rectangle layoutBox) {
        SvgImageXObject svgImageXObject = svgImage.getSvgImageXObject();
        ISvgNodeRenderer svgRootRenderer = svgImageXObject.getResult().getRootRenderer();

        Float aspectRatio = null;
        float[] viewBoxValues = SvgCssUtils.parseViewBox(svgRootRenderer);
        if (viewBoxValues != null && viewBoxValues.length == SvgConstants.Values.VIEWBOX_VALUES_NUMBER) {
            // aspectRatio can also be specified by absolute height and width,
            // but in that case SVG isn't relative and processed as usual image
            aspectRatio = viewBoxValues[2] / viewBoxValues[3];
        }

        Float retrievedAreaWidth = retrieveWidth(layoutBox.getWidth());
        Float retrievedAreaHeight = retrieveHeight();

        float areaWidth = retrievedAreaWidth == null ?
                (aspectRatio == null ? Values.DEFAULT_VIEWPORT_WIDTH : layoutBox.getWidth())
                : (float) retrievedAreaWidth;
        float areaHeight = retrievedAreaHeight == null ? Values.DEFAULT_VIEWPORT_HEIGHT : (float) retrievedAreaHeight;

        float finalWidth;
        float finalHeight;

        if (aspectRatio != null && (retrievedAreaHeight == null || retrievedAreaWidth == null)) {
            if (retrievedAreaWidth == null && retrievedAreaHeight != null) {
                finalHeight = areaHeight;
                finalWidth = (float) (finalHeight * aspectRatio);
            } else {
                finalWidth = areaWidth;
                finalHeight = (float) (finalWidth / aspectRatio);
            }
        } else {
            finalWidth = areaWidth;
            finalHeight = areaHeight;
        }

        svgRootRenderer.setAttribute(Attributes.WIDTH, null);
        svgRootRenderer.setAttribute(Attributes.HEIGHT, null);

        svgImageXObject.updateBBox(finalWidth, finalHeight);
        imageWidth = svgImage.getImageWidth();
        imageHeight = svgImage.getImageHeight();
    }
}
