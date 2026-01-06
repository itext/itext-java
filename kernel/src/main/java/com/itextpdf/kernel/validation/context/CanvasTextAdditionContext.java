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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class which contains context in which text was added to canvas.
 */
public class CanvasTextAdditionContext implements IValidationContext {
    private final String text;
    private PdfNumber mcId;
    private final PdfDictionary attributes;
    private final PdfStream contentStream;

    /**
     * Creates {@link CanvasTextAdditionContext} instance.
     *
     * @param text text which was added to canvas
     * @param attributes {@link PdfDictionary} attributes which correspond to this text
     * @param contentStream {@link PdfStream} in which text is written
     */
    public CanvasTextAdditionContext(String text, PdfDictionary attributes, PdfStream contentStream) {
        this.text = text;
        this.attributes = attributes;
        this.contentStream = contentStream;
        if (attributes != null) {
            this.mcId = attributes.getAsNumber(PdfName.MCID);
        }
    }

    /**
     * Gets text which was added to canvas.
     *
     * @return text which was added to canvas
     */
    public String getText() {
        return text;
    }

    /**
     * Gets {@link PdfNumber} which represents MCID of this text.
     *
     * @return {@link PdfNumber} which represents MCID of this text
     */
    public PdfNumber getMcId() {
        return mcId;
    }

    /**
     * Gets {@link PdfDictionary} attributes which correspond to the added text.
     *
     * @return {@link PdfDictionary} attributes which correspond to the added text
     */
    public PdfDictionary getAttributes() {
        return attributes;
    }

    /**
     * Returns {@link PdfStream} on which text is written.
     *
     * @return {@link PdfStream} on which text is written
     */
    public PdfStream getContentStream() {
        return contentStream;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.CANVAS_TEXT_ADDITION;
    }
}
