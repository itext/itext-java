package com.itextpdf.barcodes;

import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.ErrorCorrectionLevel;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BarcodeQRCodeTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    public static final String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodeQRCode/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeQRCode01.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BarcodeQRCode barcode = new BarcodeQRCode("some specific text 239214 hello world");
        barcode.placeBarcode(canvas, Color.GRAY, 12);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeQRCode02.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BarcodeQRCode barcode1 = new BarcodeQRCode("дима", hints);
        barcode1.placeBarcode(canvas, Color.GRAY, 12);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

}
