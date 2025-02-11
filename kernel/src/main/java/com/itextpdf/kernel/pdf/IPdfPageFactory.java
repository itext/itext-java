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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.geom.PageSize;

/**
 * Interface used to create instances of {@link PdfPage}.
 */
public interface IPdfPageFactory {

    /**
     * Create {@link PdfPage} on the base of the page {@link PdfDictionary}.
     * @param pdfObject the {@link PdfDictionary} object on which the {@link PdfPage} will be based
     * @return created {@link PdfPage}
     */
    PdfPage createPdfPage(PdfDictionary pdfObject);

    /**
     * Create {@link PdfPage} with given page size and add it to the {@link PdfDocument}.
     * @param pdfDocument  {@link PdfDocument} to add page
     * @param pageSize {@link PageSize} of the created page
     * @return created {@link PdfPage}
     */
    PdfPage createPdfPage(PdfDocument pdfDocument, PageSize pageSize);
}
