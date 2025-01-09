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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PolylineSvgNodeRendererTest extends SvgIntegrationTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/PolylineSvgNodeRendererTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/PolylineSvgNodeRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void polylineRendererTest() throws IOException, InterruptedException {
        String filename = "polylineRendererTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        ISvgNodeRenderer root = new PolylineSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgConstants.Attributes.POINTS, "0,40 40,40 40,80 80,80 80,120 120,120 120,160");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();
        Assertions.assertNull(new CompareTool().compareVisually(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void polyLineInvalidAttributeTest01() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        ISvgNodeRenderer root = new PolylineSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgConstants.Attributes.POINTS, "0,0 notAnum,alsoNotANum");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        Assertions.assertThrows(StyledXMLParserException.class, () -> root.draw(context));
    }

    @Test
    public void polyLineInvalidAttributeTest02() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        ISvgNodeRenderer root = new PolylineSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgConstants.Attributes.POINTS, "0,0 100,100 5, 20,30");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        Assertions.assertThrows(SvgProcessingException.class, () -> root.draw(context));
    }

    @Test
    public void polyLineEmptyPointsListTest() throws IOException, InterruptedException {
        String filename = "polyLineEmptyPointsListTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        ISvgNodeRenderer root = new PolylineSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();

        int numPoints = ((PolylineSvgNodeRenderer) root).getPoints().size();
        Assertions.assertEquals(numPoints, 0);
        Assertions.assertNull(new CompareTool().compareVisually(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void polyPointCheckerTest() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        ISvgNodeRenderer root = new PolylineSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgConstants.Attributes.POINTS, "0,0 100,100 200,200 300,300");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        root.draw(context);

        List<Point> expectedPoints = new ArrayList<>();
        expectedPoints.add(new Point(0, 0));
        expectedPoints.add(new Point(75, 75));
        expectedPoints.add(new Point(150, 150));
        expectedPoints.add(new Point(225, 225));
        List<Point> attributePoints = ((PolylineSvgNodeRenderer) root).getPoints();

        Assertions.assertEquals(expectedPoints.size(), attributePoints.size());
        for (int x = 0; x < attributePoints.size(); x++) {
            Assertions.assertEquals(expectedPoints.get(x), attributePoints.get(x));
        }

    }

    @Test
    public void elementDimensionExceedsViewboxBoundaryTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "elementDimensionExceedsViewboxBoundary");
    }
}
