/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.renderers;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class SvgIntegrationTest extends ExtendedITextTest {

    public void convert(InputStream svg, OutputStream pdfOutputStream) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfWriter(pdfOutputStream, new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        SvgConverter.drawOnDocument(svg, doc, 1);

        doc.close();
    }

    public void convert(String svg, String output) throws IOException {
        convert(svg, output, PageSize.DEFAULT);
    }

    public void convert(String svg, String output, PageSize size) throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(output, new WriterProperties().setCompressionLevel(0)))) {
            doc.addNewPage(size);
            ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(svg);
            SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(svg), doc, 1, properties);
        }
    }

    public static PdfDocument convertWithResult(String svg, String output) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfWriter(output, new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(svg);
        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(svg), doc, 1, properties);
        return doc;
    }

    public void convertToSinglePage(InputStream svg, OutputStream pdfOutputStream) throws IOException {
        WriterProperties writerprops = new WriterProperties().setCompressionLevel(0);
        SvgConverter.createPdf(svg, pdfOutputStream, writerprops);
    }

    public void convertToSinglePage(File svg, File pdf) throws IOException {
        SvgConverter.createPdf(svg, pdf);
    }

    public void convertToSinglePage(File svg, File pdf, ISvgConverterProperties properties) throws IOException {
        SvgConverter.createPdf(svg, pdf, properties);
    }

    public void convertToSinglePage(File svg, File pdf, ISvgConverterProperties properties, WriterProperties writerProperties) throws IOException {
        SvgConverter.createPdf(svg, pdf, properties, writerProperties);
    }

    public void convertToSinglePage(File svg, File pdf, WriterProperties writerProperties) throws IOException {
        SvgConverter.createPdf(svg, pdf, writerProperties);
    }

    public void convertToSinglePage(InputStream svg, OutputStream pdfOutputStream, ISvgConverterProperties properties) throws IOException {
        SvgConverter.createPdf(svg, pdfOutputStream, properties);
    }

    public void convertToSinglePage(InputStream svg, OutputStream pdfOutputStream, ISvgConverterProperties properties, WriterProperties writerprops) throws IOException {
        SvgConverter.createPdf(svg, pdfOutputStream, properties, writerprops);
    }

    public void convertAndCompare(String src, String dest, String fileName) throws IOException, InterruptedException {
        convertAndCompare(src, dest, fileName, PageSize.DEFAULT);
    }

    public void convertAndCompare(String src, String dest, String fileName, PageSize size) throws IOException, InterruptedException {
        convert(src + fileName + ".svg", dest + fileName + ".pdf", size);
        compare(fileName, src, dest);
    }

    public void convertAndCompareSinglePage(String src, String dest, String fileName) throws IOException, InterruptedException {
        convertToSinglePage(FileUtil.getInputStreamForFile(src + fileName + ".svg"),
                FileUtil.getFileOutputStream(dest + fileName + ".pdf"));
        compare(fileName, src, dest);
    }

    public void convertAndCompareSinglePage(String src, String dest, String fileName, ISvgConverterProperties properties) throws IOException, InterruptedException {
        convertToSinglePage(FileUtil.getInputStreamForFile(src + fileName + ".svg"),
                FileUtil.getFileOutputStream(dest + fileName + ".pdf"), properties);
        compare(fileName, src, dest);
    }

    protected void compare(String filename, String sourceFolder, String destinationFolder) throws IOException, InterruptedException {
        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename + ".pdf",
                        sourceFolder + "cmp_" + filename + ".pdf",
                        destinationFolder, "diff_"));
    }
}
