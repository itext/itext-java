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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
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
public class PdfLayerMembershipTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/layer/PdfLayerMembershipTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/layer/PdfLayerMembershipTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }


    @Test
    public void enabledVisibilityPolicyAllOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAllOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOn.addLayer(allLayers.get(1));
        layerMembershipAllOn.addLayer(allLayers.get(2));
        layerMembershipAllOn.setVisibilityPolicy(PdfName.AllOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOn, canvas, "visibilityPolicyAllOnTest", 200, 500);


        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }


    @Test
    public void disabledVisibilityPolicyAllOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAllOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOn.addLayer(allLayers.get(1));
        layerMembershipAllOn.addLayer(allLayers.get(0));
        layerMembershipAllOn.setVisibilityPolicy(PdfName.AllOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOn, canvas, "visibilityPolicyAllOnTest", 200, 500);


        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void enabledVisibilityPolicyAllOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAllOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOff = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOff.addLayer(allLayers.get(0));
        layerMembershipAllOff.addLayer(allLayers.get(3));
        layerMembershipAllOff.setVisibilityPolicy(PdfName.AllOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOff, canvas, "visibilityPolicyAllOffTest", 200, 500);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void disabledVisibilityPolicyAllOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAllOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOff = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOff.addLayer(allLayers.get(0));
        layerMembershipAllOff.addLayer(allLayers.get(1));
        layerMembershipAllOff.setVisibilityPolicy(PdfName.AllOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOff, canvas, "visibilityPolicyAllOffTest", 200, 500);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void enabledVisibilityPolicyAnyOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAnyOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOnTest", 200, 500);


        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }


    @Test
    public void disabledVisibilityPolicyAnyOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAnyOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(3));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOnTest", 200, 500);


        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }


    @Test
    public void enabledVisibilityPolicyAnyOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAnyOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOffTest", 200, 500);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void disabledVisibilityPolicyAnyOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAnyOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.addLayer(allLayers.get(2));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOffTest", 200, 500);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void enabledVisualExpressionTest01() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisualExpressionTest01.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);

        // create expression with the AND operator as the first operand
        PdfVisibilityExpression expression = new PdfVisibilityExpression(PdfName.And);
        // add an empty dictionary as the second operand
        expression.addOperand(allLayers.get(1));
        // create a nested expression with the OR operator and two empty dictionaries as operands
        PdfVisibilityExpression nestedExpression = new PdfVisibilityExpression(PdfName.Or);
        nestedExpression.addOperand(allLayers.get(0));
        nestedExpression.addOperand(allLayers.get(2));
        // add another expression as the third operand
        expression.addOperand(nestedExpression);


        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.setVisibilityExpression(expression);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visualExpressionTest01", 200, 500);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void disabledVisualExpressionTest01() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisualExpressionTest01.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                new PdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);

        // create expression with the AND operator as the first operand
        PdfVisibilityExpression expression = new PdfVisibilityExpression(PdfName.And);
        // add an empty dictionary as the second operand
        expression.addOperand(allLayers.get(1));
        // create a nested expression with the AND operator and two empty dictionaries as operands
        PdfVisibilityExpression nestedExpression = new PdfVisibilityExpression(PdfName.And);
        nestedExpression.addOperand(allLayers.get(0));
        nestedExpression.addOperand(allLayers.get(2));
        // add another expression as the third operand
        expression.addOperand(nestedExpression);


        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.setVisibilityExpression(expression);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visualExpressionTest01", 200, 500);

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

}
