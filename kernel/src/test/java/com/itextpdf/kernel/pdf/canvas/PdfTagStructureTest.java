package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagutils.AccessibleElementProperties;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.utils.CompareTool;
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

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfTagStructureTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfTagStructureTest/";

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
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPage(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        tagPointer.addTag(PdfName.P);
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        tagPointer.moveToParent().moveToParent();

        canvas.endText()
              .release();

        PdfPage page2 = document.addNewPage();
        tagPointer.setPage(page2);
        canvas = new PdfCanvas(page2);

        tagPointer.addTag(PdfName.P);
        canvas.beginText()
              .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);
        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        tagPointer.moveToParent().addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        canvas.endText()
              .release();
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
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPage(page);

        PdfCanvas canvas = new PdfCanvas(page);

        tagPointer.addTag(PdfName.P);
        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        AccessibleElementProperties properties = new AccessibleElementProperties();
        PdfDictionary attributes = new PdfDictionary();
        attributes.put(PdfName.O, new PdfString("random attributes"));
        attributes.put(new PdfName("hello"), new PdfString("world"));

        properties.setActualText("Actual text for span is: Hello World")
                .setLanguage("en-GB")
                .addAttributes(attributes);
        tagPointer.addTag(PdfName.Span).setProperties(properties);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        canvas.endText()
              .release();
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

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR).flushTag();
        tagPointer.moveToKid(3, PdfName.TR).moveToKid(PdfName.TD).flushTag();
        tagPointer.moveToParent().flushTag();

        String exceptionMessage = null;
        try {
            tagPointer.flushTag();
        } catch(PdfException e) {
            exceptionMessage = e.getMessage();
        }

        document.close();

        assertEquals(PdfException.CannotFlushDocumentRootTagBeforeDocumentIsClosed, exceptionMessage);
        compareResult("tagStructureFlushingTest01.pdf", "taggedDocument.pdf", "diffFlushing01_");
    }

    @Test
    public void tagStructureFlushingTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest02.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagStructureContext tagStructure = document.getTagStructureContext();
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
        assertFalse(kids.get(0).isFlushed());
        assertTrue(kids.getAsDictionary(0).getAsDictionary(PdfName.K).isFlushed());

        document.close();

        compareResult("tagStructureFlushingTest03.pdf", "taggedDocument.pdf", "diffFlushing03_");
    }

    @Test
    public void tagStructureFlushingTest04() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest04.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR).flushTag();
        // intended redundant call to flush page tags separately from page. Page tags are flushed when the page is flushed.
        document.getTagStructureContext().flushPageTags(document.getPage(1));
        document.getPage(1).flush();
        tagPointer.moveToKid(5).flushTag();
        document.getPage(2).flush();

        PdfArray kids = document.getStructTreeRoot().getKidsObject();
        assertFalse(kids.get(0).isFlushed());
        assertTrue(kids.getAsDictionary(0).getAsDictionary(PdfName.K).isFlushed());

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
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPage(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        tagPointer.addTag(PdfName.Div);

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
        tagPointer.addTag(paragraphElement, true);
        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        tagPointer.moveToParent().moveToParent();

        // Flushing /Div tag and it's children. /P tag shall not be flushed, as it is has connected paragraphElement
        // object. On removing connection between paragraphElement and /P tag, /P tag shall be flushed.
        // When tag is flushed, tagPointer begins to point to tag's parent. If parent is also flushed - to the root.
        tagPointer.flushTag();

        tagPointer.moveToTag(paragraphElement);
        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("again")
              .closeTag();

        tagPointer.removeConnectionToTag(paragraphElement);
        tagPointer.moveToRoot();

        canvas.endText()
              .release();

        PdfPage page2 = document.addNewPage();
        tagPointer.setPage(page2);
        canvas = new PdfCanvas(page2);

        tagPointer.addTag(PdfName.P);
        canvas.beginText()
              .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);
        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        tagPointer.moveToParent().addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        canvas.endText()
              .release();
        page1.flush();
        page2.flush();

        document.close();

        compareResult("tagStructureFlushingTest05.pdf", "cmp_tagStructureFlushingTest05.pdf", "diffFlushing05_");
    }

    @Test
    public void tagStructureRemovingTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest01.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);
        document.removePage(1);
        document.close();

        compareResult("tagStructureRemovingTest01.pdf", "cmp_tagStructureRemovingTest01.pdf", "diffRemoving01_");
    }

    @Test
    public void tagStructureRemovingTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest02.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        PdfPage firstPage = document.getPage(1);
        PdfPage secondPage = document.getPage(2);
        document.removePage(firstPage);
        document.removePage(secondPage);

        PdfPage page = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPage(page);

        PdfCanvas canvas = new PdfCanvas(page);

        tagPointer.addTag(PdfName.P);
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag()
              .endText();

        document.close();

        compareResult("tagStructureRemovingTest02.pdf", "cmp_tagStructureRemovingTest02.pdf", "diffRemoving02_");
    }

    @Test
    public void tagStructureRemovingTest03() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest03.pdf");
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPage(page);

        PdfCanvas canvas = new PdfCanvas(page);

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

        tagPointer.addTag(paragraphElement, true);
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag()
              .endText();

        tagPointer.moveToParent().moveToParent();

        document.removePage(1);

        PdfPage newPage = document.addNewPage();
        canvas = new PdfCanvas(newPage);
        tagPointer.setPage(newPage);

        tagPointer.moveToTag(paragraphElement).addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
                .beginText()
                .setFontAndSize(standardFont, 24)
                .setTextMatrix(1, 0, 0, 1, 32, 512)
                .showText("Hello.")
                .endText()
                .closeTag();

        document.close();

        compareResult("tagStructureRemovingTest03.pdf", "cmp_tagStructureRemovingTest03.pdf", "diffRemoving03_");
    }

    @Test
    public void tagStructureRemovingTest04() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAnnots.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest04.pdf").setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);
        document.removePage(1);
        document.close();

        compareResult("tagStructureRemovingTest04.pdf", "cmp_tagStructureRemovingTest04.pdf", "diffRemoving04_");
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
