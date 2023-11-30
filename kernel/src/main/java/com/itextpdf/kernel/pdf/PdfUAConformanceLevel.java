/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

/**
 * Implementation of {@link IConformanceLevel} interface for PDF/UA conformance level.
 * <p>
 *
 * PDF/UA is a conformance level for PDF files that ensures the files are accessible.
 * It contains an enumeration of all the PDF/UA conformance levels currently supported by iText.
 */
public class PdfUAConformanceLevel implements IConformanceLevel {

    /**
     * PDF/UA conformance level PDF/UA-1.
     */
    public static final PdfUAConformanceLevel PDFUA_1 = new PdfUAConformanceLevel(1);

    private final int version;


    /**
     * Creates a new {@link PdfUAConformanceLevel} instance.
     *
     * @param version the version of the PDF/UA conformance level
     */
    private PdfUAConformanceLevel(int version) {
        this.version = version;
    }

    /**
     * Get the version of the PDF/UA conformance level.
     *
     * @return the version of the PDF/UA conformance level
     */
    public int getVersion() {
        return version;
    }
}
