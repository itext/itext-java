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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
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
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
        InputStream colorStream = FileUtil.getInputStreamForFile(sourceFolder + "color/sRGB_CS_profile.icm");
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp/PdfA2LayoutOcgTest/cmp_" + fileName;
        PdfDocument pdfDoc = new PdfADocument(new PdfWriter(outFileName), PdfAConformanceLevel.PDF_A_2A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", colorStream));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        pdfDoc.addNewPage();

        Image image1 = new Image(ImageDataFactory.create(sourceFolder + "images/manualTransparency_for_png.png"));

        PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc, 1);

        Canvas canvas1 = new Canvas(pdfCanvas, new Rectangle(0, 0, 590, 420));
        PdfLayer imageLayer1 = new PdfLayer("*SomeTest_image$here@.1", pdfDoc);
        imageLayer1.setOn(true);
        pdfCanvas.beginLayer(imageLayer1);
        canvas1.add(image1);
        pdfCanvas.endLayer();

        canvas1.close();

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
        Assert.assertNull(new VeraPdfValidator().validate(outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

}
