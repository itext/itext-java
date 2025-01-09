/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.List;

@Tag("IntegrationTest")
public class PdfLayerMembershipTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/layer/PdfLayerMembershipTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/layer/PdfLayerMembershipTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void enabledVisibilityPolicyAllOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAllOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOn.addLayer(allLayers.get(1));
        layerMembershipAllOn.addLayer(allLayers.get(2));
        layerMembershipAllOn.setVisibilityPolicy(PdfName.AllOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOn, canvas, "visibilityPolicyAllOnTest", 200, 500);


        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }


    @Test
    public void disabledVisibilityPolicyAllOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAllOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOn.addLayer(allLayers.get(1));
        layerMembershipAllOn.addLayer(allLayers.get(0));
        layerMembershipAllOn.setVisibilityPolicy(PdfName.AllOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOn, canvas, "visibilityPolicyAllOnTest", 200, 500);


        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void enabledVisibilityPolicyAllOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAllOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOff = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOff.addLayer(allLayers.get(0));
        layerMembershipAllOff.addLayer(allLayers.get(3));
        layerMembershipAllOff.setVisibilityPolicy(PdfName.AllOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOff, canvas, "visibilityPolicyAllOffTest", 200, 500);

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void disabledVisibilityPolicyAllOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAllOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAllOff = new PdfLayerMembership(pdfDoc);
        layerMembershipAllOff.addLayer(allLayers.get(0));
        layerMembershipAllOff.addLayer(allLayers.get(1));
        layerMembershipAllOff.setVisibilityPolicy(PdfName.AllOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAllOff, canvas, "visibilityPolicyAllOffTest", 200, 500);

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void enabledVisibilityPolicyAnyOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAnyOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOnTest", 200, 500);


        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }


    @Test
    public void disabledVisibilityPolicyAnyOnTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAnyOnTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(3));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOn);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOnTest", 200, 500);


        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }


    @Test
    public void enabledVisibilityPolicyAnyOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisibilityPolicyAnyOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(0));
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOffTest", 200, 500);

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void disabledVisibilityPolicyAnyOffTest() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisibilityPolicyAnyOffTest.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();

        PdfLayerMembership layerMembershipAnyOn = new PdfLayerMembership(pdfDoc);
        layerMembershipAnyOn.addLayer(allLayers.get(1));
        layerMembershipAnyOn.addLayer(allLayers.get(2));
        layerMembershipAnyOn.setVisibilityPolicy(PdfName.AnyOff);

        PdfLayerTestUtils.addTextInsideLayer(layerMembershipAnyOn, canvas, "visibilityPolicyAnyOffTest", 200, 500);

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void enabledVisualExpressionTest01() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "enabledVisualExpressionTest01.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

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
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

    @Test
    public void disabledVisualExpressionTest01() throws IOException, InterruptedException {
        String srcPdf = "sourceWithDifferentLayers.pdf";
        String destPdf = "disabledVisualExpressionTest01.pdf";
        String cmpPdf = "cmp_" + destPdf;

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + srcPdf),
                CompareTool.createTestPdfWriter(destinationFolder + destPdf));

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
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + destPdf,
                sourceFolder + cmpPdf, destinationFolder));
    }

}
