package com.itextpdf.canvas;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.tagutils.AccessibleElementProperties;
import com.itextpdf.core.pdf.tagutils.PdfTagStructure;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import java.io.FileOutputStream;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfTagStructureTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfTagStructureTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfTagStructureTest/";

    @BeforeClass
    static public void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void tagStructureTest01() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "tagStructureTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page = document.addNewPage();
        PdfTagStructure tagStructure = new PdfTagStructure(document);
        tagStructure.setPage(page);

        PdfCanvas canvas = new PdfCanvas(page);

        tagStructure.addTag(PdfName.P);
        canvas.beginText();
        PdfFont standardFont = PdfFont.createStandardFont(document, FontConstants.COURIER);
        canvas.setFontAndSize(standardFont, 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);

        tagStructure.addTag(PdfName.Span);

        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("Hello ");
        canvas.closeTag();

        canvas.setFontAndSize(standardFont, 30);
        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("World");
        canvas.closeTag();

        tagStructure.moveToParent().moveToParent();

        canvas.endText();
        canvas.release();
        page.flush();

        page = document.addNewPage();
        tagStructure.setPage(page);
        canvas = new PdfCanvas(page);

        tagStructure.addTag(PdfName.P);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(document, FontConstants.HELVETICA), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        tagStructure.addTag(PdfName.Span);

        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("Hello ");
        canvas.closeTag();

        tagStructure.moveToParent().addTag(PdfName.Span);

        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("World");
        canvas.closeTag();

        canvas.endText();
        canvas.release();
        page.flush();

        document.close();
    }

    @Test
    public void tagStructureTest02() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "tagStructureTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page = document.addNewPage();
        PdfTagStructure tagStructure = new PdfTagStructure(document);
        tagStructure.setPage(page);

        PdfCanvas canvas = new PdfCanvas(page);

        tagStructure.addTag(PdfName.P);
        canvas.beginText();
        PdfFont standardFont = PdfFont.createStandardFont(document, FontConstants.COURIER);
        canvas.setFontAndSize(standardFont, 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);

        AccessibleElementProperties properties = new AccessibleElementProperties();
        PdfDictionary attributes = new PdfDictionary();
        attributes.put(PdfName.O, new PdfString("random attributes"));
        attributes.put(new PdfName("hello"), new PdfString("world"));

        properties.setActualText("Actual text for span is: Hello World")
                .setLanguage("en-GB")
                .addAttributes(attributes);
        tagStructure.addTag(PdfName.Span).setProperties(properties);

        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("Hello ");
        canvas.closeTag();

        canvas.setFontAndSize(standardFont, 30);
        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("World");
        canvas.closeTag();

        canvas.endText();
        canvas.release();
        page.flush();

        document.close();
    }
}
