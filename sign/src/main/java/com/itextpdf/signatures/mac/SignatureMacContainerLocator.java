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
package com.itextpdf.signatures.mac;

import com.itextpdf.kernel.mac.IMacContainerLocator;
import com.itextpdf.kernel.mac.AbstractMacIntegrityProtector;
import com.itextpdf.kernel.mac.MacProperties;
import com.itextpdf.kernel.mac.MacValidationException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * {@link IMacContainerLocator} strategy, which should be used specifically in case of signature creation.
 * This strategy locates MAC container in signature unsigned attributes.
 */
public class SignatureMacContainerLocator implements IMacContainerLocator {
    private boolean macContainerLocated = false;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void locateMacContainer(AbstractMacIntegrityProtector macIntegrityProtector) {
        ((SignatureMacIntegrityProtector) macIntegrityProtector).prepareDocument();
        macContainerLocated = true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isMacContainerLocated() {
        return macContainerLocated;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document,
            MacProperties macProperties) {
        return new SignatureMacIntegrityProtector(document, macProperties);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document,
            PdfDictionary authDictionary) {
        return new SignatureMacIntegrityProtector(document, authDictionary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMacValidationError(MacValidationException exception) {
        throw exception;
    }
}
