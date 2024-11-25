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
package com.itextpdf.svg.xobject;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.SvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.PdfRootSvgNodeRenderer;

/**
 * A wrapper for Form XObject for SVG images.
 */
public class SvgImageXObject extends PdfFormXObject {
    private final ISvgProcessorResult result;
    private final ResourceResolver resourceResolver;
    private boolean isGenerated = false;

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
     */
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    /**
     * Processes xObject before first image generation to avoid drawing it twice or more. It allows to reuse the same
     * Form XObject multiple times.
     *
     * @param document pdf that shall contain the SVG image.
     */
    public void generate(PdfDocument document) {
        if (!isGenerated) {
            PdfCanvas canvas = new PdfCanvas(this, document);
            SvgDrawContext context = new SvgDrawContext(resourceResolver, result.getFontProvider());
            if (result instanceof SvgProcessorResult) {
                context.setCssContext(((SvgProcessorResult) result).getContext().getCssContext());
            }
            context.setTempFonts(result.getTempFonts());
            context.addNamedObjects(result.getNamedObjects());
            context.pushCanvas(canvas);
            ISvgNodeRenderer root = new PdfRootSvgNodeRenderer(result.getRootRenderer());
            root.draw(context);
            isGenerated = true;
        }
    }
}
