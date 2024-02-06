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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;

/**
 * Used to check if a PDF document is compliant to a specific validation profile.
 */
public interface IValidationChecker {

    /**
     * Validate the provided {@link ValidationContext}.
     * <p>
     *
     * This method is called by the {@link PdfDocument#close()} to check for additional conformance requirements.
     *
     * @param validationContext the {@link ValidationContext} to validate
     */
    void validateDocument(ValidationContext validationContext);

    /**
     * Check the provided object for conformance.
     * <p>
     *
     * This method is called by the
     * {@link PdfDocument#checkIsoConformance(Object, IsoKey, PdfResources, PdfStream, Object)}
     * to check for additional conformance requirements.
     *
     * @param obj           the object to check
     * @param key           the {@link IsoKey} of the object
     * @param resources     the {@link PdfResources} of the object
     * @param contentStream the {@link PdfStream} of the object
     * @param extra         additional information
     */
    void validateObject(Object obj, IsoKey key, PdfResources resources, PdfStream contentStream,
            Object extra);
}
