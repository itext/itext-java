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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class LineSvgNodeRendererTest extends SvgIntegrationTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/LineSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/LineSvgNodeRendererTest/";


    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void lineRendererTest() throws IOException, InterruptedException {
        String filename = "lineSvgRendererTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        Map<String, String> lineProperties = new HashMap<>();

        lineProperties.put("x1", "100");
        lineProperties.put("y1", "800");
        lineProperties.put("x2", "300");
        lineProperties.put("y2", "800");
        lineProperties.put("stroke", "green");
        lineProperties.put("stroke-width", "25");

        LineSvgNodeRenderer root = new LineSvgNodeRenderer();
        root.setAttributesAndStyles(lineProperties);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void lineWithEmpyAttributesTest() throws IOException, InterruptedException {
        String filename = "lineWithEmpyAttributesTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        Map<String, String> lineProperties = new HashMap<>();

        LineSvgNodeRenderer root = new LineSvgNodeRenderer();
        root.setAttributesAndStyles(lineProperties);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void invalidAttributeTest01() throws IOException, InterruptedException {
        String filename = "invalidAttributeTest01.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();
        ISvgNodeRenderer root = new LineSvgNodeRenderer();
        Map<String, String> lineProperties = new HashMap<>();
        lineProperties.put("x1", "1");
        lineProperties.put("y1", "800");
        lineProperties.put("x2", "notAnum");
        lineProperties.put("y2", "alsoNotANum");
        root.setAttributesAndStyles(lineProperties);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        root.draw(context);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }


    @Test
    public void invalidAttributeTest02() throws IOException {
        Map<String, String> lineProperties = new HashMap<>();
        lineProperties.put("x1", "100");
        lineProperties.put("y1", "800");
        lineProperties.put("x2", "1 0");
        lineProperties.put("y2", "0 2 0");
        lineProperties.put("stroke", "orange");


        String filename = "invalidAttributes02.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        LineSvgNodeRenderer root = new LineSvgNodeRenderer();
        root.setAttributesAndStyles(lineProperties);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);

        doc.close();
    }

    @Test
    public void emptyPointsListTest() throws IOException, InterruptedException {
        String filename = "lineEmptyPointsListTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        ISvgNodeRenderer root = new LineSvgNodeRenderer();
        Map<String, String> lineProperties = new HashMap<>();
        root.setAttributesAndStyles(lineProperties);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();

        int numPoints = ((LineSvgNodeRenderer) root).attributesAndStyles.size();
        Assertions.assertEquals(numPoints, 0);
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void getAttributeTest() {
        float expected = 0.75f;
        LineSvgNodeRenderer lineSvgNodeRenderer = new LineSvgNodeRenderer();

        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "1.0");

        float actual = lineSvgNodeRenderer.getAttribute(attributes, "key");

        Assertions.assertEquals(expected, actual, 0f);
    }

    @Test
    public void getNotPresentAttributeTest() {
        float expected = 0f;
        LineSvgNodeRenderer lineSvgNodeRenderer = new LineSvgNodeRenderer();

        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "1.0");

        float actual = lineSvgNodeRenderer.getAttribute(attributes, "notHere");

        Assertions.assertEquals(expected, actual, 0f);
    }

}
