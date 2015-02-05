package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.layer.PdfLayer;
import com.itextpdf.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfLayerTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfLayerTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfLayerTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void testInStamperMode1() throws IOException, PdfException, InterruptedException, DocumentException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "input_layered.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "output_copy_layered.pdf")));
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "output_copy_layered.pdf", sourceFolder + "input_layered.pdf", destinationFolder, "diff"));
    }

    @Test
    public void testInStamperMode2() throws IOException, PdfException, InterruptedException, DocumentException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "input_layered.pdf")),
                new PdfWriter(new FileOutputStream(destinationFolder + "output_layered.pdf")));

        PdfCanvas canvas = new PdfCanvas(pdfDoc, 1);

        PdfLayer newLayer = new PdfLayer("appended", pdfDoc);
        canvas.beginLayer(newLayer).beginText().setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 18).
                moveText(200, 600).showText("APPENDED CONTENT").endText().endLayer();

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();
        for (PdfLayer layer : allLayers) {
            if (layer.isLocked())
                layer.setLocked(false);
            if ("Grouped layers".equals(layer.getTitle())) {
                layer.addChild(newLayer);
            }
        }

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "output_layered.pdf", sourceFolder + "cmp_output_layered.pdf", destinationFolder, "diff"));
    }

}
