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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.InputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfALongStringTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfALongStringTest/";
    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis condimentum, tortor sit amet fermentum pharetra, sem felis finibus enim, vel consectetur nunc justo at nisi. In hac habitasse platea dictumst. Donec quis suscipit eros. Nam urna purus, scelerisque in placerat in, convallis vel sapien. Suspendisse sed lacus sit amet orci ornare vulputate. In hac habitasse platea dictumst. Ut eu aliquet felis, at consectetur neque.";
    private static final int STRING_LENGTH_LIMIT = 32767;

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void runTest() throws Exception {
        String file = "pdfALongString.pdf";
        String filename = destinationFolder + file;
        try (InputStream icm = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
                OutputStream fos = FileUtil.getFileOutputStream(filename)) {
            Document document = new Document(new PdfADocument(new PdfWriter(fos), PdfAConformance.PDF_A_3U,
                    new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB ICC preference", icm))
            );
            StringBuilder stringBuilder = new StringBuilder(LOREM_IPSUM);
            while (stringBuilder.length() < STRING_LENGTH_LIMIT) {
                stringBuilder.append(stringBuilder.toString());
            }
            PdfFontFactory.register(sourceFolder + "FreeSans.ttf", sourceFolder + "FreeSans.ttf");
            PdfFont font = PdfFontFactory.createFont(
                    sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
            Paragraph p = new Paragraph(stringBuilder.toString());
            p.setMinWidth(1e6f);
            p.setFont(font);
            document.add(p);

            // when document is closing, ISO conformance check is performed
            // this document contain a string which is longer than it is allowed
            // per specification. That is why conformance exception should be thrown
            Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> document.close());
            Assertions.assertEquals(PdfaExceptionMessageConstant.PDF_STRING_IS_TOO_LONG, e.getMessage());
        }
    }
}
