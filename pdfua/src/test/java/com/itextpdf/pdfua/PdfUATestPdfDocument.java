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
package com.itextpdf.pdfua;

import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

/**
 * PdfDocument extension for testing purposes.
 */
public class PdfUATestPdfDocument extends PdfUADocument {

    public PdfUATestPdfDocument(PdfWriter writer) {
        super(writer, createConfig());
    }

    public PdfUATestPdfDocument(PdfWriter writer, DocumentProperties properties) {
        super(writer, properties, createConfig());
    }

    public PdfUATestPdfDocument(PdfReader reader, PdfWriter writer) {
        super(reader, writer, createConfig());
    }

    public PdfUATestPdfDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        super(reader, writer, properties, createConfig());
    }

    private static PdfUAConfig createConfig() {
        return new PdfUAConfig(PdfUAConformance.PDF_UA_1, "English pangram", "en-US");
    }
}
