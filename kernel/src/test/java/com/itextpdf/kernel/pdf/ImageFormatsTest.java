package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ImageFormatsTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/ImageFormatsTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/ImageFormatsTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void imagesWithDifferentDepth() throws IOException, InterruptedException {
        //TODO: update after DEVSIX-1934 ticket will be fixed
        String outFileName = destinationFolder + "transparencyTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_transparencyTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        PdfPage page = pdfDocument.addNewPage(PageSize.A3);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.LIGHT_GRAY).fill();
        canvas.rectangle(80, 0, 700, 1200).fill();

        canvas
                .saveState()
                .beginText()
                .moveText(116, 1150)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("8 bit depth PNG")
                .endText()
                .restoreState();
        ImageData img = ImageDataFactory.create(sourceFolder + "manualTransparency_8bit.png");
        canvas.addImage(img, 100, 780, 200, false);


        canvas
                .saveState()
                .beginText()
                .moveText(316, 1150)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("24 bit depth PNG")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_24bit.png");
        canvas.addImage(img, 300, 780, 200, false);


        canvas
                .saveState()
                .beginText()
                .moveText(516, 1150)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("32 bit depth PNG")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_32bit.png");
        canvas.addImage(img, 500, 780, 200, false);

        canvas
                .saveState()
                .beginText()
                .moveText(116, 650)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("GIF image ")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_gif.gif");
        canvas.addImage(img, 100, 300, 200, false);

        canvas
                .saveState()
                .beginText()
                .moveText(316, 650)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("TIF image ")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_tif.tif");
        canvas.addImage(img, 300, 300, 200, false);

        canvas.release();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }
}
