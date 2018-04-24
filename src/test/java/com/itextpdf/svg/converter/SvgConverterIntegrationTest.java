/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.svg.converter;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class SvgConverterIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/converter/SvgConverterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/converter/SvgConverterTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void unusedXObjectIntegrationTest() throws IOException, InterruptedException {
        // This method tests that making an XObject does not, in itself, influence the document it's for.
        PdfDocument doc1 = new PdfDocument(new PdfWriter(destinationFolder + "unusedXObjectIntegrationTest1.pdf"));
        PdfDocument doc2 = new PdfDocument(new PdfWriter(destinationFolder + "unusedXObjectIntegrationTest2.pdf"));
        doc1.addNewPage();
        doc2.addNewPage();

        SvgConverter.convertToXObject("<svg width='100pt' height='100pt' />", doc1);

        doc1.close();
        doc2.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "unusedXObjectIntegrationTest1.pdf", destinationFolder + "unusedXObjectIntegrationTest2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void basicIntegrationTest() throws IOException, InterruptedException {
        String filename = "basicIntegrationTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        PdfFormXObject form = SvgConverter.convertToXObject("<svg width='100pt' height='100pt' />", doc);

        new PdfCanvas(doc.getPage(1)).addXObject(form, new Rectangle(100, 100, 100, 100));
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void nonExistingTagIntegrationTest() throws InterruptedException {
        junitExpectedException.expect(SvgProcessingException.class);
        String contents = "<svg width='100pt' height='100pt'> <nonExistingTag/> </svg>";
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();

        try {
            SvgConverter.convertToXObject(contents, doc);
        } finally {
            doc.close();
        }
    }
}
