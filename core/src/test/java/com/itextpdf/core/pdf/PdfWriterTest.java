package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.ras.RandomAccessFileOrArray;
import com.itextpdf.io.streams.ras.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfWriterTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfWriterTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfWriterTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createEmptyDocument() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "emptyDocument.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(destinationFolder + "emptyDocument.pdf");
        Assert.assertNotNull(reader.getPageN(1));
        reader.close();

    }

    @Test
    public void useObjectForMultipleTimes1() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes1.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes1.pdf");
    }

    @Test
    public void useObjectForMultipleTimes2() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes2.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        helloWorld.flush();
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes2.pdf");
    }

    @Test
    public void useObjectForMultipleTimes3() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes3.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        helloWorld.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes3.pdf");
    }

    @Test
    public void useObjectForMultipleTimes4() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes4.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        helloWorld.flush();
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes4.pdf");
    }

    private void validateUseObjectForMultipleTimesTest(String filename) throws IOException {
        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(filename);
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page);
        com.itextpdf.text.pdf.PdfDictionary helloWorld = page.getAsDict(new com.itextpdf.text.pdf.PdfName("HelloWorld"));
        Assert.assertNotNull(helloWorld);
        com.itextpdf.text.pdf.PdfString world = helloWorld.getAsString(new com.itextpdf.text.pdf.PdfName("Hello"));
        Assert.assertEquals("World", world.toString());
        helloWorld = reader.getCatalog().getAsDict(new com.itextpdf.text.pdf.PdfName("HelloWorld"));
        Assert.assertNotNull(helloWorld);
        world = helloWorld.getAsString(new com.itextpdf.text.pdf.PdfName("Hello"));
        Assert.assertEquals("World", world.toString());
        reader.close();
    }

    public static class PRTokeniserTest {

        @Before
        public void setUp() throws Exception {
        }

        @After
        public void tearDown() throws Exception {
        }

        private void checkTokenTypes(String data, PRTokeniser.TokenType... expectedTypes) throws Exception {
            RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
            PRTokeniser tok = new PRTokeniser(new RandomAccessFileOrArray(factory.createSource(data.getBytes())));

            for(int i = 0; i < expectedTypes.length; i++){
                tok.nextValidToken();
                //System.out.println(tok.getTokenType() + " -> " + tok.getStringValue());
                Assert.assertEquals("Position " + i, expectedTypes[i], tok.getTokenType());
            }
        }

        @Test
        public void testOneNumber() throws Exception {
            checkTokenTypes(
                    "/Name1 70",
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.Number,
                    PRTokeniser.TokenType.EndOfFile
            );
        }

        @Test
        public void testTwoNumbers() throws Exception {
            checkTokenTypes(
                    "/Name1 70/Name 2",
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.Number,
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.Number,
                    PRTokeniser.TokenType.EndOfFile
            );
        }

        @Test
        public void tokenTypesTest() throws Exception {
            checkTokenTypes(
                    "<</Size 70/Root 46 0 R/Info 44 0 R/ID[<8C2547D58D4BD2C6F3D32B830BE3259D><8F69587888569A458EB681A4285D5879>]/Prev 116 >>",
                    PRTokeniser.TokenType.StartDic,
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.Number,
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.Ref,
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.Ref,
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.StartArray,
                    PRTokeniser.TokenType.String,
                    PRTokeniser.TokenType.String,
                    PRTokeniser.TokenType.EndArray,
                    PRTokeniser.TokenType.Name,
                    PRTokeniser.TokenType.Number,
                    PRTokeniser.TokenType.EndDic,
                    PRTokeniser.TokenType.EndOfFile
            );
        }

        @Test
        public void primitivesTest() throws Exception {
            String data = "<</Size 70." +
                    "/Value#20 .1" +
                    "/Root 46 0 R" +
                    "/Info 44 0 R" +
                    "/ID[<736f6d652068657820737472696e672>(some simple string )<8C2547D58D4BD2C6F3D32B830BE3259D2>-70.1--0.2]" +
                    "/Prev ---116.23 >>";
            RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
            PRTokeniser tok = new PRTokeniser(new RandomAccessFileOrArray(factory.createSource(data.getBytes())));

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.StartDic);

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Name);
            PdfName name = new PdfName(tok.getByteContent());
            Assert.assertEquals("Size", name.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Number);
            PdfNumber num = new PdfNumber(tok.getByteContent());
            Assert.assertEquals("70", num.toString());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Name);
            name = new PdfName(tok.getByteContent());
            Assert.assertEquals("Value ", name.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Number);
            num = new PdfNumber(tok.getByteContent());
            Assert.assertNotSame("0.1", num.toString());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Name);
            name = new PdfName(tok.getByteContent());
            Assert.assertEquals("Root", name.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Ref);
            PdfIndirectReference ref = new PdfIndirectReference(null, tok.getReference(), tok.getGeneration(), null);
            Assert.assertEquals("46 0 R", ref.toString());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Name);
            name = new PdfName(tok.getByteContent());
            Assert.assertEquals("Info", name.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Ref);
            ref = new PdfIndirectReference(null, tok.getReference(), tok.getGeneration(), null);
            Assert.assertEquals("44 0 R", ref.toString());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Name);
            name = new PdfName(tok.getByteContent());
            Assert.assertEquals("ID", name.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.StartArray);

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.String);
            Assert.assertSame(tok.isHexString(), true);
            PdfString str = new PdfString(tok.getByteContent(), tok.isHexString());
            Assert.assertEquals("some hex string ", str.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.String);
            Assert.assertSame(tok.isHexString(), false);
            str = new PdfString(tok.getByteContent(), tok.isHexString());
            Assert.assertEquals("some simple string ", str.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.String);
            Assert.assertSame(tok.isHexString(), true);
            str = new PdfString(tok.getByteContent(), tok.isHexString());
            Assert.assertEquals("\u008C%GÕ\u008DKÒÆóÓ+\u0083\u000Bã%\u009D ", str.toString());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Number);
            num = new PdfNumber(tok.getByteContent());
            Assert.assertEquals("-70.1", num.toString());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Number);
            num = new PdfNumber(tok.getByteContent());
            Assert.assertEquals("0.2", num.toString());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.EndArray);

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Name);
            name = new PdfName(tok.getByteContent());
            Assert.assertEquals("Prev", name.getValue());

            tok.nextValidToken();
            Assert.assertSame(tok.getTokenType(), PRTokeniser.TokenType.Number);
            num = new PdfNumber(tok.getByteContent());
            Assert.assertEquals("-116.23", num.toString());
        }
    }
}
