/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.Assert;

public class SvgIntegrationTest {

    public void convert(InputStream svg, OutputStream pdfOutputStream) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfWriter(pdfOutputStream, new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        SvgConverter.drawOnDocument(svg, doc, 1);

        doc.close();
    }

    public void convert(String svg, String output) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfWriter(output, new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(svg);
        SvgConverter.drawOnDocument(new FileInputStream(svg), doc, 1, properties);

        doc.close();
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

    public void convertAndCompareVisually(String src, String dest, String fileName) throws IOException, InterruptedException {
        convert(src + fileName + ".svg", dest + fileName + ".pdf");
        compare(fileName, src, dest);
    }

    public void convertAndCompareSinglePageStructurally(String src, String dest, String fileName) throws IOException, InterruptedException {
        convertToSinglePage(new FileInputStream(src + fileName + ".svg"), new FileOutputStream(dest + fileName + ".pdf"));
        compare(fileName, src, dest);
    }

    public void convertAndCompareSinglePageStructurally(String src, String dest, String fileName, ISvgConverterProperties properties) throws IOException, InterruptedException {
        convertToSinglePage(new FileInputStream(src + fileName + ".svg"), new FileOutputStream(dest + fileName + ".pdf"), properties);
        compare(fileName, src, dest);
    }

    public void convertAndCompareSinglePageVisually(String src, String dest, String fileName) throws IOException, InterruptedException {
        convertToSinglePage(new FileInputStream(src + fileName + ".svg"), new FileOutputStream(dest + fileName + ".pdf"));
        compare(fileName, src, dest);
    }

    public void convertAndCompareSinglePageVisually(String src, String dest, String fileName, ISvgConverterProperties properties) throws IOException, InterruptedException {
        convertToSinglePage(new FileInputStream(src + fileName + ".svg"), new FileOutputStream(dest + fileName + ".pdf"), properties);
        compare(fileName, src, dest);
    }

    public void convertAndCompareStructurally(String src, String dest, String fileName) throws IOException, InterruptedException {
        convert(new FileInputStream(src + fileName + ".svg"), new FileOutputStream(dest + fileName + ".pdf"));
        compare(fileName, src, dest);
    }

    protected void compare(String filename, String sourceFolder, String destinationFolder) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(destinationFolder + filename + ".pdf", sourceFolder + "cmp_" + filename + ".pdf", destinationFolder, "diff_");

        if (result != null && !result.contains("No visual differences")) {
            Assert.fail(result);
        }
    }
}
