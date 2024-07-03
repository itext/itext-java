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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfAIndirectObjectsCountLimitTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test
    public void validAmountOfIndirectObjectsTest() throws IOException {
        PdfA1Checker testChecker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B) {
            @Override
            protected long getMaxNumberOfIndirectObjects() {
                return 10;
            }
        };

        try (
                InputStream icm = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
                OutputStream fos = new ByteArrayOutputStream();
                Document document = new Document(new PdfADocument(new PdfWriter(fos),
                        PdfAConformanceLevel.PDF_A_1B,
                        getOutputIntent(icm)));
        ) {
            PdfADocument pdfa = (PdfADocument) document.getPdfDocument();
            pdfa.checker = testChecker;
            document.add(buildContent());

            // generated document contains exactly 10 indirect objects. Given 10 is the allowed
            // limit per "mock specification" conformance exception shouldn't be thrown
        }
    }

    @Test
    public void invalidAmountOfIndirectObjectsTest() throws IOException {
        PdfA1Checker testChecker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B) {
            @Override
            protected long getMaxNumberOfIndirectObjects() {
                return 9;
            }
        };

        try (
                InputStream icm = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
                OutputStream fos = new ByteArrayOutputStream();
        ) {
            Document document = new Document(new PdfADocument(new PdfWriter(fos),
                    PdfAConformanceLevel.PDF_A_1B,
                    getOutputIntent(icm)));
            PdfADocument pdfa = (PdfADocument) document.getPdfDocument();
            pdfa.checker = testChecker;
            document.add(buildContent());

            // generated document contains exactly 10 indirect objects. Given 9 is the allowed
            // limit per "mock specification" conformance exception should be thrown as the limit
            // is exceeded
            Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> document.close());
            Assertions.assertEquals(PdfaExceptionMessageConstant.MAXIMUM_NUMBER_OF_INDIRECT_OBJECTS_EXCEEDED, e.getMessage());
        }
    }

    @Test
    public void invalidAmountOfIndirectObjectsAppendModeTest() throws IOException {
        PdfA1Checker testChecker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B) {
            @Override
            protected long getMaxNumberOfIndirectObjects() {
                return 11;
            }
        };

        try (
                InputStream fis = FileUtil.getInputStreamForFile(sourceFolder + "pdfs/pdfa10IndirectObjects.pdf");
                OutputStream fos = new ByteArrayOutputStream();
        ) {
            PdfADocument pdfa = new PdfADocument(new PdfReader(fis), new PdfWriter(fos), new StampingProperties().useAppendMode());
            pdfa.checker = testChecker;
            pdfa.addNewPage();

            // during closing of pdfa object exception will be thrown as new document will contain
            // 12 indirect objects and limit per "mock specification" conformance will be exceeded
            Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfa.close());
            Assertions.assertEquals(PdfaExceptionMessageConstant.MAXIMUM_NUMBER_OF_INDIRECT_OBJECTS_EXCEEDED, e.getMessage());
        }
    }

    private Paragraph buildContent() throws IOException {
        PdfFontFactory.register(sourceFolder + "FreeSans.ttf",sourceFolder + "FreeSans.ttf");
        PdfFont font = PdfFontFactory.createFont(
                sourceFolder + "FreeSans.ttf", PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        Paragraph p = new Paragraph(UUID.randomUUID().toString());
        p.setMinWidth(1e6f);
        p.setFont(font);

        return p;
    }

    private PdfOutputIntent getOutputIntent(InputStream inputStream) {
        return new PdfOutputIntent("Custom", "",
                "http://www.color.org", "sRGB ICC preference", inputStream);
    }
}
