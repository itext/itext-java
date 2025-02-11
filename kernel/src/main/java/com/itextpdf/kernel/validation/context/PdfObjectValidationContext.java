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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class for {@link PdfObject} validation context.
 */
public class PdfObjectValidationContext implements IValidationContext {
    private final PdfObject object;

    /**
     * Instantiates a new {@link PdfObjectValidationContext} based on pdf object.
     *
     * @param object the pdf object
     */
    public PdfObjectValidationContext(PdfObject object) {
        this.object = object;
    }

    /**
     * Gets the pdf object.
     *
     * @return the pdf object
     */
    public PdfObject getObject() {
        return object;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.PDF_OBJECT;
    }
}
