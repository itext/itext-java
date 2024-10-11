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
package com.itextpdf.kernel.pdf;

/**
 * PDF/UA is a conformance for PDF files that ensures the files are accessible to all users.
 * It contains an enumeration of all the PDF/UA conformance currently supported by iText.
 */
public enum PdfUAConformance {
    /** PDF/UA-1 conformance **/
    PDF_UA_1("1");

    private final String part;

    /**
     * Creates a new {@link PdfUAConformance} instance.
     *
     * @param part the part of the PDF/UA conformance
     */
    PdfUAConformance(String part) {
        this.part = part;
    }

    /**
     * Get the part of the PDF/UA conformance.
     *
     * @return the part of the PDF/UA conformance
     */
    public String getPart() {
        return this.part;
    }
}
