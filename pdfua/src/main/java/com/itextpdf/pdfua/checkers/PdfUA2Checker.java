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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.layout.validation.context.LayoutValidationContext;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2HeadingsChecker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * The class defines the requirements of the PDF/UA-2 standard and contains
 * method implementations from the abstract {@link PdfUAChecker} class.
 *
 * <p>
 * The specification implemented by this class is ISO 14289-2.
 */
public class PdfUA2Checker extends PdfUAChecker {

    private final PdfDocument pdfDocument;
    private final PdfUAValidationContext context;
    private final PdfUA2HeadingsChecker headingsChecker;

    /**
     * Creates {@link PdfUA2Checker} instance with PDF document which will be validated against PDF/UA-2 standard.
     *
     * @param pdfDocument the document to validate
     */
    public PdfUA2Checker(PdfDocument pdfDocument) {
        super();
        this.pdfDocument = pdfDocument;
        this.context = new PdfUAValidationContext(this.pdfDocument);
        this.headingsChecker = new PdfUA2HeadingsChecker(this.context);
    }

    @Override
    public void validate(IValidationContext context) {
        switch (context.getType()) {
            case PDF_DOCUMENT:
                PdfDocumentValidationContext pdfDocContext = (PdfDocumentValidationContext) context;
                checkCatalog(pdfDocContext.getPdfDocument().getCatalog());
                checkStructureTreeRoot(pdfDocContext.getPdfDocument().getStructTreeRoot());
                break;
            case LAYOUT:
                LayoutValidationContext layoutContext = (LayoutValidationContext) context;
                headingsChecker.checkLayoutElement(layoutContext.getRenderer());
                break;
        }
    }

    @Override
    public boolean isPdfObjectReadyToFlush(PdfObject object) {
        return true;
    }

    /**
     * Checks that the {@code Catalog} dictionary of a conforming file contains the {@code Metadata} key whose value is
     * a metadata stream as defined in ISO 32000-2:2020. Also checks that the value of {@code pdfuaid:part} is 2 for
     * conforming PDF files and validates required {@code pdfuaid:rev} value.
     *
     * <p>
     * Checks that the {@code Metadata} stream as specified in ISO 32000-2:2020, 14.3 in the document catalog dictionary
     * includes a {@code dc: title} entry reflecting the title of the document.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    protected void checkMetadata(PdfCatalog catalog) {
        try {
            PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2);
            XMPMeta metadata = catalog.getDocument().getXmpMetadata();
            if (metadata.getProperty(XMPConst.NS_DC, XMPConst.TITLE) == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY);
            }
        } catch (PdfException | XMPException e) {
            throw new PdfUAConformanceException(e.getMessage());
        }
    }

    /**
     * Validates document catalog dictionary against PDF/UA-2 standard.
     *
     * <p>
     * For now, only {@code Metadata} and {@code ViewerPreferences} are checked.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary to check
     */
    private void checkCatalog(PdfCatalog catalog) {
        checkMetadata(catalog);
        checkViewerPreferences(catalog);
    }

    /**
     * Validates structure tree root dictionary against PDF/UA-2 standard.
     *
     * <p>
     * For now, only headings check is performed.
     *
     * @param structTreeRoot {@link PdfStructTreeRoot} structure tree root dictionary to check
     */
    private void checkStructureTreeRoot(PdfStructTreeRoot structTreeRoot) {
        TagTreeIterator tagTreeIterator = new TagTreeIterator(structTreeRoot);
        tagTreeIterator.addHandler(new PdfUA2HeadingsChecker.PdfUA2HeadingHandler(context));
        tagTreeIterator.traverse();
    }
}
