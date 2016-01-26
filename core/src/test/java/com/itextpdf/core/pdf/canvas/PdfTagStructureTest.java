package com.itextpdf.core.pdf.canvas;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.tagging.IPdfStructElem;
import com.itextpdf.core.pdf.tagging.PdfStructElem;
import com.itextpdf.core.pdf.tagutils.AccessibleElementProperties;
import com.itextpdf.core.pdf.tagutils.IAccessibleElement;
import com.itextpdf.core.pdf.tagutils.PdfTagStructure;
import com.itextpdf.core.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class PdfTagStructureTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfTagStructureTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/PdfTagStructureTest/";

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

        PdfPage page1 = document.addNewPage();
        PdfTagStructure tagStructure = new PdfTagStructure(document);
        tagStructure.setPage(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        tagStructure.addTag(PdfName.P);
        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createStandardFont(FontConstants.COURIER);
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

        PdfPage page2 = document.addNewPage();
        tagStructure.setPage(page2);
        canvas = new PdfCanvas(page2);

        tagStructure.addTag(PdfName.P);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 24);
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
        page1.flush();
        page2.flush();

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
        PdfFont standardFont = PdfFontFactory.createStandardFont(FontConstants.COURIER);
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

        compareResult("tagStructureTest01.pdf", "cmp_tagStructureTest01.pdf", "diff01_");
    }

    @Test
    public void tagStructureFlushingTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest01.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        PdfTagStructure tagStructure = document.getTagStructure();
        tagStructure.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR).flushTag();
        tagStructure.moveToKid(3, PdfName.TR).moveToKid(PdfName.TD).flushTag();
        tagStructure.moveToParent().moveToParent().flushTag();

        document.close();

        compareResult("tagStructureFlushingTest01.pdf", "taggedDocument.pdf", "diffFlushing01_");
    }

    @Test
    public void tagStructureFlushingTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest02.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        PdfTagStructure tagStructure = document.getTagStructure();
        tagStructure.flushPageTags(document.getPage(1));

        List<IPdfStructElem> kids = document.getStructTreeRoot().getKids();
        assertTrue(!((PdfStructElem)kids.get(0)).getPdfObject().isFlushed());
        assertTrue(!((PdfStructElem)kids.get(0).getKids().get(0)).getPdfObject().isFlushed());
        PdfArray rowsTags = (PdfArray) ((PdfStructElem) kids.get(0).getKids().get(0)).getK();
        assertTrue(rowsTags.get(0).isFlushed());

        document.close();

        compareResult("tagStructureFlushingTest02.pdf", "taggedDocument.pdf", "diffFlushing02_");
    }

    @Test
    public void tagStructureFlushingTest03() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest03.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        document.getPage(2).flush();
        document.getPage(1).flush();

        PdfArray kids = document.getStructTreeRoot().getKidsObject();
        assertTrue(kids.get(0).isFlushed());

        document.close();

        compareResult("tagStructureFlushingTest03.pdf", "taggedDocument.pdf", "diffFlushing03_");
    }

    @Test
    public void tagStructureFlushingTest04() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest04.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        document.getTagStructure().moveToKid(PdfName.Table).moveToKid(2, PdfName.TR).flushTag();
        document.getTagStructure().flushPageTags(document.getPage(1));
        document.getPage(1).flush();
        document.getTagStructure().moveToKid(5).flushTag();
        document.getPage(2).flush();

        PdfArray kids = document.getStructTreeRoot().getKidsObject();
        assertTrue(kids.get(0).isFlushed());

        document.close();

        compareResult("tagStructureFlushingTest04.pdf", "taggedDocument.pdf", "diffFlushing04_");
    }

    @Test
    public void tagStructureFlushingTest05() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest05.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page1 = document.addNewPage();
        PdfTagStructure tagStructure = new PdfTagStructure(document);
        tagStructure.setPage(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        tagStructure.addTag(PdfName.Div);

        //TODO refactor after the implementation of getting IAccessibleElement from current tag in TagStructure
        IAccessibleElement paragraphElement = new IAccessibleElement() {
            @Override
            public PdfName getRole() {
                return PdfName.P;
            }

            @Override
            public void setRole(PdfName role) {
            }

            @Override
            public AccessibleElementProperties getAccessibilityProperties() {
                return null;
            }
        };
        tagStructure.addTag(paragraphElement, true);
        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createStandardFont(FontConstants.COURIER);
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

        // Flushing /Div tag and it's children. /P tag shall not be flushed, as it is has connected paragraphElement
        // object. On removing connection between paragraphElement and /P tag, /P tag shall be flushed.
        // When tag is flushed, tagStructure begins to point to tag's parent. If parent is also flushed - to the root.
        tagStructure.flushTag();

        tagStructure.moveToTag(paragraphElement);
        tagStructure.addTag(PdfName.Span);

        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("Hello ");
        canvas.closeTag();

        canvas.setFontAndSize(standardFont, 30);
        canvas.openTag(tagStructure.getTagReference());
        canvas.showText("again");
        canvas.closeTag();

        tagStructure.removeConnectionToTag(paragraphElement);
        tagStructure.moveToRoot();

        canvas.endText();
        canvas.release();

        PdfPage page2 = document.addNewPage();
        tagStructure.setPage(page2);
        canvas = new PdfCanvas(page2);

        tagStructure.addTag(PdfName.P);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createStandardFont(FontConstants.HELVETICA), 24);
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
        page1.flush();
        page2.flush();

        document.close();

        compareResult("tagStructureFlushingTest05.pdf", "cmp_tagStructureFlushingTest05.pdf", "diffFlushing05_");
    }

    private void compareResult(String outFileName, String cmpFileName, String diffNamePrefix)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String outPdf = destinationFolder + outFileName;
        String cmpPdf = sourceFolder + cmpFileName;

        String contentDifferences = compareTool.compareByContent(outPdf,
                cmpPdf, destinationFolder, diffNamePrefix);
        String taggedStructureDifferences = compareTool.compareTagStructures(outPdf, cmpPdf);

        String errorMessage = "";
        errorMessage += taggedStructureDifferences == null ? "" : taggedStructureDifferences + "\n";
        errorMessage += contentDifferences == null ? "" : contentDifferences;
        if (!errorMessage.isEmpty()) {
            fail(errorMessage);
        }
    }
}
