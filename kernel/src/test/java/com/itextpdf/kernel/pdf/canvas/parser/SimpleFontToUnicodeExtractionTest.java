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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class SimpleFontToUnicodeExtractionTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/SimpleFontToUnicodeExtractionTest/";

    @Test
    public void test01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleFontToUnicode01.pdf"));
        String expected = "Information plays a central role in soci-\n" +
                "ety today, and it is becoming more and \n" +
                "more common for that information to \n" +
                "be offered in digital form alone. The re-\n" +
                "liable, user-friendly Portable Document \n" +
                "Format (PDF) has become the world’s \n" +
                "file type of choice for providing infor-\n" +
                "mation as a digital document. \n" +
                "Tags can be added to a PDF in order \n" +
                "to structure the content of a document. \n" +
                "These tags are a critical requirement if \n" +
                "any form of assistive technology (such \n" +
                "as screen readers, specialist mice, and \n" +
                "speech recognition and text-to-speech \n" +
                "software) is to gain access to this con-\n" +
                "tent. To date, PDF documents have rare-\n" +
                "ly been tagged, and not all software can \n" +
                "make use of PDF tags. In practical terms, \n" +
                "this particularly reduces information‘s \n" +
                "accessibility for people with disabilities \n" +
                "who rely on assistive technology.";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new LocationTextExtractionStrategy());

        Assert.assertEquals(expected, actualText);
    }

    @Test
    public void test02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleFontToUnicode02.pdf"));
        String expected = "ffaast";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new LocationTextExtractionStrategy());

        Assert.assertEquals(expected, actualText);
    }

}
