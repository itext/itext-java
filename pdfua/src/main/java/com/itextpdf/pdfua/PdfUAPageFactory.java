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
package com.itextpdf.pdfua;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.IPdfPageFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.pdfua.checkers.PdfUA1Checker;

/**
 * The class implements PDF page factory which is used for creating correct PDF/UA documents.
 */
class PdfUAPageFactory implements IPdfPageFactory {
    private final PdfUA1Checker checker;

    /**
     * Instantiates a new {@link PdfUAPageFactory} instance based on {@link PdfUA1Checker}.
     *
     * @param checker the PDF/UA checker
     */
    public PdfUAPageFactory(PdfUA1Checker checker) {
        this.checker = checker;
    }

    /**
     * @param pdfObject the {@link PdfDictionary} object on which the {@link PdfPage} will be based
     *
     * @return The pdf page.
     */
    @Override
    public PdfPage createPdfPage(PdfDictionary pdfObject) {
        return new PdfUAPage(pdfObject, checker);
    }

    /**
     * @param pdfDocument {@link PdfDocument} to add page
     * @param pageSize    {@link PageSize} of the created page
     *
     * @return The Pdf page.
     */
    @Override
    public PdfPage createPdfPage(PdfDocument pdfDocument, PageSize pageSize) {
        return new PdfUAPage(pdfDocument, pageSize, checker);
    }
}
