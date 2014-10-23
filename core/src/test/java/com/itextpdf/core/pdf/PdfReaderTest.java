package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.OutputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

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

    protected boolean objectTypeEqualTo(PdfObject object, PdfName type) throws PdfException {
        PdfName objectType = ((PdfDictionary)object).getAsName(PdfName.Type);
        return type.equals(objectType);
    }
}
