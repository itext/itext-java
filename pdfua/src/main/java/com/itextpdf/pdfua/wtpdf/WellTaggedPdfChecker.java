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
package com.itextpdf.pdfua.wtpdf;

import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.pdfua.checkers.PdfUA2Checker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * The class defines the requirements of the Well Tagged PDF standard.
 */
public class WellTaggedPdfChecker extends PdfUA2Checker {
    /**
     * Creates {@link WellTaggedPdfChecker} instance with PDF document which will be validated against WTPDF standard.
     *
     * @param pdfDocument the document to validate
     */
    public WellTaggedPdfChecker(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    /**
     * Checks that the {@code Catalog} dictionary of a conforming file contains the {@code Metadata} key whose value is
     * a metadata stream as defined in ISO 32000-2:2020.
     *
     * <p>
     * Checks that the {@code Metadata} stream as specified in ISO 32000-2:2020, 14.3 in the document catalog dictionary
     * includes a {@code dc: title} entry reflecting the title of the document.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    @Override
    protected void checkMetadata(PdfCatalog catalog) {
        PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.WELL_TAGGED_PDF_FOR_ACCESSIBILITY,
                msg -> new PdfUAConformanceException(msg));
        try {
            XMPMeta metadata = catalog.getDocument().getXmpMetadata();
            if (metadata.getProperty(XMPConst.NS_DC, XMPConst.TITLE) == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY);
            }
        } catch (XMPException e) {
            throw new PdfUAConformanceException(e.getMessage(), e);
        }
    }
}
