package com.itextpdf.core.pdf.canvas;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.tagging.PdfMcr;
import com.itextpdf.core.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.core.pdf.tagging.PdfMcrNumber;
import com.itextpdf.core.pdf.tagging.PdfObjRef;
import com.itextpdf.core.pdf.tagging.PdfStructElem;
import com.itextpdf.core.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class PdfTaggingTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfTaggingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/PdfTaggingTest/";

    @BeforeClass
    static public void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void taggingTest01() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page1));

        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(page1, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.HELVETICA), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page2, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page2, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        page1.flush();
        page2.flush();

        document.close();

        compareResult("taggingTest01.pdf", "cmp_taggingTest01.pdf", "diff01_");
    }

    @Test
    public void taggingTest02() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest02.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.core.pdf.PdfName("Chunk"), com.itextpdf.core.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        compareResult("taggingTest02.pdf", "cmp_taggingTest02.pdf", "diff02_");
    }

    @Test
    public void taggingTest03() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest03.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.core.pdf.PdfName("Chunk"), com.itextpdf.core.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page1));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page1));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page1, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.HELVETICA), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page2, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page2));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page2, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page1.flush();
        page2.flush();

        document.close();

        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new FileInputStream(destinationFolder + "taggingTest03.pdf"));
        document = new PdfDocument(reader);
        Assert.assertEquals(2, document.getNextStructParentIndex().intValue());
        PdfPage page = document.getPage(1);
        Assert.assertEquals(0, page.getStructParentIndex().intValue());
        Assert.assertEquals(2, page.getNextMcid());
        document.close();
    }

    @Test
    public void taggingTest04() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.core.pdf.PdfName("Chunk"), com.itextpdf.core.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();
        byte[] bytes = baos.toByteArray();

        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new ByteArrayInputStream(bytes));
        writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest04.pdf"));
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        document = new PdfDocument(reader, writer);

        page = document.getPage(1);
        canvas = new PdfCanvas(page);

        List<PdfMcr> elems = document.getStructTreeRoot().getPageMarkedContentReferences(page);

        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 490);

        //Inserting span between of 2 existing ones.
        span1 = ((PdfStructElem) elems.get(0).getParent().getParent()).addKid(1, new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("text1");
        canvas.closeTag();

        //Inserting span at the end.
        PdfMcr elem = elems.get(elems.size() - 1);
        span1 = ((PdfStructElem) elem.getParent().getParent()).addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("text2");
        canvas.closeTag();

        canvas.endText();

        canvas.release();
        page.flush();

        document.close();

        compareResult("taggingTest04.pdf", "cmp_taggingTest04.pdf", "diff04_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void taggingTest05() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest05.pdf"));
        PdfDocument destination = new PdfDocument(writer);
        destination.setTagged();

        source.copyPages(new TreeSet<Integer>() {{
            add(3);
            add(4);
            add(10);
            add(11);
        }}, destination);
        source.copyPages(50, 52, destination);


        destination.close();
        source.close();

        compareResult("taggingTest05.pdf", "cmp_taggingTest05.pdf", "diff05_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void taggingTest06() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest06.pdf"));
        PdfDocument destination = new PdfDocument(writer);
        destination.setTagged();

        source.copyPages(6, source.getNumberOfPages(), destination);
        source.copyPages(1, 5, destination);

        destination.close();
        source.close();

        compareResult("taggingTest06.pdf", "cmp_taggingTest06.pdf", "diff06_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void taggingTest07() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest07.pdf"));
        PdfDocument destination = new PdfDocument(writer);


        source.copyPages(6, source.getNumberOfPages(), destination);
        source.copyPages(1, 5, destination);

        destination.close();
        source.close();

        String errorMessage = new CompareTool().compareByContent(destinationFolder + "taggingTest07.pdf", sourceFolder + "cmp_taggingTest07.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void taggingTest08() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest08.pdf"));
        PdfDocument destination = new PdfDocument(writer);
        destination.setTagged();

        for (int i = 1; i <= source.getNumberOfPages(); i++)
            source.copyPages(i, i, destination);

        destination.close();
        source.close();

        compareResult("taggingTest08.pdf", "cmp_taggingTest08.pdf", "diff08_");
    }

    @Test
    public void taggingTest09() throws Exception {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder + "iphone_user_guide.pdf"));
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest09.pdf"));
        PdfDocument document = new PdfDocument(reader, writer);

        PdfReader reader1 = new PdfReader(new FileInputStream(sourceFolder + "quick-brown-fox.pdf"));
        PdfDocument document1 = new PdfDocument(reader1);
        document1.copyPages(1, 1, document, 2);

        PdfReader reader2 = new PdfReader(new FileInputStream(sourceFolder + "quick-brown-fox-table.pdf"));
        PdfDocument document2 = new PdfDocument(reader2);
        document2.copyPages(1, 3, document, 4);


        document.close();
        document1.close();
        document2.close();

        compareResult("taggingTest09.pdf", "cmp_taggingTest09.pdf", "diff09_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void taggingTest10() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest10.pdf"));
        PdfDocument destination = new PdfDocument(writer);
        destination.setTagged();

        source.copyPages(1, source.getNumberOfPages(), destination);

        destination.close();
        source.close();

        compareResult("taggingTest10.pdf", "cmp_taggingTest10.pdf", "diff10_");
    }

    @Test
    public void taggingTest11() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest11.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.COURIER), 14);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Click ");
        canvas.closeTag();

        PdfStructElem link = paragraph.addKid(new PdfStructElem(document, PdfName.Link, page));
        canvas.openTag(new CanvasTag(link.addKid(new PdfMcrNumber(page, link))));
        canvas.setFillColorRgb(0, 0, 1).showText("here");
        PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(80, 508, 40, 18)).setColor(new float[] {0, 0, 1}).setBorder(new PdfArray(new float[]{0, 0, 1}));
        page.addAnnotation(-1, linkAnnotation, false);
        link.addKid(new PdfObjRef(linkAnnotation, link));
        canvas.closeTag();

        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.setFillColorRgb(0, 0, 0);
        canvas.showText(" to visit iText site.");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        document.close();

        compareResult("taggingTest11.pdf", "cmp_taggingTest11.pdf", "diff11_");
    }

    @Test
    public void taggingTest12() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest12.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFont.createStandardFont(FontConstants.COURIER), 14);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1)))
                .addProperty(PdfName.Lang, new PdfString("en-US"))
                .addProperty(PdfName.ActualText, new PdfString("The actual text is: Text with property list")));
        canvas.showText("Text with property list");
        canvas.closeTag();

        canvas.endText();
        canvas.release();

        document.close();

        compareResult("taggingTest12.pdf", "cmp_taggingTest12.pdf", "diff12_");
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
            Assert.fail(errorMessage);
        }
    }

}
