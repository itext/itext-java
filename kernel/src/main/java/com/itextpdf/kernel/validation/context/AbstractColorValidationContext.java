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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.validation.IValidationContext;

/**
 * Abstract class for color validation context.
 */
public abstract class AbstractColorValidationContext implements IValidationContext,
        IContentStreamValidationParameter, IGraphicStateValidationParameter {

    private final CanvasGraphicsState graphicsState;
    private final PdfDictionary currentColorSpaces;
    private final PdfStream contentStream;

    /**
     * Instantiates a new {@link AbstractColorValidationContext} based on graphic state, resources and content stream.
     *
     * @param graphicsState the graphical state
     * @param resources the resources
     * @param contentStream the content stream
     */
    protected AbstractColorValidationContext(CanvasGraphicsState graphicsState, PdfResources resources,
            PdfStream contentStream) {
        this.graphicsState = graphicsState;
        currentColorSpaces = resources == null ? null : resources.getPdfObject().getAsDictionary(PdfName.ColorSpace);
        this.contentStream = contentStream;
    }

    /**
     * Gets the current color space.
     *
     * @return the color space dictionary
     */
    public PdfDictionary getCurrentColorSpaces() {
        return currentColorSpaces;
    }

    @Override
    public CanvasGraphicsState getGraphicsState() {
        return graphicsState;
    }

    @Override
    public PdfStream getContentStream() {
        return contentStream;
    }
}
