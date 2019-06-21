package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.svg.converter.SvgConverter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestUtils {

    public static void convertSVGtoPDF(String pdfFilePath, String svgFilePath, int PageNo, PageSize pageSize) throws IOException {

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(pdfFilePath),
                new WriterProperties().setCompressionLevel(0)));
        PageSize format = new PageSize(pageSize);
        pdfDocument.addNewPage(format.rotate());
        SvgConverter.drawOnDocument(new FileInputStream(svgFilePath), pdfDocument, PageNo);

        pdfDocument.close();
    }
}
