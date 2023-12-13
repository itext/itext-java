/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
