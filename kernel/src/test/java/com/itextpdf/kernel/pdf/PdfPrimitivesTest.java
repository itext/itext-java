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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Tag("IntegrationTest")
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

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void primitivesFloatNumberTest() throws IOException {
        String filename = "primitivesFloatNumberTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));
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
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.DIRECTONLY_OBJECT_CANNOT_BE_INDIRECT)})
    public void makeIndirectDirectOnlyPdfBoolean() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfBoolean t = PdfBoolean.valueOf(true);
        t.makeIndirect(pdfDoc);
    }

    @Test
    public void equalStrings() {
        PdfString a = (PdfString) new PdfString("abcd").makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfString b = new PdfString("abcd".getBytes(StandardCharsets.US_ASCII));
        Assertions.assertTrue(a.equals(b));

        PdfString c = new PdfString("abcd", "UTF-8");
        Assertions.assertFalse(c.equals(a));
    }

    @Test
    public void equalNumbers() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            document.addNewPage();

            PdfNumber num1 = (PdfNumber) new PdfNumber(1).makeIndirect(document);
            PdfNumber num2 = new PdfNumber(2);

            Assertions.assertFalse(num1.equals(num2));

            int hashCode = num1.hashCode();
            num1.increment();

            Assertions.assertTrue(num1.equals(num2));
            Assertions.assertNotEquals(hashCode, num1.hashCode());
        }

        PdfNumber a = new PdfNumber(1);
        PdfNumber aContent = new PdfNumber(a.getInternalContent());
        PdfNumber b = new PdfNumber(2);
        PdfNumber bContent = new PdfNumber(b.getInternalContent());

        Assertions.assertTrue(a.equals(aContent));
        Assertions.assertEquals(a.hashCode(), aContent.hashCode());
        Assertions.assertTrue(b.equals(bContent));
        Assertions.assertEquals(b.hashCode(), bContent.hashCode());
        Assertions.assertFalse(aContent.equals(bContent));
        Assertions.assertNotEquals(aContent.hashCode(), bContent.hashCode());

        aContent.increment();

        Assertions.assertFalse(a.equals(aContent));
        Assertions.assertNotEquals(a.hashCode(), aContent.hashCode());
        Assertions.assertTrue(aContent.equals(bContent));
        Assertions.assertEquals(aContent.hashCode(), bContent.hashCode());
    }

    @Test
    public void equalNames() {
        PdfName a = (PdfName) new PdfName("abcd").makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfName b = new PdfName("abcd");
        Assertions.assertTrue(a.equals(b));
    }

    @Test
    public void equalBoolean() {
        PdfBoolean f = (PdfBoolean) new PdfBoolean(false).makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        PdfBoolean t = new PdfBoolean(true);
        Assertions.assertFalse(f.equals(t));
        Assertions.assertTrue(f.equals(PdfBoolean.FALSE));
        Assertions.assertTrue(t.equals(PdfBoolean.TRUE));
    }

    @Test
    public void equalNulls() {
        PdfNull a = (PdfNull) new PdfNull().makeIndirect(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));;
        Assertions.assertTrue(a.equals(PdfNull.PDF_NULL));
    }

    @Test
    public void equalLiterals() {
        PdfLiteral a = new PdfLiteral("abcd");
        PdfLiteral b = new PdfLiteral("abcd".getBytes(StandardCharsets.US_ASCII));
        Assertions.assertTrue(a.equals(b));
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
