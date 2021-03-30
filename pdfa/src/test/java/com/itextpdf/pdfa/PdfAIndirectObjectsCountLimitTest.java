/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa;

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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfAIndirectObjectsCountLimitTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void validAmountOfIndirectObjectsTest() throws IOException {
        PdfA1Checker testChecker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B) {
            @Override
            protected long getMaxNumberOfIndirectObjects() {
                return 10;
            }
        };

        try (
                InputStream icm = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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

        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_NUMBER_OF_INDIRECT_OBJECTS_EXCEEDED);

        PdfA1Checker testChecker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B) {
            @Override
            protected long getMaxNumberOfIndirectObjects() {
                return 9;
            }
        };

        try (
                InputStream icm = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
                OutputStream fos = new ByteArrayOutputStream();
                Document document = new Document(new PdfADocument(new PdfWriter(fos),
                        PdfAConformanceLevel.PDF_A_1B,
                        getOutputIntent(icm)));
        ) {
            PdfADocument pdfa = (PdfADocument) document.getPdfDocument();
            pdfa.checker = testChecker;
            document.add(buildContent());

            // generated document contains exactly 10 indirect objects. Given 9 is the allowed
            // limit per "mock specification" conformance exception should be thrown as the limit
            // is exceeded
        }
    }

    @Test
    public void invalidAmountOfIndirectObjectsAppendModeTest() throws IOException {

        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.MAXIMUM_NUMBER_OF_INDIRECT_OBJECTS_EXCEEDED);

        PdfA1Checker testChecker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1B) {
            @Override
            protected long getMaxNumberOfIndirectObjects() {
                return 11;
            }
        };

        try (
                InputStream fis = new FileInputStream(sourceFolder + "pdfs/pdfa10IndirectObjects.pdf");
                OutputStream fos = new ByteArrayOutputStream();
                PdfADocument pdfa = new PdfADocument(new PdfReader(fis), new PdfWriter(fos), new StampingProperties().useAppendMode())

        ) {
            pdfa.checker = testChecker;
            pdfa.addNewPage();
            // during closing of pdfa object exception will be thrown as new document will contain
            // 12 indirect objects and limit per "mock specification" conformance will be exceeded
        }
    }

    private Paragraph buildContent() throws IOException {
        PdfFontFactory.register(sourceFolder + "FreeSans.ttf",sourceFolder + "FreeSans.ttf");
        PdfFont font = PdfFontFactory.createFont(
                sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);
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
