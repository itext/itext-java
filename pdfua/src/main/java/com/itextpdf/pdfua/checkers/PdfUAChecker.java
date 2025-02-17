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

import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import org.slf4j.LoggerFactory;

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
}
