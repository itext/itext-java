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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class for duplicate ID entry in structure element tree validation.
 */
public class DuplicateIdEntryValidationContext implements IValidationContext {
    private final PdfString id;

    /**
     * Instantiates a new {@link DuplicateIdEntryValidationContext} based on ID string.
     *
     * @param id the ID of the entry
     */
    public DuplicateIdEntryValidationContext(PdfString id) {
        this.id = id;
    }

    /**
     * Gets the ID of the entry.
     *
     * @return the ID
     */
    public PdfString getId() {
        return id;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.DUPLICATE_ID_ENTRY;
    }
}
