package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import org.junit.Assert;
import org.junit.BeforeClass;
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

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void openSimpleDoc() throws IOException, PdfException {
        String filename = destinationFolder + "openSimpleDoc.pdf";
        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

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
        PdfObject object = pdfDoc.getXRef().get(1).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getXRef().get(2).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = pdfDoc.getXRef().get(4).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertTrue(pdfDoc.getXRef().get(5).getRefersTo().getType() == PdfObject.Stream);
        pdfDoc.close();
    }

    @Test
    public void openSimpleDocWithFullCompression() throws IOException, PdfException {
        String filename = sourceFolder + "simpleCanvasWithFullCompression.pdf";
        com.itextpdf.core.pdf.PdfReader reader = new com.itextpdf.core.pdf.PdfReader(new FileInputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfObject object = pdfDoc.getXRef().get(1).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getXRef().get(2).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = pdfDoc.getXRef().get(4).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        object = pdfDoc.getXRef().get(5).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Stream);
        String content = "100 100 100 100 re\nf\n";
        Assert.assertArrayEquals(OutputStream.getIsoBytes(content), ((PdfStream)object).getInputStreamBytes());

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
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertTrue(document.getXRef().get(5).getRefersTo().getType() == PdfObject.Stream);

        object = document.getXRef().get(6).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Boolean);
        Assert.assertNotNull(object.getIndirectReference());


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
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertTrue(document.getXRef().get(5).getRefersTo().getType() == PdfObject.Stream);

        for (int i = 6; i < document.getXRef().size(); i++)
            Assert.assertTrue(document.getXRef().get(i).getRefersTo().getType() == PdfObject.Dictionary);
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
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertTrue(document.getXRef().get(5).getRefersTo().getType() == PdfObject.Stream);

        for (int i = 6; i < 6+32; i++)
            Assert.assertTrue(document.getXRef().get(6).getRefersTo().getType() == PdfObject.Dictionary);

        for (int i = 6+32; i < document.getXRef().size(); i++)
            Assert.assertTrue(document.getXRef().get(i).getRefersTo().getType() == PdfObject.IndirectReference);
        document.close();
    }

    @Test
    public void indirectsChain3() throws PdfException, IOException {
        String filename = sourceFolder + "indirectsChain3.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXRef().get(1).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertTrue(document.getXRef().get(5).getRefersTo().getType() == PdfObject.Stream);

        Assert.assertTrue(document.getXRef().get(6).getRefersTo().getType() == PdfObject.Dictionary);
        for (int i = 7; i < document.getXRef().size(); i++)
            Assert.assertTrue(document.getXRef().get(i).getRefersTo().getType() == PdfObject.IndirectReference);
        document.close();
    }

    @Test
    public void invalidIndirect() throws PdfException, IOException {
        String filename = sourceFolder + "invalidIndirect.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXRef().get(1).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXRef().get(2).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXRef().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);

        object = document.getXRef().get(4).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.Dictionary);
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertTrue(document.getXRef().get(5).getRefersTo().getType() == PdfObject.Stream);
        Assert.assertTrue(document.getXRef().get(6).getRefersTo().getType() == PdfObject.Dictionary);
        for (int i = 7; i < document.getXRef().size(); i++)
            Assert.assertNull(document.getXRef().get(i).getRefersTo());
        document.close();
    }

    @Test
    public void pagesTest01() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocument.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 1000);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }
        reader.close();
    }

    @Test
    public void pagesTest02() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 1000);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream().getInputStreamBytes());
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
        Assert.assertTrue(pageCount == 10);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+i+")"));
        }
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }
        reader.close();
    }

    @Test
    public void pagesTest04() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 3);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.startsWith(pageNum + "00"));
        }
        reader.close();
    }

    @Test
    public void pagesTest05() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument05.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 3);

        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.removePage(1);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum  = document.getNumOfPages();
            PdfPage page = document.removePage(pageNum);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.startsWith(pageNum + "00"));
        }
        reader.close();
    }

    @Test
    public void pagesTest06() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument06.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 2);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream().getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));

        page = document.getPage(2);
        content = new String(page.getContentStream().getInputStreamBytes());
        Assert.assertTrue(content.startsWith("300"));
        reader.close();
        document.close();

        reader = new PdfReader(new FileInputStream(filename));
        document = new PdfDocument(reader);

        page = document.removePage(2);
        content = new String(page.getContentStream().getInputStreamBytes());
        Assert.assertTrue(content.startsWith("300"));
        page = document.removePage(1);
        content = new String(page.getContentStream().getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));

        reader.close();
    }

    @Test
    public void pagesTest07() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument07.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 2);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    public void pagesTest08() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument08.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 1);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    public void pagesTest09() throws IOException, PdfException {
        String filename = sourceFolder + "PagesDocument09.pdf";

        InputStream stream = new FileInputStream(filename);
        PdfReader reader = new PdfReader(stream);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 1);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream().getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));

        page = document.removePage(1);
        content = new String(page.getContentStream().getInputStreamBytes());
        Assert.assertTrue(content.startsWith("100"));
        reader.close();
    }

    @Test
    public void pagesTest10() throws IOException, PdfException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumOfPages();
        Assert.assertTrue(pageCount == 1000);

        Random rnd = new Random();
        for (int i = 1; i < document.getNumOfPages() + 1; i++) {
            int pageNum = rnd.nextInt(document.getNumOfPages()) + 1;
            PdfPage page = document.getPage(pageNum);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }

        ArrayList<Integer> pageNums = new ArrayList<Integer>(1000);
        for (int i = 0; i < 1000; i++)
            pageNums.add(i+1);

        for (int i = 1; i < pageCount + 1; i++) {
            int index = rnd.nextInt(document.getNumOfPages()) + 1;
            int pageNum = pageNums.remove(index-1);
            PdfPage page = document.removePage(index);
            String content = new String(page.getContentStream().getInputStreamBytes());
            Assert.assertTrue(content.contains("("+pageNum+")"));
        }
        reader.close();
        document.close();
    }

    @Test
    public void comparePerformanceTest() throws IOException, PdfException, DocumentException {
        comparePerformance(sourceFolder + "performanceTest.pdf", "no compression", 1.70f);
    }

    @Test
    public void comparePerformanceTestFullCompression() throws IOException, PdfException, DocumentException {
        comparePerformance(sourceFolder + "performanceTestWithCompression.pdf", "compression", 1.85f);
    }

    @Test
    public void comparePerformanceRandomTest() throws IOException, PdfException, DocumentException {
        comparePerformanceRandom(sourceFolder + "performanceTest.pdf", "random, no compression", 2.3f);
    }

    @Test
    public void comparePerformanceRandomTestFullCompression() throws IOException, PdfException, DocumentException {
        comparePerformanceRandom(sourceFolder + "performanceTestWithCompression.pdf", "random, compression", 2.1f);
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
                page.getContentStream();
            }
            reader.close();
            pdfDoc.close();
            fis.close();
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
                page.getContentStream();
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


    protected boolean objectTypeEqualTo(PdfObject object, PdfName type) throws PdfException {
        PdfName objectType = ((PdfDictionary)object).getAsName(PdfName.Type);
        return type.equals(objectType);
    }
}
