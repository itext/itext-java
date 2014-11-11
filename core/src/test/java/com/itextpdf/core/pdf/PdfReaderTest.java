package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PdfReaderTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfReaderTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfReaderTest/";

    static final String author = "Alexander Chingarev";
    static final String creator = "iText 6";
    static final String title = "Empty iText 6 Document";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void openSimpleDoc() throws IOException, PdfException {
        String filename = destinationFolder + "openSimpleDoc.pdf";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        pdfDoc.addNewPage();
        pdfDoc.close();

        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new FileInputStream(filename));
        pdfDoc = new PdfDocument(reader);
        Assert.assertEquals(author, pdfDoc.getInfo().getAuthor());
        Assert.assertEquals(creator, pdfDoc.getInfo().getCreator());
        Assert.assertEquals(title, pdfDoc.getInfo().getTitle());
        PdfObject object = pdfDoc.getXRef().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getXRef().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getXRef().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());

        object = pdfDoc.getXRef().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.Stream, pdfDoc.getXRef().get(5).getRefersTo().getType());

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        pdfDoc.close();
    }

    @Test
    public void openSimpleDocWithFullCompression() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithFullCompression.pdf";
        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new FileInputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfObject object = pdfDoc.getXRef().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getXRef().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getXRef().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());

        object = pdfDoc.getXRef().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        object = pdfDoc.getXRef().get(5).getRefersTo();
        Assert.assertEquals(PdfObject.Stream, object.getType());
        String content = "100 100 100 100 re\nf\n";
        Assert.assertArrayEquals(OutputStream.getIsoBytes(content), ((PdfStream)object).getInputStreamBytes());

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void primitivesRead() throws PdfException, IOException {
        String filename = destinationFolder + "primitivesRead.pdf";
        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        catalog.put(new PdfName("a"), new PdfBoolean(true).makeIndirect(document));
        document.close();

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);

        PdfObject object = document.getXRef().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.Stream, document.getXRef().get(5).getRefersTo().getType());

        object = document.getXRef().get(6).getRefersTo();
        Assert.assertEquals(PdfObject.Boolean, object.getType());
        Assert.assertNotNull(object.getIndirectReference());


        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void indirectsChain1() throws PdfException, IOException {
        String filename = destinationFolder + "indirectsChain1.pdf";
        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(new PdfName("b"), new PdfName("c"));
        }});
        PdfObject object = dictionary;
        for (int i = 0; i < 5; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), object);
        document.close();

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);

        object = document.getXRef().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.Stream, document.getXRef().get(5).getRefersTo().getType());

        for (int i = 6; i < document.getXRef().size(); i++)
            Assert.assertEquals(PdfObject.Dictionary, document.getXRef().get(i).getRefersTo().getType());

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void indirectsChain2() throws PdfException, IOException {
        String filename = destinationFolder + "indirectsChain2.pdf";
        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(new PdfName("b"), new PdfName("c"));
        }});
        PdfObject object = dictionary;
        for (int i = 0; i < 100; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), object);
        document.close();

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);

        object = document.getXRef().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.Stream, document.getXRef().get(5).getRefersTo().getType());

        for (int i = 6; i < 6+32; i++)
            Assert.assertEquals(PdfObject.Dictionary, document.getXRef().get(6).getRefersTo().getType());

        for (int i = 6+32; i < document.getXRef().size(); i++)
            Assert.assertEquals(PdfObject.IndirectReference, document.getXRef().get(i).getRefersTo().getType());

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void indirectsChain3() throws PdfException, IOException {
        String filename = sourceFolder + "indirectsChain3.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXRef().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.Stream, document.getXRef().get(5).getRefersTo().getType());

        Assert.assertEquals(PdfObject.Dictionary, document.getXRef().get(6).getRefersTo().getType());
        for (int i = 7; i < document.getXRef().size(); i++)
            Assert.assertEquals(PdfObject.IndirectReference, document.getXRef().get(i).getRefersTo().getType());

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void invalidIndirect() throws PdfException, IOException {
        String filename = sourceFolder + "invalidIndirect.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXRef().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.Dictionary, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.Stream, document.getXRef().get(5).getRefersTo().getType());
        Assert.assertEquals(PdfObject.Dictionary, document.getXRef().get(6).getRefersTo().getType());
        for (int i = 7; i < document.getXRef().size(); i++)
            Assert.assertNull(document.getXRef().get(i).getRefersTo());

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest01() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocument.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest02() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest03() throws IOException, PdfException {
        String filename = sourceFolder + "10PagesDocumentWithLeafs.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }
        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest04() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(3, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.startsWith(pageNum + "00"));
        }
        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest05() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument05.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(3, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.startsWith(pageNum + "00"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest06() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument06.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(2, pageCount);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream(0).getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));

        page = document.getPage(2);
        content = new String(page.getContentStream(0).getInputStreamBytes());
        Assert.assertTrue(content.startsWith("300"));
        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);

        page = document.removePage(2);
        content = new String(page.getContentStream(0).getInputStreamBytes());
        Assert.assertTrue(content.startsWith("300"));
        page = document.removePage(1);
        content = new String(page.getContentStream(0).getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest07() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument07.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(2, pageCount);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest08() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument08.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1, pageCount);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest09() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument09.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1, pageCount);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream(0).getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));

        page = document.removePage(1);
        content = new String(page.getContentStream(0).getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));
        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Test
    public void pagesTest10() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1000, pageCount);

        Random rnd = new Random();
        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            int pageNum = rnd.nextInt(document.getNumOfPages()) + 1;
            PdfPage page = document.getPage(pageNum);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }

        ArrayList<Integer> pageNums = new ArrayList<Integer>(1000);
        for (int i = 0; i < 1000; i++)
            pageNums.add(i+1);

        for (int i = 1; i < pageCount + 1; i++) {
            int index = rnd.nextInt(document.getNumOfPages()) + 1;
            int pageNum = pageNums.remove(index-1);
            PdfPage page = document.removePage(index);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }
        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);
        reader.close();
        document.close();
    }

    @Ignore
    @Test
    public void compareMemory5Test() throws IOException, PdfException, DocumentException {
        String filename = sourceFolder + "performanceTest.pdf";
        int runCount = 10;
        for (int i = 0; i < runCount; i++) {
            FileInputStream fis = new FileInputStream(filename);
            com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(fis);
            int pageCount = reader.getNumberOfPages();
            for (int k = 1; k < pageCount+1; k++) {
                com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(k);
                page.get(com.itextpdf.text.pdf.PdfName.MEDIABOX);
                com.itextpdf.text.pdf.PdfReader.getStreamBytes((PRStream)page.getAsStream(com.itextpdf.text.pdf.PdfName.CONTENTS));
            }
            reader.close();
        }
        System.out.flush();
    }

    @Test @Ignore
    public void compareMemory6Test() throws IOException, PdfException, DocumentException {
        String filename = sourceFolder + "performanceTest.pdf";
        int runCount = 10;
        for (int i = 0; i < runCount; i++) {
            FileInputStream fis = new FileInputStream(filename);
            PdfReader reader = new PdfReader(fis);
            PdfDocument pdfDoc = new PdfDocument(reader);
            int pageCount = pdfDoc.getNumOfPages();
            for (int k = 1; k < pageCount+1; k++) {
                PdfPage page = pdfDoc.getPage(k);
                page.getPdfObject().get(PdfName.MediaBox);
                page.getContentStream(0);
            }
            reader.close();
            pdfDoc.close();
            fis.close();
        }
        System.out.flush();
    }

    @Test
    public void comparePerformanceTest() throws IOException, PdfException, DocumentException {
        comparePerformance(sourceFolder + "performanceTest.pdf", "no compression", 1.55f);
    }

    @Test
    public void comparePerformanceTestFullCompression() throws IOException, PdfException, DocumentException {
        comparePerformance(sourceFolder + "performanceTestWithCompression.pdf", "compression", 1.7f);
    }

    @Test
    public void comparePerformanceRandomTest() throws IOException, PdfException, DocumentException {
        comparePerformanceRandom(sourceFolder + "performanceTest.pdf", "random, no compression", 2.15f);
    }

    @Test
    public void comparePerformanceRandomTestFullCompression() throws IOException, PdfException, DocumentException {
        comparePerformanceRandom(sourceFolder + "performanceTestWithCompression.pdf", "random, compression", 2.0f);
    }

    private void comparePerformance(String filename, String message, float coef) throws IOException, PdfException, DocumentException {
        int runCount = 10;
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < runCount; i++) {
            FileInputStream fis = new FileInputStream(filename);
            com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(fis);
            int pageCount = reader.getNumberOfPages();
            for (int k = 1; k < pageCount+1; k++) {
                com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(k);
                page.get(com.itextpdf.text.pdf.PdfName.MEDIABOX);
                com.itextpdf.text.pdf.PdfReader.getStreamBytes((PRStream)page.getAsStream(com.itextpdf.text.pdf.PdfName.CONTENTS));
            }
            reader.close();
        }
        long t2 = System.currentTimeMillis();
        long iText5Time = t2 - t1;
        System.out.println(String.format("iText5 time: %dms\t(%s)", iText5Time, message));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < runCount; i++) {
            FileInputStream fis = new FileInputStream(filename);
            PdfReader reader = new PdfReader(fis);
            PdfDocument pdfDoc = new PdfDocument(reader);
            int pageCount = pdfDoc.getNumOfPages();
            for (int k = 1; k < pageCount+1; k++) {
                PdfPage page = pdfDoc.getPage(k);
                page.getPdfObject().get(PdfName.MediaBox);
                page.getContentStream(0).getInputStreamBytes();
            }
            reader.close();
            pdfDoc.close();
        }
        t2 = System.currentTimeMillis();
        long iText6Time = t2 - t1;
        System.out.println(String.format("iText6 time: %dms\t(%s)", iText6Time, message));
        message = String.format("%s: %.2f (%.2f)", message, (double)iText5Time/iText6Time, coef);
        Assert.assertTrue(message, iText5Time > iText6Time*coef);
        System.out.println(message);
        System.out.flush();
    }

    private void comparePerformanceRandom(String filename, String message, float coef) throws IOException, PdfException, DocumentException {
        int runCount = 10;
        int totalCount = 10000;

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < runCount; i++) {
            FileInputStream fis = new FileInputStream(filename);
            com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(fis);
            int pageCount = reader.getNumberOfPages();
            Random rnd = new Random();
            for (int k = 0; k < totalCount; k++) {
                com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(rnd.nextInt(pageCount)+1);
                page.get(com.itextpdf.text.pdf.PdfName.MEDIABOX);
                com.itextpdf.text.pdf.PdfReader.getStreamBytes((PRStream)page.getAsStream(com.itextpdf.text.pdf.PdfName.CONTENTS));
            }
            reader.close();
        }

        long t2 = System.currentTimeMillis();
        long iText5Time = t2 - t1;
        System.out.println(String.format("iText5 time: %dms\t(%s)", iText5Time, message));

        t1 = System.currentTimeMillis();
        for (int i = 0; i < runCount; i++) {
            FileInputStream fis = new FileInputStream(filename);
            PdfReader reader = new PdfReader(fis);
            PdfDocument pdfDoc = new PdfDocument(reader);
            int pageCount = pdfDoc.getNumOfPages();
            Random rnd = new Random();
            for (int k = 0; k < totalCount; k++) {
                PdfPage page = pdfDoc.getPage(rnd.nextInt(pageCount)+1);
                page.getPdfObject().get(PdfName.MediaBox);
                page.getContentStream(0).getInputStreamBytes();
            }
            reader.close();
            pdfDoc.close();
        }
        t2 = System.currentTimeMillis();
        long iText6Time = t2 - t1;
        System.out.println(String.format("iText6 time: %dms\t(%s)", iText6Time, message));
        message = String.format("%s: %.2f (%.2f)", message, (double)iText5Time/iText6Time, coef);
        Assert.assertTrue(message, iText5Time > iText6Time*coef);
        System.out.println(message);
        System.out.flush();
    }

    @Test
    public void correctSimpleDoc1() throws IOException, PdfException {
        String filename = sourceFolder + "correctSimpleDoc1.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assert.assertNotNull(page.getContentStream(0).getInputStreamBytes());

        reader.close();
        document.close();
    }

    @Test
    public void correctSimpleDoc2() throws IOException, PdfException {
        String filename = sourceFolder + "correctSimpleDoc2.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assert.assertNotNull(page.getContentStream(0).getInputStreamBytes());

        reader.close();
        document.close();
    }

    @Test
    public void correctSimpleDoc3() throws IOException, PdfException {
        String filename = sourceFolder + "correctSimpleDoc3.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assert.assertNotNull(page.getContentStream(0).getInputStreamBytes());

        reader.close();
        document.close();
    }

    @Test @Ignore //test with abnormal object declaration
    public void correctSimpleDoc4() throws IOException, PdfException {
        String filename = sourceFolder + "correctSimpleDoc4.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assert.assertNotNull(page.getContentStream(0).getInputStreamBytes());

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest01() throws IOException, PdfException {
        String filename = sourceFolder + "OnlyTrailer.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest02() throws IOException, PdfException {
        String filename = sourceFolder + "CompressionShift1.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest03() throws IOException, PdfException {
        String filename = sourceFolder + "CompressionShift2.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest04() throws IOException, PdfException {
        String filename = sourceFolder + "CompressionWrongObjStm.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    public void fixPdfTest05() throws IOException, PdfException {
        String filename = sourceFolder + "CompressionWrongShift.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    public void fixPdfTest06() throws IOException, PdfException {
        String filename = sourceFolder + "InvalidOffsets.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest07() throws IOException, PdfException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences1.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    public void fixPdfTest08() throws IOException, PdfException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences2.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        Assert.assertEquals(author, document.getInfo().getAuthor());
        Assert.assertEquals(creator, document.getInfo().getCreator());
        Assert.assertEquals(title, document.getInfo().getTitle());

        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest09() throws IOException, PdfException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences3.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        Assert.assertEquals(author, document.getInfo().getAuthor());
        Assert.assertEquals(creator, document.getInfo().getCreator());
        Assert.assertEquals(title, document.getInfo().getTitle());

        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest10() throws IOException, PdfException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences4.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        Assert.assertEquals(null, document.getInfo().getAuthor());
        Assert.assertEquals(null, document.getInfo().getCreator());
        Assert.assertEquals(null, document.getInfo().getTitle());

        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest11() throws IOException, PdfException {
        String filename = sourceFolder + "XRefSectionWithoutSize.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest12() throws IOException, PdfException {
        String filename = sourceFolder + "XRefWithBreaks.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest13() throws IOException, PdfException {
        String filename = sourceFolder + "XRefWithInvalidGenerations1.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < 10; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));

        }

        boolean exception = false;
        try {
            for (int i = 11; i < document.getNumOfPages() + 1; i++) {
                PdfPage page = document.getPage(i);
                page.getContentStream(0).getInputStreamBytes();
            }
        } catch (PdfException ex) {
            exception = true;
        }
        Assert.assertTrue(exception);
        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest14() throws IOException, PdfException {
        String filename = sourceFolder + "XRefWithInvalidGenerations2.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    public void fixPdfTest15() throws IOException, PdfException {
        String filename = sourceFolder + "XRefWithInvalidGenerations3.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest16() throws IOException, PdfException {
        String filename = sourceFolder + "XrefWithInvalidOffsets.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void fixPdfTest17() throws IOException, PdfException {
        String filename = sourceFolder + "XrefWithNullOffsets.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        reader.close();
        document.close();
    }

    @Test
    public void appendModeWith1000Pages() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocumentAppended.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertFalse(content.isEmpty());
            content = new String(page.getContentStream(1).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
            content = new String(page.getContentStream(2).getInputStreamBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);

        reader.close();
        document.close();
    }

    @Test
    public void appendModeWith1000PagesWithCompression() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompressionAppended.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertFalse(content.isEmpty());
            content = new String(page.getContentStream(1).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
            content = new String(page.getContentStream(2).getInputStreamBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);

        reader.close();
        document.close();
    }

    @Test
    public void appendModeWith10Pages() throws IOException, PdfException {
        String filename = sourceFolder + "10PagesDocumentAppended.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertFalse(content.isEmpty());
            content = new String(page.getContentStream(1).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
            content = new String(page.getContentStream(2).getInputStreamBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);

        reader.close();
        document.close();
    }

    @Test
    public void appendModeWith10PagesWithCompression() throws IOException, PdfException {
        String filename = sourceFolder + "10PagesDocumentWithFullCompressionAppended.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertFalse(content.isEmpty());
            content = new String(page.getContentStream(1).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
            content = new String(page.getContentStream(2).getInputStreamBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.rebuildXref);

        reader.close();
        document.close();
    }

    @Test
    public void appendModeWith10PagesFix1() throws IOException, PdfException {
        String filename = sourceFolder + "10PagesDocumentAppendedFix1.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertFalse(content.isEmpty());
            content = new String(page.getContentStream(1).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
            content = new String(page.getContentStream(2).getInputStreamBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertTrue("need rebuildXref()", reader.rebuildXref);
        Assert.assertNotNull("Invalid trailer", document.getTrailer().getPdfObject().get(PdfName.ID));

        reader.close();
        document.close();
    }

    @Test
    public void appendModeWith10PagesFix2() throws IOException, PdfException {
        String filename = sourceFolder + "10PagesDocumentAppendedFix2.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        int pageCount = document.getNumOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getInputStreamBytes());
            Assert.assertFalse(content.isEmpty());
            content = new String(page.getContentStream(1).getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
            content = new String(page.getContentStream(2).getInputStreamBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertTrue("need rebuildXref()", reader.rebuildXref);
        Assert.assertNotNull("Invalid trailer", document.getTrailer().getPdfObject().get(PdfName.ID));

        reader.close();
        document.close();
    }

    @Test
    public void StreamLengthCorrection1() throws IOException, PdfException {
        String filename = sourceFolder + "10PagesDocumentWithInvalidStreamLength.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        int pageCount = pdfDoc.getNumOfPages();
        for (int k = 1; k < pageCount + 1; k++) {
            PdfPage page = pdfDoc.getPage(k);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getInputStreamBytes();
            Assert.assertEquals(57, content.length);
        }
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void StreamLengthCorrection2() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithDrawingLength1.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(1);
        page.getPdfObject().get(PdfName.MediaBox);
        byte[] content = page.getFirstContentStream().getInputStreamBytes();
        Assert.assertEquals(696, content.length);
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void StreamLengthCorrection3() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithDrawingLength2.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(1);
        page.getPdfObject().get(PdfName.MediaBox);
        byte[] content = page.getFirstContentStream().getInputStreamBytes();
        Assert.assertEquals(697, content.length);
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void StreamLengthCorrection4() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithDrawingLength3.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(1);
        page.getPdfObject().get(PdfName.MediaBox);
        byte[] content = page.getFirstContentStream().getInputStreamBytes();
        Assert.assertEquals(696, content.length);
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void StreamLengthCorrection5() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithDrawingLength4.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(1);
        page.getPdfObject().get(PdfName.MediaBox);
        byte[] content = page.getFirstContentStream().getInputStreamBytes();
        Assert.assertEquals(696, content.length);
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void StreamLengthCorrection6() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithDrawingWithInvalidStreamLength1.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(1);
        page.getPdfObject().get(PdfName.MediaBox);
        byte[] content = page.getFirstContentStream().getInputStreamBytes();
        Assert.assertEquals(696, content.length);
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void StreamLengthCorrection7() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithDrawingWithInvalidStreamLength2.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(1);
        page.getPdfObject().get(PdfName.MediaBox);
        byte[] content = page.getFirstContentStream().getInputStreamBytes();
        Assert.assertEquals(696, content.length);
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void StreamLengthCorrection8() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithDrawingWithInvalidStreamLength3.pdf";

        FileInputStream fis = new FileInputStream(filename);
        PdfReader reader = new PdfReader(fis);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfPage page = pdfDoc.getPage(1);
        page.getPdfObject().get(PdfName.MediaBox);
        byte[] content = page.getFirstContentStream().getInputStreamBytes();
        Assert.assertEquals(697, content.length);
        reader.close();
        pdfDoc.close();
    }

    private boolean objectTypeEqualTo(PdfObject object, PdfName type) throws PdfException {
        PdfName objectType = ((PdfDictionary)object).getAsName(PdfName.Type);
        return type.equals(objectType);
    }

    /**
     * Returns the current memory use.
     *
     * @return the current memory use
     */
    private static long getMemoryUse() {
        garbageCollect();
        garbageCollect();
        garbageCollect();
        garbageCollect();
        long totalMemory = Runtime.getRuntime().totalMemory();
        garbageCollect();
        garbageCollect();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return (totalMemory - freeMemory);
    }

    /**
     * Makes sure all garbage is cleared from the memory.
     */
    private static void garbageCollect() {
        try {
            System.gc();
            Thread.sleep(200);
            System.runFinalization();
            Thread.sleep(200);
            System.gc();
            Thread.sleep(200);
            System.runFinalization();
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
