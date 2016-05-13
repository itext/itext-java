package com.itextpdf.barcodes;


import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BarcodeInter25Test {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    public static final String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodeInter25/";


    @BeforeClass
    public static void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeInter25.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new BarcodeInter25(document);
        barcode.setGenerateChecksum(true);
        barcode.setCode("41-1200076041-001");
        barcode.setTextAlignment(Barcode1D.ALIGN_CENTER);
        barcode.placeBarcode(canvas, Color.BLUE, Color.GREEN);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

}
