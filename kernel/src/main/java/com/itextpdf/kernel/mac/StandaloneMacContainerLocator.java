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
package com.itextpdf.kernel.mac;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Default {@link AbstractMacIntegrityProtector} location strategy, which locates MAC container in document's trailer.
 */
public class StandaloneMacContainerLocator implements IMacContainerLocator {
    private boolean macContainerLocated = false;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void locateMacContainer(AbstractMacIntegrityProtector macIntegrityProtector) {
        ((StandaloneMacIntegrityProtector) macIntegrityProtector).prepareDocument();
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
        return new StandaloneMacIntegrityProtector(document, macProperties);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document,
            PdfDictionary authDictionary) {
        return new StandaloneMacIntegrityProtector(document, authDictionary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMacValidationError(MacValidationException exception) {
        throw exception;
    }
}
