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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfTextArray;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.Image;
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
public class SimpleTextExtractionStrategyTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/SimpleTextExtractionStrategyTest/";

    String TEXT1 = "TEXT1 TEXT1";
    String TEXT2 = "TEXT2 TEXT2";

    public ITextExtractionStrategy createRenderListenerForTest() {
        return new SimpleTextExtractionStrategy();
    }

    @Test
    public void testCoLinnearText() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 0, false, 0);

        Assert.assertEquals(TEXT1 + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testCoLinnearTextWithSpace() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 0, false, 2);
        //saveBytesToFile(bytes, new File("c:/temp/test.pdf"));

        Assert.assertEquals(TEXT1 + " " + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testCoLinnearTextEndingWithSpaceCharacter() throws Exception {
        // in this case, we shouldn't be inserting an extra space
        byte[] bytes = createPdfWithRotatedText(TEXT1 + " ", TEXT2, 0, false, 2);

        //TestResourceUtils.openBytesAsPdf(bytes);

        Assert.assertEquals(TEXT1 + " " + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testUnRotatedText() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 0, true, -20);

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testRotatedText() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, -90, true, -20);

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testRotatedText2() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 90, true, -20);
        //TestResourceUtils.saveBytesToFile(bytes, new File("C:/temp/out.pdf"));

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testPartiallyRotatedText() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 33, true, -20);

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testWordSpacingCausedByExplicitGlyphPositioning() throws Exception {
        byte[] bytes = createPdfWithArrayText(TEXT1, TEXT2, 250);

        Assert.assertEquals(TEXT1 + " " + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }


    @Test
    public void testWordSpacingCausedByExplicitGlyphPositioning2() throws Exception {
        PdfTextArray textArray = new PdfTextArray();
        textArray.add(new PdfString("S"));
        textArray.add(3.2f);
        textArray.add(new PdfString("an"));
        textArray.add(-255);
        textArray.add(new PdfString("D"));
        textArray.add(13);
        textArray.add(new PdfString("i"));
        textArray.add(8.3f);
        textArray.add(new PdfString("e"));
        textArray.add(-10.1f);
        textArray.add(new PdfString("g"));
        textArray.add(1.6f);
        textArray.add(new PdfString("o"));
        textArray.add(-247.5f);
        textArray.add(new PdfString("C"));
        textArray.add(2.4f);
        textArray.add(new PdfString("h"));
        textArray.add(5.8f);
        textArray.add(new PdfString("ap"));
        textArray.add(3);
        textArray.add(new PdfString("t"));
        textArray.add(10.7f);
        textArray.add(new PdfString("er"));

        byte[] bytes = createPdfWithArrayText(textArray);

        Assert.assertEquals("San Diego Chapter", PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }


    @Test
    public void testTrailingSpace() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1 + " ", TEXT2, 0, false, 6);

        Assert.assertEquals(TEXT1 + " " + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testLeadingSpace() throws Exception {
        byte[] bytes = createPdfWithRotatedText(TEXT1, " " + TEXT2, 0, false, 6);

        Assert.assertEquals(TEXT1 + " " + TEXT2, PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testExtractXObjectText() throws Exception {
        String text1 = "X";
        byte[] bytes = createPdfWithXObject(text1);
        String text = PdfTextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest());
        Assert.assertTrue("extracted text (" + text + ") must contain '" + text1 + "'", text.contains(text1));
    }

    @Test
    public void extractFromPage229() throws IOException {
        if (this.getClass() != SimpleTextExtractionStrategyTest.class)
            return;
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "page229.pdf"));
        String text1 = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new SimpleTextExtractionStrategy());
        String text2 = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new GlyphTextEventListener(new SimpleTextExtractionStrategy()));
        pdfDocument.close();
        Assert.assertEquals(text1, text2);
    }

    @Test
    public void extractFromIsoTc171() throws IOException {
        if (this.getClass() != SimpleTextExtractionStrategyTest.class)
            return;
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf"));
        String text1 = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new SimpleTextExtractionStrategy()) +
                "\n" +
                PdfTextExtractor.getTextFromPage(pdfDocument.getPage(2), new SimpleTextExtractionStrategy());
        String text2 = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new GlyphTextEventListener(new SimpleTextExtractionStrategy())) +
                "\n" +
                PdfTextExtractor.getTextFromPage(pdfDocument.getPage(2), new GlyphTextEventListener(new SimpleTextExtractionStrategy()));
        pdfDocument.close();
        Assert.assertEquals(text1, text2);
    }


    byte[] createPdfWithXObject(String xobjectText) throws Exception {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(byteStream).setCompressionLevel(0));
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("A"));
        document.add(new Paragraph("B"));

        PdfFormXObject template = new PdfFormXObject(new Rectangle(100, 100));
        new PdfCanvas(template, pdfDocument).
                beginText().
                setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12).
                moveText(5, template.getHeight() - 5).
                showText(xobjectText).
                endText();

        document.add(new Image(template));

        document.add(new Paragraph("C"));

        document.close();

        return byteStream.toByteArray();
    }

    private static byte[] createPdfWithArrayText(PdfTextArray textArray) throws Exception {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        PdfDocument document = new PdfDocument(new PdfWriter(byteStream));
        document.setDefaultPageSize(new PageSize(612, 792));

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        canvas.beginText().
                setFontAndSize(font, 12);

        canvas.showText(textArray);

        canvas.endText();

        document.close();

        return byteStream.toByteArray();

    }

    private static byte[] createPdfWithArrayText(String text1, String text2, int spaceInPoints) throws Exception {
        PdfTextArray textArray = new PdfTextArray();
        textArray.add(new PdfString(text1));
        textArray.add(-spaceInPoints);
        textArray.add(new PdfString(text2));
        return createPdfWithArrayText(textArray);
    }

    private static byte[] createPdfWithRotatedText(String text1, String text2, float rotation, boolean moveTextToNextLine, float moveTextDelta) throws Exception {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        PdfDocument document = new PdfDocument(new PdfWriter(byteStream));
        document.setDefaultPageSize(new PageSize(612, 792));

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        float x = document.getDefaultPageSize().getWidth() / 2;
        float y = document.getDefaultPageSize().getHeight() / 2;

        canvas.concatMatrix(AffineTransform.getTranslateInstance(x, y));

        canvas.moveTo(-10, 0).
                lineTo(10, 0).
                moveTo(0, -10).
                lineTo(0, 10).
                stroke();

        canvas.beginText().
                setFontAndSize(font, 12).
                concatMatrix(AffineTransform.getRotateInstance((float) (rotation / 180f * Math.PI))).
                showText(text1);
        if (moveTextToNextLine)
            canvas.moveText(0, moveTextDelta);
        else
            canvas.concatMatrix(AffineTransform.getTranslateInstance(moveTextDelta, 0));
        canvas.showText(text2);
        canvas.endText();

        document.close();

        return byteStream.toByteArray();
    }

}
