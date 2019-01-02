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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;

@Category(IntegrationTest.class)
public class PdfLayerTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfLayerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfLayerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void testInStamperMode1() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "input_layered.pdf"),
                new PdfWriter(destinationFolder + "output_copy_layered.pdf"));
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "output_copy_layered.pdf", sourceFolder + "input_layered.pdf", destinationFolder, "diff"));
    }

    @Test
    public void testInStamperMode2() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "input_layered.pdf"),
                new PdfWriter(destinationFolder + "output_layered.pdf"));

        PdfCanvas canvas = new PdfCanvas(pdfDoc, 1);

        PdfLayer newLayer = new PdfLayer("appended", pdfDoc);
        canvas.beginLayer(newLayer).beginText().setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18).
                moveText(200, 600).showText("APPENDED CONTENT").endText().endLayer();

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();
        for (PdfLayer layer : allLayers) {
            if (layer.isLocked())
                layer.setLocked(false);
            if ("Grouped layers".equals(layer.getTitle())) {
                layer.addChild(newLayer);
            }
        }

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "output_layered.pdf", sourceFolder + "cmp_output_layered.pdf", destinationFolder, "diff"));
    }

}
