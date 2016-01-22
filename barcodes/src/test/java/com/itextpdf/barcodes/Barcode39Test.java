package com.itextpdf.barcodes;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.utils.CompareTool;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class Barcode39Test {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    static final public String destinationFolder = "./target/test/com/itextpdf/barcodes/Barcode39/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {

        String filename = "barcode39_01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new Barcode39(document);
        barcode.setCode("9781935182610");

        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, Color.BLACK, Color.BLACK);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {

        String filename = "barcode39_02.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder + "DocumentWithTrueTypeFont1.pdf"));
        PdfDocument document = new PdfDocument(reader, writer);

        PdfCanvas canvas = new PdfCanvas(document.getLastPage());

        Barcode1D barcode = new Barcode39(document);
        barcode.setCode("9781935182610");

        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, Color.BLACK, Color.BLACK);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void barcode03Test() {

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(writer);
        Barcode39 barcode = new Barcode39(document);
        barcode.getBarsCode39("9781935*182610");


    }
}
