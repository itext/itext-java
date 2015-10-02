package com.itextpdf.barcodes;


import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BarcodeDataMatrixTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    static final public String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodeDataMatrix/";


    @BeforeClass
    static public void beforeClass() {
        File dir = new File(destinationFolder);
        dir.mkdirs();
        for (File file : dir.listFiles())
            file.delete();
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        BarcodeDataMatrix barcode = new BarcodeDataMatrix();
        barcode.setCode("AAAAAAAAAA;BBBBAAAA3;00028;BBBAA05;AAAA;AAAAAA;1234567;AQWXSZ;JEAN;;;;7894561;AQWXSZ;GEO;;;;1;1;1;1;0;0;1;0;1;0;0;0;1;0;1;0;0;0;0;0;0;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1");
        barcode.placeBarcode(canvas, Color.GREEN, 5);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

}
