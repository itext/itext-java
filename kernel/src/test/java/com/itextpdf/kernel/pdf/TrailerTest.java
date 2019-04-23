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

import com.itextpdf.io.util.FileUtil;
import com.itextpdf.kernel.ProductInfo;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Michael Demey
 */
@Category(IntegrationTest.class)
public class TrailerTest extends ExtendedITextTest {

    private ProductInfo productInfo;
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/TrailerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Before
    public void beforeTest() {
        this.productInfo = new ProductInfo("pdfProduct", 1, 0, 0, true);
    }

    @Test
    public void trailerFingerprintTest() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "output.pdf");
        PdfDocument pdf = new PdfDocument(new PdfWriter(fos));
        pdf.registerProduct(this.productInfo);
        PdfPage page = pdf.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(), 12f)
                .showText("Hello World")
                .endText();

        pdf.close();

        Assert.assertTrue(doesTrailerContainFingerprint(new File(destinationFolder + "output.pdf"), productInfo.toString()));
    }

    private boolean doesTrailerContainFingerprint(File file, String fingerPrint) throws IOException {
        RandomAccessFile raf = FileUtil.getRandomAccessFile(file);

        // put the pointer at the end of the file
        raf.seek(raf.length());

        // look for startxref
        String startxref = "startxref";
        String templine = "";

        while ( ! templine.contains(startxref) ) {
            templine = (char) raf.read() + templine;
            raf.seek(raf.getFilePointer() - 2);
        }

        // look for fingerprint
        char read = ' ';
        templine = "";

        while ( read != '%' ) {
            read = (char) raf.read();
            templine = read + templine;
            raf.seek(raf.getFilePointer() - 2);
        }

        boolean output = templine.contains(fingerPrint);
        raf.close();
        return output;
    }

}
