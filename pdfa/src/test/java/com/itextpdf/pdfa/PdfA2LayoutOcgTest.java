/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfA2LayoutOcgTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2LayoutOcgTest/";

    @Before
    public void configure() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void checkIfOcgForPdfA2Works() throws IOException, InterruptedException {
        String fileName = "createdOcgPdfA.pdf";
        InputStream colorStream = new FileInputStream(sourceFolder + "color/sRGB_CS_profile.icm");
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp/PdfA2LayoutOcgTest/cmp_" + fileName;
        PdfDocument pdfDoc = new PdfADocument(new PdfWriter(outFileName), PdfAConformanceLevel.PDF_A_2A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", colorStream));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        pdfDoc.addNewPage();

        Image image1 = new Image(ImageDataFactory.create(sourceFolder + "images/manualTransparency_for_png.png"));

        PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc, 1);

        Canvas canvas1 = new Canvas(pdfCanvas, pdfDoc, new Rectangle(0, 0, 590, 420));
        PdfLayer imageLayer1 = new PdfLayer("*SomeTest_image$here@.1", pdfDoc);
        imageLayer1.setOn(true);
        pdfCanvas.beginLayer(imageLayer1);
        canvas1.add(image1);
        pdfCanvas.endLayer();

        canvas1.close();

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
    }

}
