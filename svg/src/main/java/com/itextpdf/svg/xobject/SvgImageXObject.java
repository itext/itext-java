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
package com.itextpdf.svg.xobject;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.SvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.PdfRootSvgNodeRenderer;
import com.itextpdf.svg.utils.SvgCssUtils;

/**
 * A wrapper for Form XObject for SVG images.
 */
public class SvgImageXObject extends PdfFormXObject {
    private final ISvgProcessorResult result;
    private final ResourceResolver resourceResolver;
    private boolean isGenerated = false;
    private boolean isCreatedByImg = false;
    private boolean isCreatedByObject = false;

    private float em;
    private SvgDrawContext svgDrawContext;
    private boolean isRelativeSized = false;

    /**
     * Creates a new instance of Form XObject for the SVG image.
     *
     * @param bBox             the form XObject’s bounding box.
     * @param result           processor result containing the SVG information.
     * @param resourceResolver {@link ResourceResolver} for the SVG image.
     */
    public SvgImageXObject(Rectangle bBox, ISvgProcessorResult result, ResourceResolver resourceResolver) {
        super(bBox);
        this.result = result;
        this.resourceResolver = resourceResolver;
        this.svgDrawContext = new SvgDrawContext(resourceResolver, result.getFontProvider());
    }

    /**
     * Creates a new instance of Form XObject for the relative sized SVG image.
     *
     * @param result processor result containing the SVG information
     * @param svgContext the svg draw context
     * @param em em value in pt
     * @param pdfDocument pdf that shall contain the SVG image, can be null
     */
    public SvgImageXObject(ISvgProcessorResult result, SvgDrawContext svgContext, float em, PdfDocument pdfDocument) {
        this(null, result, svgContext.getResourceResolver());
        if (pdfDocument != null) {
            svgContext.pushCanvas(new PdfCanvas(this, pdfDocument));
        }
        this.em = em;
        this.isRelativeSized = true;
        this.svgDrawContext = svgContext;
    }

    /**
     * Set if SVG image is created from HTML img tag context
     *
     * @param isCreatedByImg true if object is created from HTML img tag, false otherwise
     */
    public void setIsCreatedByImg(boolean isCreatedByImg) {
        this.isCreatedByImg = isCreatedByImg;
    }

    /**
     * Check if SVG image is created from HTML img tag context
     *
     * @return true if object is created from HTML img tag, false otherwise
     */
    public boolean isCreatedByImg() {
        return isCreatedByImg;
    }

    /**
     * Set if SVG image is created from HTML object tag context
     *
     * @param isCreatedByObject true if object is created from HTML object tag, false otherwise
     */
    public void setIsCreatedByObject(boolean isCreatedByObject) {
        this.isCreatedByObject = isCreatedByObject;
    }

    /**
     * Check if SVG image is created from HTML object tag context
     *
     * @return true if object is created from HTML object tag, false otherwise
     */
    public boolean isCreatedByObject() {
        return isCreatedByObject;
    }

    /**
     * If the SVG image is relative sized. This information
     * is used during image layouting to resolve it's relative size.
     *
     * @return {@code true} if the SVG image is relative sized, {@code false} otherwise
     *
     * @see #updateBBox(float, float)
     * @see #SvgImageXObject(ISvgProcessorResult, SvgDrawContext, float, PdfDocument)
     */
    @Override
    public boolean isRelativeSized() {
        return isRelativeSized;
    }

    /**
     * Returns processor result containing the SVG information.
     *
     * @return {ISvgProcessorResult} processor result.
     */
    public ISvgProcessorResult getResult() {
        return result;
    }

    /**
     * Returns resource resolver for the SVG image.
     *
     * @return {@link ResourceResolver} instance
     *
     * @deprecated not used anymore
     */
    @Deprecated
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    /**
     * Processes xObject before first image generation to avoid drawing it twice or more. It allows to reuse the same
     * Form XObject multiple times.
     *
     * @param document pdf that shall contain the SVG image, can be null if constructor
     *                 {@link #SvgImageXObject(ISvgProcessorResult, SvgDrawContext, float, PdfDocument)} was used
     */
    public void generate(PdfDocument document) {
        if (!isGenerated) {
            if (result instanceof SvgProcessorResult) {
                svgDrawContext.setCssContext(((SvgProcessorResult) result).getContext().getCssContext());
            }
            svgDrawContext.setTempFonts(result.getTempFonts());
            svgDrawContext.addNamedObjects(result.getNamedObjects());
            if (svgDrawContext.size() == 0) {
                svgDrawContext.pushCanvas(new PdfCanvas(this, document));
            }
            ISvgNodeRenderer root = new PdfRootSvgNodeRenderer(result.getRootRenderer());
            root.draw(svgDrawContext);
            isGenerated = true;
        }
    }

    /**
     * Updated XObject BBox for relative sized SVG image.
     *
     * @param areaWidth the area width where SVG image will be drawn
     * @param areaHeight the area height where SVG image will be drawn
     */
    public void updateBBox(float areaWidth, float areaHeight) {
        svgDrawContext.setCustomViewport(new Rectangle(areaWidth, areaHeight));
        Rectangle bbox = SvgCssUtils.extractWidthAndHeight(result.getRootRenderer(), em, svgDrawContext);
        setBBox(new PdfArray(bbox));
    }

    /**
     * Gets the SVG element width.
     *
     * @return the SVG element width
     */
    public UnitValue getElementWidth() {
        String widthStr = result.getRootRenderer().getAttribute(Attributes.WIDTH);
        return CssDimensionParsingUtils.parseLengthValueToPt(widthStr, em,
                svgDrawContext.getCssContext().getRootFontSize());
    }

    /**
     * Gets the SVG element height.
     *
     * @return the SVG element height
     */
    public UnitValue getElementHeight() {
        String heightStr = result.getRootRenderer().getAttribute(Attributes.HEIGHT);
        return CssDimensionParsingUtils.parseLengthValueToPt(heightStr, em,
                svgDrawContext.getCssContext().getRootFontSize());
    }
}
