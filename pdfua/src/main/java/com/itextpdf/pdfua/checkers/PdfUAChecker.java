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
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * An abstract class that will run through all necessary checks defined in the different PDF/UA standards. A number of
 * common checks are executed in this class, while standard-dependent specifications are implemented in the available
 * subclasses. The standard that is followed is the series of ISO 14289 specifications, currently generations 1 and 2.
 *
 * <p>
 * While it is possible to subclass this method and implement its abstract methods in client code, this is not
 * encouraged and will have little effect. It is not possible to plug custom implementations into iText, because
 * iText should always refuse to create non-compliant PDF/UA, which would be possible with client code implementations.
 * Any future generations of the PDF/UA standard and its derivatives will get their own implementation in the iText -
 * pdfua project.
 */
public abstract class PdfUAChecker implements IValidationChecker {

    static final Function<String, PdfException> EXCEPTION_SUPPLIER = (msg) -> new PdfUAConformanceException(msg);

    private boolean warnedOnPageFlush = false;

    /**
     * Creates new {@link PdfUAChecker} instance.
     */
    protected PdfUAChecker() {
        // Empty constructor.
    }

    /**
     * Logs a warn on page flushing that page flushing is disabled in PDF/UA mode.
     */
    public void warnOnPageFlush() {
        if (!warnedOnPageFlush) {
            LoggerFactory.getLogger(PdfUAChecker.class).warn(PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED);
            warnedOnPageFlush = true;
        }
    }

    /**
     * Checks that the default natural language for content and text strings is specified using the {@code Lang}
     * entry, with a nonempty value, in the document catalog dictionary.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    void checkLang(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        PdfObject lang = catalogDict.get(PdfName.Lang);
        if (!(lang instanceof PdfString)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.CATALOG_SHOULD_CONTAIN_LANG_ENTRY);
        }
        if (((PdfString) lang).getValue().isEmpty()) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY);
        }
    }

    /**
     * Checks that the {@code ViewerPreferences} dictionary of the document catalog dictionary is present and contains
     * at least the {@code DisplayDocTitle} key with a value of {@code true}, as defined in
     * ISO 32000-1:2008, 12.2, Table 150 or ISO 32000-2:2020, Table 147.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    void checkViewerPreferences(PdfCatalog catalog) {
        PdfDictionary viewerPreferences = catalog.getPdfObject().getAsDictionary(PdfName.ViewerPreferences);
        if (viewerPreferences == null) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES);
        }
        PdfObject displayDocTitle = viewerPreferences.get(PdfName.DisplayDocTitle);
        if (!(displayDocTitle instanceof PdfBoolean)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES);
        }
        if (PdfBoolean.FALSE.equals(displayDocTitle)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.VIEWER_PREFERENCES_IS_FALSE);
        }
    }
}
