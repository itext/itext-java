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
package com.itextpdf.pdfa;

import com.itextpdf.kernel.pdf.DocumentInfoHelper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;

/**
 * The class is helper which used for PDF/A document to properly configure PDF document's info dictionary.
 */
public class PdfADocumentInfoHelper extends DocumentInfoHelper {
    private final PdfDocument pdfDocument;

    /**
     * Instantiates a new {@link PdfADocumentInfoHelper} instance based on the document.
     *
     * @param pdfDocument the pdf document which will use that helper
     */
    public PdfADocumentInfoHelper(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean shouldAddDocumentInfoToTrailer() {
        if ("4".equals(pdfDocument.getConformance().getAConformance().getPart())) {
            // In case of PieceInfo == null don't add Info to trailer
            return pdfDocument.getCatalog().getPdfObject().get(PdfName.PieceInfo) != null;
        }
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void adjustDocumentInfo(PdfDocumentInfo documentInfo) {
        if ("4".equals(pdfDocument.getConformance().getAConformance().getPart())) {
            if (pdfDocument.getCatalog().getPdfObject().get(PdfName.PieceInfo) != null) {
                // Leave only ModDate as required by 6.1.3 File trailer of pdf/a-4 spec
                documentInfo.removeCreationDate();
            }
        }
    }
}
