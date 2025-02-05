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
package com.itextpdf.kernel.validation;


import com.itextpdf.kernel.pdf.PdfObject;

/**
 * Used to check if a PDF document is compliant to a specific validation profile.
 */
public interface IValidationChecker {
    /**
     * Validate the provided {@link IValidationContext}.
     *
     * @param validationContext the {@link IValidationContext} to validate
     */
    void validate(IValidationContext validationContext);

    /**
     * Is {@link PdfObject} ready to flush.
     *
     * @param object the pdf object to check
     *
     * @return {@code true} if the object is ready to flush, {@code false} otherwise
     */
    boolean isPdfObjectReadyToFlush(PdfObject object);
}
