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
package com.itextpdf.pdfa;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.IPdfPageFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.pdfa.checker.PdfAChecker;

/**
 * The class implements PDF page factory which is used for creating correct PDF/A documents.
 */
public class PdfAPageFactory implements IPdfPageFactory {
    private final PdfAChecker checker;

    /**
     * Instantiates a new {@link PdfAPageFactory} instance based on {@link PdfAChecker}.
     *
     * @param checker the PDF/A checker
     */
    public PdfAPageFactory(PdfAChecker checker) {
        this.checker = checker;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PdfPage createPdfPage(PdfDictionary pdfObject) {
        return new PdfAPage(pdfObject, checker);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PdfPage createPdfPage(PdfDocument pdfDocument, PageSize pageSize) {
        return new PdfAPage(pdfDocument, pageSize, checker);
    }
}
