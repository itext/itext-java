package com.itextpdf.barcodes;


import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.utils.CompareTool;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BarcodePostnetTest {


    static final public String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    static final public String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodePostnet/";


    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {

        String filename = "barcodePostnet.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePostnet codePlanet = new BarcodePostnet(document);
        codePlanet.setCode("01234567890");
        codePlanet.setCodeType(BarcodePostnet.TYPE_PLANET);
        codePlanet.placeBarcode(canvas, null, null);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

}
