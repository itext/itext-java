/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfTextExtractorEncodingsTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/PdfTextExtractorEncodingsTest/";

    /**
     * Basic Latin characters, with Unicode values less than 128
     */
    private static final String TEXT1 = "AZaz09*!";
    /**
     * Latin-1 characters
     */
    private static final String TEXT2 = "\u0027\u0060\u00a4\u00a6";

    /**
     * Test parsing a document which uses a standard non-embedded font.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testStandardFont() throws Exception {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    /**
     * Test parsing a document which uses a font encoding which creates a /Differences
     * PdfArray in the PDF.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testEncodedFont() throws Exception {
        PdfFont font = getTTFont("ISO-8859-1", true);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    /**
     * Test parsing a document which uses a Unicode font encoding which creates a /ToUnicode
     * PdfArray.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testUnicodeFont() throws Exception {
        PdfFont font = getTTFont(PdfEncodings.IDENTITY_H, true);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    private void checkPdf(byte[] pdfBytes) throws Exception {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)));
        // Characters from http://unicode.org/charts/PDF/U0000.pdf
        Assert.assertEquals(TEXT1, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        // Characters from http://unicode.org/charts/PDF/U0080.pdf
        Assert.assertEquals(TEXT2, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(2)));
    }

    protected static PdfFont getTTFont(String encoding, boolean embedded) throws IOException {
        return PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", encoding, embedded);
    }

    private static byte[] createPdf(PdfFont font)
            throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        Document document = new Document(new PdfDocument(new PdfWriter(byteStream)));
        document.add(new Paragraph(TEXT1).setFont(font));
        document.add(new AreaBreak());
        document.add(new Paragraph(TEXT2).setFont(font));
        document.close();

        return byteStream.toByteArray();
    }
}
