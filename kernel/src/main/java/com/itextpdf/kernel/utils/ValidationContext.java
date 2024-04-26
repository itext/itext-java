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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.Collection;

/**
 * This class is used to pass additional information to the {@link IValidationChecker} implementations.
 */
public class ValidationContext {

    private PdfDocument PdfDocument = null;
    private Collection<PdfFont> fonts = null;

    /**
     * Creates new {@link ValidationContext} instance.
     */
    public ValidationContext() {
    }

    /**
     * Sets {@link PdfDocument} for this {@link ValidationContext} instance.
     *
     * @param pdfDocument document to validate.
     *
     * @return this {@link ValidationContext} instance.
     */
    public ValidationContext withPdfDocument(PdfDocument pdfDocument) {
        this.PdfDocument = pdfDocument;
        return this;
    }

    /**
     * Sets fonts for this {@link ValidationContext} instance.
     *
     * @param fonts collection of the {@link PdfFont} fonts to validate.
     *
     * @return this {@link ValidationContext} instance.
     */
    public ValidationContext withFonts(Collection<PdfFont> fonts) {
        this.fonts = fonts;
        return this;
    }

    /**
     * Gets {@link PdfDocument} related to this {@link ValidationContext}.
     *
     * @return {@link PdfDocument} document to validate.
     */
    public PdfDocument getPdfDocument() {
        return PdfDocument;
    }

    /**
     * Gets fonts related to this {@link ValidationContext}.
     *
     * @return fonts to validate.
     */
    public Collection<PdfFont> getFonts() {
        return fonts;
    }
}
