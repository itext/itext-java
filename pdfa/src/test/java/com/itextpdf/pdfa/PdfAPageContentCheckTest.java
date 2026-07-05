/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfAPageContentCheckTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String PDFS_FOLDER = SOURCE_FOLDER + "pdfs/";

    @Test
    public void pageContentSplitAcrossStreamsTest() throws IOException {
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org",
                "sRGB IEC61966-2.1", FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm"));

        Assertions.assertDoesNotThrow(() -> {
            try (PdfDocument srcDoc = new PdfDocument(new PdfReader(PDFS_FOLDER + "pageContentSplitAcrossStreams.pdf"));
                    PdfADocument pdfADocument = new PdfADocument(new PdfWriter(new ByteArrayOutputStream()),
                            PdfAConformance.PDF_A_3B, outputIntent)) {
                srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), pdfADocument);
            }
        });
    }
}
