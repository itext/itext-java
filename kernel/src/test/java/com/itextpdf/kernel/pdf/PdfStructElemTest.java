package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

@Category(IntegrationTest.class)
public class PdfStructElemTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStructElemTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStructElemTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void structElemTest01() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "structElemTest01.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page1));

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
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page2, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page2, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        page1.flush();
        page2.flush();

        document.close();

        compareResult("structElemTest01.pdf", "cmp_structElemTest01.pdf", "diff_structElem_01_");
    }

    @Test
    public void structElemTest02() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "structElemTest02.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.kernel.pdf.PdfName("Chunk"), com.itextpdf.kernel.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.kernel.pdf.PdfName("Chunk"), page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        compareResult("structElemTest02.pdf", "cmp_structElemTest02.pdf", "diff_structElem_02_");
    }

    @Test
    public void structElemTest03() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "structElemTest03.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.kernel.pdf.PdfName("Chunk"), com.itextpdf.kernel.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page1));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.kernel.pdf.PdfName("Chunk"), page1));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page1, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page2));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page2, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.kernel.pdf.PdfName("Chunk"), page2));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page2, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page1.flush();
        page2.flush();

        document.close();

        document = new PdfDocument(new PdfReader(destinationFolder + "structElemTest03.pdf"));
        Assert.assertEquals(2, (int) document.getNextStructParentIndex());
        PdfPage page = document.getPage(1);
        Assert.assertEquals(0, page.getStructParentIndex().intValue());
        Assert.assertEquals(2, page.getNextMcid());
        document.close();
    }

    @Test
    public void structElemTest04() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        document.getStructTreeRoot().getRoleMap().put(new com.itextpdf.kernel.pdf.PdfName("Chunk"), com.itextpdf.kernel.pdf.PdfName.Span);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.kernel.pdf.PdfName("Chunk"), page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();
        byte[] bytes = baos.toByteArray();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(bytes));
        writer = new PdfWriter(new FileOutputStream(destinationFolder + "structElemTest04.pdf"));
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        document = new PdfDocument(reader, writer);

        page = document.getPage(1);
        canvas = new PdfCanvas(page);

        PdfStructElem p = (PdfStructElem) document.getStructTreeRoot().getKids().get(0).getKids().get(0);

        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 490);

        //Inserting span between of 2 existing ones.
        span1 = p.addKid(1, new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("text1");
        canvas.closeTag();

        //Inserting span at the end.
        span1 = p.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("text2");
        canvas.closeTag();

        canvas.endText();

        canvas.release();
        page.flush();

        document.close();

        compareResult("structElemTest04.pdf", "cmp_structElemTest04.pdf", "diff_structElem_04_");
    }

    @Test
    public void structElemTest05() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "structElemTest05.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 14);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1))));
        canvas.showText("Click ");
        canvas.closeTag();

        PdfStructElem link = paragraph.addKid(new PdfStructElem(document, PdfName.Link, page));
        canvas.openTag(new CanvasTag(link.addKid(new PdfMcrNumber(page, link))));
        canvas.setFillColorRgb(0, 0, 1).showText("here");
        PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(80, 508, 40, 18));
        linkAnnotation.setColor(new float[] {0, 0, 1}).setBorder(new PdfArray(new float[]{0, 0, 1}));
        page.addAnnotation(-1, linkAnnotation, false);
        link.addKid(new PdfObjRef(linkAnnotation, link));
        canvas.closeTag();

        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page, span2))));
        canvas.setFillColorRgb(0, 0, 0);
        canvas.showText(" to visit iText site.");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        document.close();

        compareResult("structElemTest05.pdf", "cmp_structElemTest05.pdf", "diff_structElem_05_");
    }

    @Test
    public void structElemTest06() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "structElemTest06.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 14);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page, span1)))
                .addProperty(PdfName.Lang, new PdfString("en-US"))
                .addProperty(PdfName.ActualText, new PdfString("The actual text is: Text with property list")));
        canvas.showText("Text with property list");
        canvas.closeTag();

        canvas.endText();
        canvas.release();

        document.close();

        compareResult("structElemTest06.pdf", "cmp_structElemTest06.pdf", "diff_structElem_06_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest01() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(new PdfWriter(destinationFolder + "structTreeCopyingTest01.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        ArrayList<Integer> pagesToCopy = new ArrayList<Integer>();
        pagesToCopy.add(3);
        pagesToCopy.add(4);
        pagesToCopy.add(10);
        pagesToCopy.add(11);
        source.copyPagesTo(pagesToCopy, destination);
        source.copyPagesTo(50, 52, destination);


        destination.close();
        source.close();

        compareResult("structTreeCopyingTest01.pdf", "cmp_structTreeCopyingTest01.pdf", "diff_copying_01_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest02() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(new PdfWriter(destinationFolder + "structTreeCopyingTest02.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        source.copyPagesTo(6, source.getNumberOfPages(), destination);
        source.copyPagesTo(1, 5, destination);

        destination.close();
        source.close();

        compareResult("structTreeCopyingTest02.pdf", "cmp_structTreeCopyingTest02.pdf", "diff_copying_02_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest03() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(new PdfWriter(destinationFolder + "structTreeCopyingTest03.pdf"));
        destination.initializeOutlines();

        source.copyPagesTo(6, source.getNumberOfPages(), destination);
        source.copyPagesTo(1, 5, destination);

        destination.close();
        source.close();

        // we don't compare tag structures, because resultant document is not tagged
        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + "structTreeCopyingTest03.pdf",
                        sourceFolder + "cmp_structTreeCopyingTest03.pdf",
                        destinationFolder, "diff_copying_03_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest04() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(new PdfWriter(destinationFolder + "structTreeCopyingTest04.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        for (int i = 1; i <= source.getNumberOfPages(); i++)
            source.copyPagesTo(i, i, destination);

        destination.close();
        source.close();

        compareResult("structTreeCopyingTest04.pdf", "cmp_structTreeCopyingTest04.pdf", "diff_copying_04_");
    }

    @Test
    public void structTreeCopyingTest05() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"),
                new PdfWriter(destinationFolder + "structTreeCopyingTest05.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document1.copyPagesTo(1, 1, document, 2);

        PdfDocument document2 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"));
        document2.copyPagesTo(1, 3, document, 4);

        document.close();
        document1.close();
        document2.close();

        compareResult("structTreeCopyingTest05.pdf", "cmp_structTreeCopyingTest05.pdf", "diff_copying_05_");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void structTreeCopyingTest06() throws Exception {
        PdfDocument source = new PdfDocument(new PdfReader(sourceFolder + "iphone_user_guide.pdf"));

        PdfDocument destination = new PdfDocument(new PdfWriter(destinationFolder + "structTreeCopyingTest06.pdf"));
        destination.setTagged();
        destination.initializeOutlines();

        source.copyPagesTo(1, source.getNumberOfPages(), destination);

        destination.close();
        source.close();

        compareResult("structTreeCopyingTest06.pdf", "cmp_structTreeCopyingTest06.pdf", "diff_copying_06_");
    }

    @Test
    public void structTreeCopyingTest07() throws Exception {
        PdfReader reader = new PdfReader(sourceFolder + "quick-brown-fox.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "structTreeCopyingTest07.pdf");
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Document));

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.kernel.pdf.PdfName.Span, page1));

        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(page1, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfDocument document1 = new PdfDocument(reader);
        document1.initializeOutlines();
        document1.copyPagesTo(1, 1, document);

        document.close();
        document1.close();

        compareResult("structTreeCopyingTest07.pdf", "cmp_structTreeCopyingTest07.pdf", "diff_copying_07_");
    }

    @Test
    public void structTreeCopyingTest08() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"),
                new PdfWriter(destinationFolder + "structTreeCopyingTest08.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document1.initializeOutlines();
        document1.copyPagesTo(1, 1, document, 2);

        document.close();
        document1.close();

        compareResult("structTreeCopyingTest08.pdf", "cmp_structTreeCopyingTest08.pdf", "diff_copying_08_");
    }

    @Test
    public void structTreeCopyingTest09() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"),
                new PdfWriter(destinationFolder + "structTreeCopyingTest09.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document1.initializeOutlines();
        document1.copyPagesTo(1, 1, document, 2);
        document1.copyPagesTo(1, 1, document, 4);

        document.close();
        document1.close();

        compareResult("structTreeCopyingTest09.pdf", "cmp_structTreeCopyingTest09.pdf", "diff_copying_09_");
    }

    @Test
    public void structTreeCopyingTest10() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "88th_Academy_Awards.pdf"),
                new PdfWriter(destinationFolder + "structTreeCopyingTest10.pdf"));

        PdfDocument document1 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox-table.pdf"));
        document1.initializeOutlines();
        document1.copyPagesTo(1, 3, document, 2);

        PdfDocument document2 = new PdfDocument(new PdfReader(sourceFolder + "quick-brown-fox.pdf"));
        document2.initializeOutlines();
        document2.copyPagesTo(1, 1, document, 4);

        document.close();
        document1.close();
        document2.close();

        compareResult("structTreeCopyingTest10.pdf", "cmp_structTreeCopyingTest10.pdf", "diff_copying_10_");
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
        if (errorMessage.length() > 0) {
            Assert.fail(errorMessage);
        }
    }

}
