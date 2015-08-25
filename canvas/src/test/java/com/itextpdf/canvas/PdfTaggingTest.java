package com.itextpdf.canvas;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.tagging.*;
import com.itextpdf.core.testutils.CompareTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.TreeSet;


public class PdfTaggingTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfTaggingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfTaggingTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void taggingTest01() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "taggingTest01.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfWriter.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Document));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.COURIER, "")), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));

        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(span1.addKid(new PdfMcrDictionary(page, span1)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        page = document.addNewPage();
        canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.HELVETICA, "")), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest01.pdf", sourceFolder + "cmp_taggingTest01.pdf", destinationFolder, "diff_"));
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
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.COURIER, "")), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest02.pdf", sourceFolder + "cmp_taggingTest02.pdf", destinationFolder, "diff_"));
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

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.COURIER, "")), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        page = document.addNewPage();
        canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.HELVETICA, "")), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();
        page.flush();

        document.close();

        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new FileInputStream(destinationFolder + "taggingTest03.pdf"));
        document = new PdfDocument(reader);
        Assert.assertEquals(2, document.getNextStructParentIndex().intValue());
        PdfPage page1 = document.getPage(1);
        Assert.assertEquals(0, page1.getStructParentIndex().intValue());
        Assert.assertEquals(2, page1.getNextMcid());
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
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.COURIER, "")), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Hello ");
        canvas.closeTag();
        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, new com.itextpdf.core.pdf.PdfName("Chunk"), page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
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

        List<IPdfTag> elems = page.getPageTags();

        canvas.beginText();
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.COURIER, "")), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 490);

        //Inserting span between of 2 existing ones.
        span1 = ((PdfStructElem) elems.get(0).getParent().getParent()).addKid(1, new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("text1");
        canvas.closeTag();

        elems = page.getPageTags();

        //Inserting span at the end.
        IPdfTag elem = elems.get(elems.size() - 1);
        span1 = ((PdfStructElem) elem.getParent().getParent()).addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("text2");
        canvas.closeTag();

        canvas.endText();

        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest04.pdf", sourceFolder + "cmp_taggingTest04.pdf", destinationFolder, "diff_"));
    }

    @Test
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest05.pdf", sourceFolder + "cmp_taggingTest05.pdf", destinationFolder, "diff_"));

    }

    @Test
    public void taggingTest06() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest06.pdf"));
        PdfDocument destination = new PdfDocument(writer);
        destination.setTagged();

        source.copyPages(6, source.getNumOfPages(), destination);
        source.copyPages(1, 5, destination);

        destination.close();
        source.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest06.pdf", sourceFolder + "cmp_taggingTest06.pdf", destinationFolder, "diff_"));

    }

    @Test
    public void taggingTest07() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest07.pdf"));
        PdfDocument destination = new PdfDocument(writer);


        source.copyPages(6, source.getNumOfPages(), destination);
        source.copyPages(1, 5, destination);

        destination.close();
        source.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest07.pdf", sourceFolder + "cmp_taggingTest07.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void taggingTest08() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest08.pdf"));
        PdfDocument destination = new PdfDocument(writer);
        destination.setTagged();

        for (int i = 1; i <= source.getNumOfPages(); i++)
            source.copyPages(i, i, destination);

        destination.close();
        source.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest08.pdf", sourceFolder + "cmp_taggingTest08.pdf", destinationFolder, "diff_"));

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

//        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest08.pdf", sourceFolder + "cmp_taggingTest08.pdf", destinationFolder, "diff_"));

    }

    @Test
    public void taggingTest10() throws Exception {
        FileInputStream fis = new FileInputStream(sourceFolder + "iphone_user_guide.pdf");
        PdfReader reader = new PdfReader(fis);
        PdfDocument source = new PdfDocument(reader);

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + "taggingTest10.pdf"));
        PdfDocument destination = new PdfDocument(writer);
        destination.setTagged();

        source.copyPages(1, source.getNumOfPages(), destination);

        destination.close();
        source.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest10.pdf", sourceFolder + "cmp_taggingTest10.pdf", destinationFolder, "diff_"));

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
        canvas.setFontAndSize(new PdfType1Font(document, new Type1Font(FontConstants.COURIER, "")), 14);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span1.addKid(new PdfMcrNumber(page, span1)));
        canvas.showText("Click ");
        canvas.closeTag();

        PdfStructElem link = paragraph.addKid(new PdfStructElem(document, PdfName.Link, page));
        canvas.openTag(link.addKid(new PdfMcrNumber(page, link)));
        canvas.setFillColorRgb(0, 0, 1).showText("here");
        PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(document, new Rectangle(80, 508, 40, 18)).setColor(new float[] {0, 0, 1}).setBorder(new PdfArray(new float[]{0, 0, 1}));
        page.addAnnotation(linkAnnotation);
        link.addKid(new PdfObjRef(linkAnnotation, link));
        canvas.closeTag();

        PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, com.itextpdf.core.pdf.PdfName.Span, page));
        canvas.openTag(span2.addKid(new PdfMcrNumber(page, span2)));
        canvas.setFillColorRgb(0, 0, 0);
        canvas.showText(" to visit iText site.");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "taggingTest11.pdf", sourceFolder + "cmp_taggingTest11.pdf", destinationFolder, "diff_"));
    }




}
