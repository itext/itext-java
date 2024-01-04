/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

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

import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    private ProductData productData;
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/TrailerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Before
    public void beforeTest() {
        this.productData = new ProductData("pdfProduct", "pdfProduct", "1.0.0", 1900, 2000);
    }

    @Test
    public void trailerFingerprintTest() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "output.pdf");
        PdfDocument pdf = new PdfDocument(new PdfWriter(fos));
        pdf.registerProduct(this.productData);
        PdfPage page = pdf.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(), 12f)
                .showText("Hello World")
                .endText();

        pdf.close();

        Assert.assertTrue(doesTrailerContainFingerprint(new File(destinationFolder + "output.pdf"), MessageFormatUtil
                .format("%iText-{0}-{1}\n", productData.getProductName(), productData.getVersion())));
    }

    private boolean doesTrailerContainFingerprint(File file, String fingerPrint) throws IOException {
        try (RandomAccessFile raf = FileUtil.getRandomAccessFile(file)) {

            // put the pointer at the end of the file
            raf.seek(raf.length());

            // look for coreProductData
            String coreProductData = "%iText-Core-" + ITextCoreProductData.getInstance().getVersion();
            String templine = "";

            while (!templine.contains(coreProductData)) {
                templine = (char) raf.read() + templine;
                raf.seek(raf.getFilePointer() - 2);
            }

            // look for fingerprint
            char read = ' ';
            templine = "";

            while (read != '%') {
                read = (char) raf.read();
                templine = read + templine;
                raf.seek(raf.getFilePointer() - 2);
            }

            return templine.contains(fingerPrint);
        }
    }
}
