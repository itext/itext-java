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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
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
        Rectangle layoutBox = layoutContext.getArea().getBBox();
        if (svgImage.getSvgImageXObject().isRelativeSized()) {
            calculateRelativeSizedSvgSize(svgImage, layoutBox);
        } else if (svgImage.getSvgImageXObject().isCreatedByObject()
                && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {

            NullableArea retrievedArea = new NullableArea(retrieveWidth(layoutBox.getWidth()), retrieveHeight());
            PdfArray bbox = svgImage.getSvgImageXObject().getBBox();
            if (retrievedArea.width != null && retrievedArea.height != null) {
                bbox.set(2, new PdfNumber((double) retrievedArea.width));
                bbox.set(3, new PdfNumber((double) retrievedArea.height));
                imageWidth = (float) retrievedArea.width;
                imageHeight = (float) retrievedArea.height;
            } else if (retrievedArea.width != null) {
                Area bboxArea = new Area(((PdfNumber) bbox.get(2)).floatValue(), ((PdfNumber) bbox.get(3)).floatValue());
                double verticalScaling = (double) retrievedArea.width / bboxArea.width;
                bbox.set(2, new PdfNumber((double) retrievedArea.width));
                bbox.set(3, new PdfNumber(bboxArea.height * verticalScaling));
                imageWidth = (float) retrievedArea.width;
                imageHeight = imageHeight * (float) verticalScaling;
            } else if (retrievedArea.height != null) {
                Area bboxArea = new Area(((PdfNumber) bbox.get(2)).floatValue(), ((PdfNumber) bbox.get(3)).floatValue());
                double horizontalScaling = (double) retrievedArea.height / bboxArea.height;
                bbox.set(2, new PdfNumber(bboxArea.width * horizontalScaling));
                bbox.set(3, new PdfNumber((double) retrievedArea.height));
                imageWidth = imageWidth * (float) horizontalScaling;
                imageHeight = (float) retrievedArea.height;
            }
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

        NullableArea retrievedArea = new NullableArea(retrieveWidth(layoutBox.getWidth()), retrieveHeight());
        boolean preserveAspectRatioNone
                = Values.NONE.equals(svgRootRenderer.getAttribute(Attributes.PRESERVE_ASPECT_RATIO));
        Area area = new Area();

        area.width = retrievedArea.width == null ?
                (aspectRatio == null ? Values.DEFAULT_VIEWPORT_WIDTH : layoutBox.getWidth())
                : (float) retrievedArea.width;
        area.height = retrievedArea.height == null ?
                (aspectRatio == null ? Values.DEFAULT_VIEWPORT_HEIGHT : layoutBox.getHeight())
                : (float) retrievedArea.height;

        UnitValue elementWidth = svgImageXObject.getElementWidth();
        UnitValue elementHeight = svgImageXObject.getElementHeight();

        //For aspect ratio none we're using the default viewport instead of layoutBox to behave like a browser
        //But this only for <img>, for all other cases using layoutBox as a fallback
        Area finalArea = new Area();
        if (preserveAspectRatioNone && svgImageXObject.isCreatedByImg()) {
            finalArea.width = retrievedArea.width == null ? Values.DEFAULT_VIEWPORT_WIDTH : (float) retrievedArea.width;
            finalArea.height = retrievedArea.height == null ? Values.DEFAULT_VIEWPORT_HEIGHT : (float) retrievedArea.height;
        } else {
            finalArea = initMissingMetricsAndApplyAspectRatio(aspectRatio, retrievedArea,
                    area, elementWidth, elementHeight);
        }

        if (svgImageXObject.isCreatedByImg() && viewBoxValues == null) {
            if (this.<UnitValue>getProperty(Property.WIDTH) == null) {
                this.setProperty(Property.WIDTH, UnitValue.createPointValue(finalArea.width));
            }
            if (retrieveHeight() == null) {
                this.setProperty(Property.HEIGHT, UnitValue.createPointValue(finalArea.height));
            }
            svgImageXObject.updateBBox(finalArea.width, finalArea.height);
        } else {
            svgRootRenderer.setAttribute(Attributes.WIDTH, null);
            svgRootRenderer.setAttribute(Attributes.HEIGHT, null);
            svgImageXObject.updateBBox(finalArea.width, finalArea.height);
        }

        imageWidth = svgImage.getImageWidth();
        imageHeight = svgImage.getImageHeight();
    }

    private Area initMissingMetricsAndApplyAspectRatio(Float aspectRatio, NullableArea retrievedArea,
                                                       Area area, UnitValue xObjectWidth, UnitValue xObjectHeight) {
        Area finalArea = new Area();
        if (!tryToApplyAspectRatio(retrievedArea, area, finalArea, aspectRatio)) {
            if (xObjectWidth != null && xObjectWidth.isPointValue() && retrievedArea.width == null) {
                area.width = xObjectWidth.getValue();
                retrievedArea.width = area.width;
                this.setProperty(Property.WIDTH, UnitValue.createPointValue(area.width));
            }
            if (xObjectHeight != null && xObjectHeight.isPointValue() && retrievedArea.height == null) {
                area.height = xObjectHeight.getValue();
                retrievedArea.height = area.height;
                this.setProperty(Property.HEIGHT, UnitValue.createPointValue(area.height));
            }
            boolean isAspectRatioApplied = tryToApplyAspectRatio(retrievedArea, area, area, aspectRatio);
            if (!isAspectRatioApplied && aspectRatio != null && retrievedArea.height == null) {
                // retrievedArea.width is also null here
                area.height = (float) (area.width / aspectRatio);
            }
            finalArea.width = area.width;
            finalArea.height = area.height;
        }
        return finalArea;
    }

    private static boolean tryToApplyAspectRatio(NullableArea retrievedArea, Area inputArea, Area resultArea,
                                                 Float aspectRatio) {

        if (aspectRatio == null) {
            return false;
        }
        if (retrievedArea.width == null && retrievedArea.height != null) {
            resultArea.height = inputArea.height;
            resultArea.width = (float) (inputArea.height * (float) aspectRatio);
            return true;
        } else if (retrievedArea.width != null && retrievedArea.height == null) {
            resultArea.width = inputArea.width;
            resultArea.height = (float) (inputArea.width / (float) aspectRatio);
            return true;
        }
        return false;
    }

    private static class NullableArea {
        public Float width;
        public Float height;

        public NullableArea(Float width, Float height) {
            this.width = width;
            this.height = height;
        }
    }

    private static class Area {
        public float width;
        public float height;

        public Area() {
            width = 0.0f;
            height = 0.0f;
        }

        public Area(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }
}
