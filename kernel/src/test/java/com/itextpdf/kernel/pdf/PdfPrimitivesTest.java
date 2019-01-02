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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Category(IntegrationTest.class)
public class PdfPrimitivesTest extends ExtendedITextTest{

    static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfPrimitivesTest/";
    static final PdfName TestArray = new PdfName("TestArray");
    static final int DefaultArraySize = 64;
    static final int PageCount = 1000;

    public static class RandomString {

        private static final char[] symbols;
        private final Random random = new Random();
        private final char[] buf;

        static {
            StringBuilder tmp = new StringBuilder();
            for (char ch = 'A'; ch <= 'Z'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }

    @Before
    public void setup() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void primitivesFloatNumberTest() throws IOException {
        String filename = "primitivesFloatNumberTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfArray array = generatePdfArrayWithFloatNumbers(null, false);
            page.getPdfObject().put(TestArray, array);
            array.flush();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesIntNumberTest() throws IOException {
        String filename = "primitivesIntNumberTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfArray array = generatePdfArrayWithIntNumbers(null, false);
            page.getPdfObject().put(TestArray, array);
            array.flush();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesNameTest() throws IOException {
        String filename = "primitivesNameTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfArray array = generatePdfArrayWithNames(null, false);
            page.getPdfObject().put(TestArray, array);
            array.flush();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesStringTest() throws IOException {
        String filename = "primitivesStringTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfArray array = generatePdfArrayWithStrings(null, false);
            page.getPdfObject().put(TestArray, array);
            array.flush();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesBooleanTest() throws IOException {
        String filename = "primitivesBooleanTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(TestArray, generatePdfArrayWithBooleans(null, false));
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesFloatNumberIndirectTest() throws IOException {
        String filename = "primitivesFloatNumberIndirectTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(TestArray, generatePdfArrayWithFloatNumbers(pdfDoc, true));
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesIntNumberIndirectTest() throws IOException {
        String filename = "primitivesIntNumberIndirectTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(TestArray, generatePdfArrayWithIntNumbers(pdfDoc, true));
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesStringIndirectTest() throws IOException {
        String filename = "primitivesStringIndirectTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(TestArray, generatePdfArrayWithStrings(pdfDoc, true));
            page.flush();
        }
        pdfDoc.close();
    }



    @Test
    public void primitivesNameIndirectTest() throws IOException {
        String filename = "primitivesNameIndirectTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(TestArray, generatePdfArrayWithNames(pdfDoc, true));
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void primitivesBooleanIndirectTest() throws IOException {
        String filename = "primitivesBooleanIndirectTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            page.getPdfObject().put(TestArray, generatePdfArrayWithBooleans(pdfDoc, true));
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void pdfNamesTest() {
        RandomString rnd = new RandomString(16);
        for (int i = 0; i < 10000000; i++) {
            new PdfName(rnd.nextString());
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = com.itextpdf.io.LogMessageConstant.DIRECTONLY_OBJECT_CANNOT_BE_INDIRECT)})
    public void makeIndirectDirectOnlyPdfBoolean() throws IOException{
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfBoolean t = PdfBoolean.valueOf(true);
        t.makeIndirect(pdfDoc);
    }

    @Test
    public void equalStrings() {
        PdfString a = (PdfString) new PdfString("abcd").makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfString b = new PdfString("abcd".getBytes(StandardCharsets.US_ASCII));
        Assert.assertTrue(a.equals(b));

        PdfString c = new PdfString("abcd", "UTF-8");
        Assert.assertFalse(c.equals(a));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CALCULATE_HASHCODE_FOR_MODIFIED_PDFNUMBER)
    })
    public void equalNumbers() {
        PdfNumber num1 = (PdfNumber) new PdfNumber(1).makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfNumber num2 = new PdfNumber(2);

        Assert.assertFalse(num1.equals(num2));

        int hashCode = num1.hashCode();
        num1.increment();

        Assert.assertTrue(num1.equals(num2));
        Assert.assertNotEquals(hashCode, num1.hashCode());
    }

    @Test
    public void equalNames() {
        PdfName a = (PdfName) new PdfName("abcd").makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfName b = new PdfName("abcd");
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void equalBoolean() {
        PdfBoolean f = (PdfBoolean) new PdfBoolean(false).makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfBoolean t = new PdfBoolean(true);
        Assert.assertFalse(f.equals(t));
        Assert.assertTrue(f.equals(PdfBoolean.FALSE));
        Assert.assertTrue(t.equals(PdfBoolean.TRUE));
    }

    @Test
    public void equalNulls() {
        PdfNull a = (PdfNull) new PdfNull().makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));;
        Assert.assertTrue(a.equals(PdfNull.PDF_NULL));
    }

    @Test
    public void equalLiterals() {
        PdfLiteral a = new PdfLiteral("abcd");
        PdfLiteral b = new PdfLiteral("abcd".getBytes(StandardCharsets.US_ASCII));
        Assert.assertTrue(a.equals(b));
    }

    private PdfArray generatePdfArrayWithFloatNumbers(PdfDocument doc, boolean indirects) {
        PdfArray array = (PdfArray) new PdfArray().makeIndirect(doc);
        Random rnd = new Random();
        for (int i = 0; i < DefaultArraySize; i++) {
            PdfNumber num = new PdfNumber(rnd.nextFloat());
            if (indirects)
                num.makeIndirect(doc);
            array.add(num);
        }
        return array;
    }

    private PdfArray generatePdfArrayWithIntNumbers(PdfDocument doc, boolean indirects) {
        PdfArray array = (PdfArray) new PdfArray().makeIndirect(doc);
        Random rnd = new Random();
        for (int i = 0; i < DefaultArraySize; i++) {
            array.add(new PdfNumber(rnd.nextInt()).makeIndirect(indirects ? doc : null));
        }
        return array;
    }

    private PdfArray generatePdfArrayWithStrings(PdfDocument doc, boolean indirects) {
        PdfArray array = (PdfArray) new PdfArray().makeIndirect(doc);
        RandomString rnd = new RandomString(16);
        for (int i = 0; i < DefaultArraySize; i++) {
            array.add(new PdfString(rnd.nextString()).makeIndirect(indirects ? doc : null));
        }
        return array;
    }

    private PdfArray generatePdfArrayWithNames(PdfDocument doc, boolean indirects) {
        PdfArray array = (PdfArray) new PdfArray().makeIndirect(doc);
        RandomString rnd = new RandomString(6);
        for (int i = 0; i < DefaultArraySize; i++) {
            array.add(new PdfName(rnd.nextString()).makeIndirect(indirects ? doc : null));
        }
        return array;
    }

    private PdfArray generatePdfArrayWithBooleans(PdfDocument doc, boolean indirects) {
        PdfArray array = (PdfArray) new PdfArray().makeIndirect(doc);
        Random rnd = new Random();
        for (int i = 0; i < DefaultArraySize; i++) {
            array.add(new PdfBoolean(rnd.nextBoolean()).makeIndirect(indirects ? doc : null));
        }
        return array;
    }
}
