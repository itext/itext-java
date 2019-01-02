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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class PdfDocumentInfoTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentInfoTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentInfoTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void documentInfoCreatePdf20() throws IOException, InterruptedException {
        String outFile = destinationFolder + "test01.pdf";
        String cmpFile = sourceFolder + "cmp_test01.pdf";

        PdfDocument document = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        document.addNewPage();
        document.getDocumentInfo().setAuthor("Alexey");
        document.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
        Assert.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assert.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

    @Test
    public void documentInfoTransformPdf17ToPdf20() throws IOException, InterruptedException {
        String inputFile = sourceFolder + "metadata_pdf.pdf";
        String outFile = destinationFolder + "metadata_pdf_20.pdf";
        String cmpFile = sourceFolder + "cmp_metadata_pdf_20.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        document.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
        Assert.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assert.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

    @Test
    public void changeDocumentVersionAndInfoInAppendMode() throws IOException, InterruptedException {
        String inputFile = sourceFolder + "metadata_pdf.pdf";
        String outFile = destinationFolder + "metadata_pdf_20_append.pdf";
        String cmpFile = sourceFolder + "cmp_metadata_pdf_20_append.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(inputFile),
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                new StampingProperties().useAppendMode());
        document.getDocumentInfo().setAuthor("Alexey Subach");
        document.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
        Assert.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assert.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

    @Test
    public void readInfoFromMetadata() throws IOException {
        String inputFile = sourceFolder + "cmp_metadata_pdf_20.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(inputFile));

        String author = document.getDocumentInfo().getAuthor();
        String subject = document.getDocumentInfo().getSubject();
        String title = document.getDocumentInfo().getTitle();

        document.close();

        Assert.assertEquals("Author", "Bruno Lowagie", author);
        Assert.assertEquals("Title", "Hello World example", title);
        Assert.assertEquals("Subject", "This example shows how to add metadata", subject);
    }

    @Test
    public void changeMetadataInAppendMode() throws IOException, InterruptedException {
        String inputFile = sourceFolder + "cmp_metadata_pdf_20.pdf";
        String outFile = destinationFolder + "metadata_pdf_20_changed_append.pdf";
        String cmpFile = sourceFolder + "cmp_metadata_pdf_20_changed_append.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outFile), new StampingProperties().useAppendMode());
        document.getDocumentInfo().setAuthor("Alexey Subach");
        document.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
        Assert.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assert.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

    @Test
    public void simpleStampingMetadataLeaveUnchanged() throws IOException, InterruptedException {
        String inputFile = sourceFolder + "cmp_metadata_pdf_20_changed_append.pdf";
        String outFile = destinationFolder + "metadata_pdf_20_unchanged_stamper.pdf";
        String cmpFile = sourceFolder + "cmp_metadata_pdf_20_changed_append.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outFile), new StampingProperties());
        String author = document.getDocumentInfo().getAuthor();
        document.close();

        Assert.assertEquals("Author", "Bruno Lowagie; Alexey Subach", author);

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
        Assert.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assert.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

}
