package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.converter.SvgConverter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;

public class SvgNodeRendererTestUtility {

    public static void convert(InputStream svg, OutputStream pdfOutputStream) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfWriter(pdfOutputStream, new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        SvgConverter.drawOnDocument(svg, doc, 1);

        doc.close();
    }

    public static void convertAndCompare(String src, String dest, String fileName) throws IOException, InterruptedException {
        convert(new FileInputStream(src + fileName + ".svg"), new FileOutputStream(dest + fileName + ".pdf"));

        CompareTool compareTool = new CompareTool();
        String compareResult = compareTool.compareByContent(dest + fileName + ".pdf", src + "cmp_" + fileName + ".pdf", dest, "diff_");

        Assert.assertNull(compareResult);
    }
}