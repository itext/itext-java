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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class for stroke canvas color validation context.
 */
public class StrokeColorValidationContext extends AbstractColorValidationContext {
    /**
     * Instantiates a new {@link StrokeColorValidationContext} based on graphics state, resources and content stream.
     *
     * @param canvasGraphicsState the canvas graphics state
     * @param resources the resources
     * @param stream the content stream
     */
    public StrokeColorValidationContext(CanvasGraphicsState canvasGraphicsState,
            PdfResources resources, PdfStream stream) {
        super(canvasGraphicsState, resources, stream);
    }

    @Override
    public ValidationType getType() {
        return ValidationType.STROKE_COLOR;
    }
}
