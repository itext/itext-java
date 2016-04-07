package com.itextpdf.layout;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfTextArray;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LocationTextExtractionStrategyTest extends SimpleTextExtractionStrategyTest {

    @Override
    public TextExtractionStrategy createRenderListenerForTest() {
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

        Assert.assertEquals("A\nB\nC\nD", text);
    }

    @Test
    public void testRotatedPage2() throws Exception {
        byte[] bytes = createSimplePdf(new Rectangle(792, 612), "A\nB\nC\nD");
        //TestResourceUtils.saveBytesToFile(bytes, new File("C:/temp/out.pdf"));

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));

        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("A\nB\nC\nD", text);
    }

    @Test
    public void testRotatedPage3() throws Exception {
        byte[] bytes = createSimplePdf(new Rectangle(792, 612), "A\nB\nC\nD");
        //TestResourceUtils.saveBytesToFile(bytes, new File("C:/temp/out.pdf"));

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));

        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), createRenderListenerForTest());

        Assert.assertEquals("A\nB\nC\nD", text);
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

    private byte[] createPdfWithNegativeCharSpacing(String str1, float charSpacing, String str2) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos).setCompressionLevel(0));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);
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
        canvas.setStrokeColor(Color.GREEN).
                rectangle(0, 0, template.getWidth(), template.getHeight()).
                stroke();
        AffineTransform tx = new AffineTransform();
        tx.translate(0, template.getHeight());
        tx.rotate((float) (-90 / 180f * Math.PI));
        canvas.concatMatrix(tx).
                beginText().
                setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12).
                moveText(0, template.getWidth() - 12).
                showText(xobjectText).
                endText();

        document.add(new Image(template).setRotationAngle(Math.PI / 2)).
                add(new Paragraph("C"));
        document.close();

        return baos.toByteArray();
    }

    private byte[] createSimplePdf(Rectangle pageSize, final String... text) throws Exception {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        final Document document = new Document(new PdfDocument(new PdfWriter(byteStream)), new PageSize(pageSize));
        for (String string : text) {
            document.add(new Paragraph(string));
            document.add(new AreaBreak());
        }

        document.close();

        return byteStream.toByteArray();
    }

    protected byte[] createPdfWithOverlappingTextHorizontal(String[] text1, String[] text2) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(baos).setCompressionLevel(0)));

        float ystart = 500;
        float xstart = 50;

        float x = xstart;
        float y = ystart;
        for (String text : text1) {
            doc.showTextAligned(text, x, y, Property.TextAlignment.LEFT);
            x += 70.0;
        }

        x = xstart + 12;
        y = ystart;
        for (String text : text2) {
            doc.showTextAligned(text, x, y, Property.TextAlignment.LEFT);
            x += 70.0;
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
            doc.showTextAligned(text, x, y, Property.TextAlignment.LEFT);
            y -= 25.0;
        }

        y = ystart - 13;
        for (String text : text2) {
            doc.showTextAligned(text, x, y, Property.TextAlignment.LEFT);
            y -= 25.0;
        }

        doc.close();

        return new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
    }

    private byte[] createPdfWithSupescript(String regularText, String superscriptText) throws Exception {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        final Document document = new Document(new PdfDocument(new PdfWriter(byteStream)));
        document.add(new Paragraph(regularText).add(new Text(superscriptText).setTextRise(7)));
        document.close();

        return byteStream.toByteArray();
    }

}
