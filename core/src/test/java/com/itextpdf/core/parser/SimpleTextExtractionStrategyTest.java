package com.itextpdf.core.parser;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.geom.AffineTransform;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfTextArray;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.model.Document;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.Paragraph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTextExtractionStrategyTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/core/parser/SimpleTextExtractionStrategyTest/";

    String TEXT1 = "TEXT1 TEXT1";
    String TEXT2 = "TEXT2 TEXT2";

    public TextExtractionStrategy createRenderListenerForTest(){
        return new SimpleTextExtractionStrategy();
    }

    @Test
    public void testCoLinnearText() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 0, false, 0);

        Assert.assertEquals(TEXT1 + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testCoLinnearTextWithSpace() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 0, false, 2);
        //saveBytesToFile(bytes, new File("c:/temp/test.pdf"));

        Assert.assertEquals(TEXT1 + " " + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testCoLinnearTextEndingWithSpaceCharacter() throws Exception{
        // in this case, we shouldn't be inserting an extra space
        TEXT1 = TEXT1 + " ";
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 0, false, 2);

        //TestResourceUtils.openBytesAsPdf(bytes);

        Assert.assertEquals(TEXT1 + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }
    @Test
    public void testUnRotatedText() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 0, true, -20);

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }


    @Test
    public void testRotatedText() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, -90, true, -20);

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testRotatedText2() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 90, true, -20);
        //TestResourceUtils.saveBytesToFile(bytes, new File("C:/temp/out.pdf"));

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testPartiallyRotatedText() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1, TEXT2, 33, true, -20);

        Assert.assertEquals(TEXT1 + "\n" + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));

    }

    @Test
    public void testWordSpacingCausedByExplicitGlyphPositioning() throws Exception{
        byte[] bytes = createPdfWithArrayText(TEXT1, TEXT2, 250);

        Assert.assertEquals(TEXT1 + " " + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }


    @Test
    public void testWordSpacingCausedByExplicitGlyphPositioning2() throws Exception{
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

        Assert.assertEquals("San Diego Chapter", TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }


    @Test
    public void testTrailingSpace() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1 + " ", TEXT2, 0, false, 6);

        Assert.assertEquals(TEXT1 + " " + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testLeadingSpace() throws Exception{
        byte[] bytes = createPdfWithRotatedText(TEXT1, " " + TEXT2, 0, false, 6);

        Assert.assertEquals(TEXT1 + " " + TEXT2, TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest()));
    }

    @Test
    public void testExtractXObjectText() throws Exception {
        String text1 = "X";
        byte[] bytes = createPdfWithXObject(text1);
        String text = TextExtractor.getTextFromPage(new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes))).getPage(1),
                createRenderListenerForTest());
        Assert.assertTrue("extracted text (" + text + ") must contain '" + text1 + "'", text.contains(text1));
    }

    @Test
    public void extractFromPage229() throws IOException {
        if (this.getClass() != SimpleTextExtractionStrategyTest.class)
            return;
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "page229.pdf"));
        String text1 = TextExtractor.getTextFromPage(pdfDocument.getPage(1), new SimpleTextExtractionStrategy());
        String text2 = TextExtractor.getTextFromPage(pdfDocument.getPage(1), new GlyphTextEventListener(new SimpleTextExtractionStrategy()));
        pdfDocument.close();
        Assert.assertEquals(text1, text2);
    }

    @Test
    public void extractFromIsoTc171() throws IOException {
        if (this.getClass() != SimpleTextExtractionStrategyTest.class)
            return;
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf"));
        String text1 = TextExtractor.getTextFromPage(pdfDocument.getPage(1), new SimpleTextExtractionStrategy()) +
                "\n" +
                TextExtractor.getTextFromPage(pdfDocument.getPage(2), new SimpleTextExtractionStrategy());
        String text2 = TextExtractor.getTextFromPage(pdfDocument.getPage(1), new GlyphTextEventListener(new SimpleTextExtractionStrategy())) +
                "\n" +
                TextExtractor.getTextFromPage(pdfDocument.getPage(2), new GlyphTextEventListener(new SimpleTextExtractionStrategy()));
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
                setFontAndSize(PdfFont.createStandardFont(FontConstants.HELVETICA), 12).
                moveText(5, template.getHeight() - 5).
                showText(xobjectText).
                endText();

        document.add(new Image(template));

        document.add(new Paragraph("C"));

        document.close();

        return byteStream.toByteArray();
    }

    private static byte[] createPdfWithArrayText(PdfTextArray textArray) throws Exception{
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        PdfDocument document = new PdfDocument(new PdfWriter(byteStream));
        document.setDefaultPageSize(new PageSize(612, 792));

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        PdfFont font = PdfFont.createStandardFont(FontConstants.HELVETICA);

        canvas.beginText().
                setFontAndSize(font, 12);

        canvas.showText(textArray);

        canvas.endText();

        document.close();

        return byteStream.toByteArray();

    }

    private static byte[] createPdfWithArrayText(String text1, String text2, int spaceInPoints) throws Exception{
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
        PdfFont font = PdfFont.createStandardFont(FontConstants.HELVETICA);

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
