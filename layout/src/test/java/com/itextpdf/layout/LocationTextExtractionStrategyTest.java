/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.layout;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfTextArray;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Category(IntegrationTest.class)
public class LocationTextExtractionStrategyTest extends SimpleTextExtractionStrategyTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LocationTextExtractionStrategyTest/";

    @Override
    public ITextExtractionStrategy createRenderListenerForTest() {
        return new LocationTextExtractionStrategy();
    }

    @Test
    public void testYPosition() throws Exception {
        PdfDocument doc = createPdfWithOverlappingTextVertical(new String[]{"A", "B", "C", "D"}, new String[]{"AA", "BB", "CC", "DD"});

        String text = PdfTextExtractor.getTextFromPage(doc.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("A\nAA\nB\nBB\nC\nCC\nD\nDD", text);
    }

    @Test
    public void testXPosition() throws Exception {
        byte[] content = createPdfWithOverlappingTextHorizontal(new String[]{"A", "B", "C", "D"}, new String[]{"AA", "BB", "CC", "DD"});
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(content)));

        //TestResourceUtils.openBytesAsPdf(content);

        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("A AA B BB C CC D DD", text);
//        Assert.assertEquals("A\tAA\tB\tBB\tC\tCC\tD\tDD", text);
    }

    @Test
    public void testRotatedPage() throws Exception {
        byte[] bytes = createSimplePdf(new Rectangle(792, 612), "A\nB\nC\nD");

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));

        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("A \nB \nC \nD", text);
    }

    @Test
    public void testRotatedPage2() throws Exception {
        byte[] bytes = createSimplePdf(new Rectangle(792, 612), "A\nB\nC\nD");
        //TestResourceUtils.saveBytesToFile(bytes, new File("C:/temp/out.pdf"));

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));

        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("A \nB \nC \nD", text);
    }

    @Test
    public void testRotatedPage3() throws Exception {
        byte[] bytes = createSimplePdf(new Rectangle(792, 612), "A\nB\nC\nD");
        //TestResourceUtils.saveBytesToFile(bytes, new File("C:/temp/out.pdf"));

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));

        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("A \nB \nC \nD", text);
    }

    @Test
    public void testExtractXObjectTextWithRotation() throws Exception {
        //LocationAwareTextExtractingPdfContentRenderListener.DUMP_STATE = true;
        String text1 = "X";
        byte[] content = createPdfWithRotatedXObject(text1);
        //TestResourceUtils.saveBytesToFile(content, new File("C:/temp/out.pdf"));
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(content)));

        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());
        Assert.assertEquals("A\nB\nX\nC", text);
    }

    @Test
    public void testNegativeCharacterSpacing() throws Exception {
        byte[] content = createPdfWithNegativeCharSpacing("W", 200, "A");
        //TestResourceUtils.openBytesAsPdf(content);
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(content)));
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());
        Assert.assertEquals("WA", text);
    }

    @Test
    public void testSanityCheckOnVectorMath() {
        Vector start = new Vector(0, 0, 1);
        Vector end = new Vector(1, 0, 1);
        Vector antiparallelStart = new Vector(0.9f, 0, 1);
        Vector parallelStart = new Vector(1.1f, 0, 1);

        float rsltAntiParallel = antiparallelStart.subtract(end).dot(end.subtract(start).normalize());
        Assert.assertEquals(-0.1f, rsltAntiParallel, 0.0001);

        float rsltParallel = parallelStart.subtract(end).dot(end.subtract(start).normalize());
        Assert.assertEquals(0.1f, rsltParallel, 0.0001);

    }

    @Test
    public void testSuperscript() throws Exception {
        byte[] content = createPdfWithSupescript("Hel", "lo");
        //TestResourceUtils.openBytesAsPdf(content);
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(content)));
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());
        Assert.assertEquals("Hello", text);
    }

    @Test
    public void test01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "test01.pdf"));
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy());
        pdfDocument.close();
        String expectedText = "        We asked each candidate company to distribute to 225 \n" +
                "randomly selected employees the Great Place to Work \n" +
                "Trust Index. This employee survey was designed by the \n" +
                "Great Place to Work Institute of San Francisco to evaluate \n" +
                "trust in management, pride in work/company, and \n" +
                "camaraderie. Responses were returned directly to us. ";
        Assert.assertEquals(expectedText, text);
    }

    @Test
    public void testFontSpacingEqualsCharSpacing() throws Exception {
        byte[] content = createPdfWithFontSpacingEqualsCharSpacing();
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(content)));
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("Preface", text);
    }

    @Test
    public void testLittleFontSize() throws Exception {
        byte[] content = createPdfWithLittleFontSize();
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(content)));
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("Preface", text);
    }

    @Test
    public void testType3FontWithDifferences() throws IOException {
        String sourcePdf = sourceFolder + "DocumentWithType3FontWithDifferences.pdf";
        String comparedTextFile = sourceFolder + "textFromDocWithType3FontWithDifferences.txt";

        try (PdfDocument pdf = new PdfDocument(new PdfReader(sourcePdf))) {
            LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
            String result = PdfTextExtractor.getTextFromPage(pdf.getPage(1), strategy);

            PdfDictionary pdfType3FontDict = (PdfDictionary) pdf.getPdfObject(292);
            PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory.createFont(pdfType3FontDict);

            byte[] bytes = Files.readAllBytes(java.nio.file.Paths.get(comparedTextFile));

            Assert.assertEquals(new String(bytes, StandardCharsets.UTF_8), result);
            Assert.assertEquals(177, pdfType3Font.getNumberOfGlyphs());

            Assert.assertEquals("gA", pdfType3Font.getFontEncoding().getDifference(10));
            Assert.assertEquals(41, pdfType3Font.getFontProgram().getGlyphByCode(10).getUnicode());

            Assert.assertEquals(".notdef", pdfType3Font.getFontEncoding().getDifference(210));
            Assert.assertEquals(928, pdfType3Font.getFontProgram().getGlyphByCode(210).getUnicode());
        }
    }

    private byte[] createPdfWithNegativeCharSpacing(String str1, float charSpacing, String str2) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos).setCompressionLevel(0));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12);
        canvas.moveText(45, pdfDocument.getDefaultPageSize().getHeight() - 45);
        PdfTextArray ta = new PdfTextArray();
        ta.add(new PdfString(str1));
        ta.add(charSpacing);
        ta.add(new PdfString(str2));
        canvas.showText(ta);
        canvas.endText();

        pdfDocument.close();

        return baos.toByteArray();
    }

    private byte[] createPdfWithRotatedXObject(String xobjectText) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos).setCompressionLevel(0));
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("A"));
        document.add(new Paragraph("B"));

        PdfFormXObject template = new PdfFormXObject(new Rectangle(20, 100));
        PdfCanvas canvas = new PdfCanvas(template, pdfDocument);
        canvas.setStrokeColor(ColorConstants.GREEN).
                rectangle(0, 0, template.getWidth(), template.getHeight()).
                stroke();
        AffineTransform tx = new AffineTransform();
        tx.translate(0, template.getHeight());
        tx.rotate((float) (-90 / 180f * Math.PI));
        canvas.concatMatrix(tx).
                beginText().
                setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12).
                moveText(0, template.getWidth() - 12).
                showText(xobjectText).
                endText();

        document.add(new Image(template).setRotationAngle(Math.PI / 2)).
                add(new Paragraph("C"));
        document.close();

        return baos.toByteArray();
    }

    private byte[] createSimplePdf(Rectangle pageSize, String... text) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        final Document document = new Document(new PdfDocument(new PdfWriter(byteStream)), new PageSize(pageSize));
        for (String string : text) {
            document.add(new Paragraph(string));
            document.add(new AreaBreak());
        }

        document.close();

        return byteStream.toByteArray();
    }

    protected byte[] createPdfWithOverlappingTextHorizontal(String[] text1, String[] text2) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(baos).setCompressionLevel(0)));

        float ystart = 500;
        float xstart = 50;

        float x = xstart;
        float y = ystart;
        for (String text : text1) {
            doc.showTextAligned(text, x, y, TextAlignment.LEFT);
            x += 70.0f;
        }

        x = xstart + 12;
        y = ystart;
        for (String text : text2) {
            doc.showTextAligned(text, x, y, TextAlignment.LEFT);
            x += 70.0f;
        }

        doc.close();

        return baos.toByteArray();
    }

    private PdfDocument createPdfWithOverlappingTextVertical(String[] text1, String[] text2) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(baos).setCompressionLevel(0)));

        float ystart = 500;

        float x = 50;
        float y = ystart;
        for (String text : text1) {
            doc.showTextAligned(text, x, y, TextAlignment.LEFT);
            y -= 25.0f;
        }

        y = ystart - 13;
        for (String text : text2) {
            doc.showTextAligned(text, x, y, TextAlignment.LEFT);
            y -= 25.0f;
        }

        doc.close();

        return new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
    }

    private byte[] createPdfWithSupescript(String regularText, String superscriptText) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        final Document document = new Document(new PdfDocument(new PdfWriter(byteStream)));
        document.add(new Paragraph(regularText).add(new Text(superscriptText).setTextRise(7)));
        document.close();

        return byteStream.toByteArray();
    }

    private byte[] createPdfWithFontSpacingEqualsCharSpacing() throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        final PdfDocument document = new PdfDocument(new PdfWriter(byteStream));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        PdfFont font = PdfFontFactory.createFont();
        PdfTextArray textArray = new PdfTextArray();
        textArray.add(font.convertToBytes("P"));
        textArray.add(-226.2f);
        textArray.add(font.convertToBytes("r"));
        textArray.add(-231.8f);
        textArray.add(font.convertToBytes("e"));
        textArray.add(-230.8f);
        textArray.add(font.convertToBytes("f"));
        textArray.add(-238);
        textArray.add(font.convertToBytes("a"));
        textArray.add(-238.9f);
        textArray.add(font.convertToBytes("c"));
        textArray.add(-228.9f);
        textArray.add(font.convertToBytes("e"));
        float charSpace = font.getWidth(' ', 12);
        canvas
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .setCharacterSpacing(-charSpace)
                .showText(textArray)
                .endText()
                .restoreState();
        canvas.release();
        document.close();

        return byteStream.toByteArray();
    }

    private byte[] createPdfWithLittleFontSize() throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        final PdfDocument document = new PdfDocument(new PdfWriter(byteStream));
        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        PdfTextArray textArray = new PdfTextArray();
        PdfFont font = PdfFontFactory.createFont();
        textArray.add(font.convertToBytes("P"));
        textArray.add(1);
        textArray.add(font.convertToBytes("r"));
        textArray.add(1);
        textArray.add(font.convertToBytes("e"));
        textArray.add(1);
        textArray.add(font.convertToBytes("f"));
        textArray.add(1);
        textArray.add(font.convertToBytes("a"));
        textArray.add(1);
        textArray.add(font.convertToBytes("c"));
        textArray.add(1);
        textArray.add(font.convertToBytes("e"));
        canvas
                .saveState()
                .beginText()
                .setFontAndSize(font, 0.2f)
                .showText(textArray)
                .endText()
                .restoreState();
        canvas.release();
        document.close();

        return byteStream.toByteArray();
    }
}
