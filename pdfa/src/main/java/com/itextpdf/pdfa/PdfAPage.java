/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;

class PdfAPage extends PdfPage {

    PdfAPage(PdfDocument pdfDocument, PageSize pageSize) {
        super(pdfDocument, pageSize);
    }

    PdfAPage(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public void flush(boolean flushResourcesContentStreams) {
        // We check in advance whether this PdfAPage can be flushed and call the flush method only if it is.
        // This avoids processing actions that are invoked during flushing (for example, sending the END_PAGE event)
        // if the page is not actually flushed.
        if (flushResourcesContentStreams
                || ((PdfADocument) getDocument()).isClosing()
                || ((PdfADocument) getDocument()).checker.objectIsChecked(this.getPdfObject())) {
            super.flush(flushResourcesContentStreams);
        } else {
            ((PdfADocument) getDocument()).logThatPdfAPageFlushingWasNotPerformed();
        }
    }
}
