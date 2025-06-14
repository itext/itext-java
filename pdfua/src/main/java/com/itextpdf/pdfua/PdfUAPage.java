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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.pdfua.checkers.PdfUA1Checker;
import com.itextpdf.pdfua.checkers.PdfUAChecker;

class PdfUAPage extends PdfPage {
    private final PdfUAChecker checker;

    /**
     * Creates new {@link PdfUAPage} instance.
     *
     * @param pdfObject the {@link PdfDictionary} object on which the {@link PdfUAPage} will be based
     * @param checker {@link PdfUA1Checker} to check the requirements of the PDF/UA-1 standard
     *
     * @deprecated in favour of {@link #PdfUAPage(PdfDictionary, PdfUAChecker)}
     */
    @Deprecated
    protected PdfUAPage(PdfDictionary pdfObject, PdfUA1Checker checker) {
        super(pdfObject);
        this.checker = checker;
    }

    /**
     * Creates new {@link PdfUAPage} instance.
     *
     * @param pdfDocument the {@link PdfDocument} object which will contain the {@link PdfUAPage}
     * @param pageSize {@link PageSize} the size of the {@link PdfUAPage}
     * @param checker {@link PdfUA1Checker} to check the requirements of the PDF/UA-1 standard
     *
     * @deprecated in favour of {@link #PdfUAPage(PdfDocument, PageSize, PdfUAChecker)}
     */
    @Deprecated
    protected PdfUAPage(PdfDocument pdfDocument, PageSize pageSize, PdfUA1Checker checker) {
        super(pdfDocument, pageSize);
        this.checker = checker;
    }

    /**
     * Creates new {@link PdfUAPage} instance.
     *
     * @param pdfObject the {@link PdfDictionary} object on which the {@link PdfUAPage} will be based
     * @param checker {@link PdfUAChecker} to check the requirements of the PDF/UA standard
     */
    protected PdfUAPage(PdfDictionary pdfObject, PdfUAChecker checker) {
        super(pdfObject);
        this.checker = checker;
    }

    /**
     * Creates new {@link PdfUAPage} instance.
     *
     * @param pdfDocument the {@link PdfDocument} object which will contain the {@link PdfUAPage}
     * @param pageSize {@link PageSize} the size of the {@link PdfUAPage}
     * @param checker {@link PdfUAChecker} to check the requirements of the PDF/UA standard
     */
    protected PdfUAPage(PdfDocument pdfDocument, PageSize pageSize, PdfUAChecker checker) {
        super(pdfDocument, pageSize);
        this.checker = checker;
    }

    @Override
    public void flush(boolean flushResourcesContentStreams) {
        if (getDocument().isClosing()) {
            super.flush(flushResourcesContentStreams);
        } else {
            checker.warnOnPageFlush();
        }
    }
}
