package com.itextpdf.barcodes;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.testutils.CompareTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BarcodePDF417Test {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    static final public String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodePDF417/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {

        String filename = "barcode417_01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
        + "precisely --having little or no money in my purse, and nothing "
        + "particular to interest me on shore, I thought I would sail about "
        + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setText(text);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {

        String filename = "barcode417_02.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder + "DocumentWithTrueTypeFont1.pdf"));
        PdfDocument document = new PdfDocument(reader, writer);

        PdfCanvas canvas = new PdfCanvas(document.getLastPage());

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setText(text);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
}
