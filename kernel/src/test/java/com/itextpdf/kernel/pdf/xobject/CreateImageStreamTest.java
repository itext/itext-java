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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CreateImageStreamTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/xobject/CreateImageStreamTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/xobject/CreateImageStreamTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void compareColorspacesTest() throws IOException, InterruptedException {
        String[] imgFiles = new String[] {
                "adobe.png",
                "anon.gif",
                "anon.jpg",
                "anon.png",
                "gamma.png",
                "odd.png",
                "rec709.jpg",
                "srgb.jpg",
                "srgb.png",
        };

        String out = destinationFolder + "compareColorspacesTest.pdf";
        String cmp = sourceFolder + "cmp_compareColorspacesTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(out));
        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        canvas.beginText().moveText(40, 730).setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("The images below are in row and expected to form four continuous lines of constant colors.")
            .endText();
        for (int i = 0; i < imgFiles.length; i++) {
            String imgFile = imgFiles[i];
            PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "compare_colorspaces/" + imgFile));
            canvas.addXObject(imageXObject, 50 + i*40, 550, 40);
        }

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder, "diff_"));
    }

    @Test
    public void createDictionaryFromMapIntArrayTest() throws IOException, InterruptedException {
        testSingleImage("createDictionaryFromMapIntArrayTest.png");
    }

    @Test
    public void imgCalrgb() throws IOException, InterruptedException {
        testSingleImage("img_calrgb.png");
    }

    @Test
    public void imgCmyk() throws IOException, InterruptedException {
        testSingleImage("img_cmyk.tif");
    }

    @Test
    public void imgCmykIcc() throws IOException, InterruptedException {
        testSingleImage("img_cmyk_icc.tif");
    }

    @Test
    public void imgIndexed() throws IOException, InterruptedException {
        testSingleImage("img_indexed.png");
    }

    @Test
    public void imgRgb() throws IOException, InterruptedException {
        testSingleImage("img_rgb.png");
    }

    @Test
    public void imgRgbIcc() throws IOException, InterruptedException {
        testSingleImage("img_rgb_icc.png");
    }

    private void testSingleImage(String imgName) throws IOException, InterruptedException {
        String out = destinationFolder + imgName.substring(0, imgName.length() - 4) + ".pdf";
        String cmp = sourceFolder + "cmp_" + imgName.substring(0, imgName.length() - 4) + ".pdf";

        ImageData img = ImageDataFactory.create(sourceFolder + imgName);
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(out));
        PdfImageXObject imageXObject = new PdfImageXObject(img);
        new PdfCanvas(pdfDocument.addNewPage(new PageSize(img.getWidth(), img.getHeight())))
                .addXObject(imageXObject, 0, 0, img.getWidth());
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder, "diff_"));
    }
}
