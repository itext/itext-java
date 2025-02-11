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
package com.itextpdf.kernel.mac;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Strategy interface, which is responsible for {@link AbstractMacIntegrityProtector} container location.
 * Expected to be used in {@link com.itextpdf.commons.utils.DIContainer}.
 */
public interface IMacContainerLocator {
    /**
     * Locates {@link AbstractMacIntegrityProtector} container.
     *
     * @param macIntegrityProtector {@link AbstractMacIntegrityProtector} container to be located
     */
    void locateMacContainer(AbstractMacIntegrityProtector macIntegrityProtector);

    /**
     * Indicates, if MAC container was already located.
     *
     * @return {@code true} if MAC container was already located, {@code false} otherwise
     */
    boolean isMacContainerLocated();

    /**
     * Creates {@link AbstractMacIntegrityProtector} from explicitly provided MAC properties.
     *
     * @param document {@link PdfDocument} for which MAC container shall be created
     * @param macProperties {@link MacProperties} to be used for MAC container creation
     *
     * @return {@link AbstractMacIntegrityProtector} which specific implementation depends on interface implementation.
     */
    AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document, MacProperties macProperties);

    /**
     * Creates {@link AbstractMacIntegrityProtector} from already existing AuthCode dictionary.
     *
     * @param document {@link PdfDocument} for which MAC container shall be created
     * @param authDictionary AuthCode {@link PdfDictionary} which contains MAC related information
     *
     * @return {@link AbstractMacIntegrityProtector} which specific implementation depends on interface implementation.
     */
    AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document, PdfDictionary authDictionary);

    /**
     * Handles MAC validation error.
     *
     * @param exception {@link MacValidationException} to handle.
     */
    void handleMacValidationError(MacValidationException exception);
}
