package com.itextpdf.barcodes;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.text.pdf.Barcode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

public class Barcode128Test {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/Barcode128/";
    static final public String destinationFolder = "./target/test/com/itextpdf/barcodes/Barcode128/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {

        String filename = "barcode01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new Barcode128(document);
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode("9781935182610");

        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, Color.Black, Color.Black);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {

        String filename = "barcode02.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder + "DocumentWithTrueTypeFont1.pdf"));
        PdfDocument document = new PdfDocument(reader, writer);

        PdfCanvas canvas = new PdfCanvas(document.getLastPage());

        Barcode1D barcode = new Barcode128(document);
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode("9781935182610");

        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, Color.Black, Color.Black);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
}
