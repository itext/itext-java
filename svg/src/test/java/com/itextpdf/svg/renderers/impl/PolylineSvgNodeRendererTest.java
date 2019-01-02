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
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PolylineSvgNodeRendererTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/PolylineSvgNodeRendererTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/PolylineSvgNodeRendererTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
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
        Assert.assertNull(new CompareTool().compareVisually(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void polyLineInvalidAttributeTest01() {
        junitExpectedException.expect(StyledXMLParserException.class);

        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        ISvgNodeRenderer root = new PolylineSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgConstants.Attributes.POINTS, "0,0 notAnum,alsoNotANum");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        root.draw(context);
    }

    @Test
    public void polyLineInvalidAttributeTest02() {
        junitExpectedException.expect(SvgProcessingException.class);

        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        ISvgNodeRenderer root = new PolylineSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgConstants.Attributes.POINTS, "0,0 100,100 5, 20,30");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        root.draw(context);
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
        Assert.assertEquals(numPoints, 0);
        Assert.assertNull(new CompareTool().compareVisually(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
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

        Assert.assertEquals(expectedPoints.size(), attributePoints.size());
        for (int x = 0; x < attributePoints.size(); x++) {
            Assert.assertEquals(expectedPoints.get(x), attributePoints.get(x));
        }

    }

    @Test
    public void deepCopyTest(){
        PolylineSvgNodeRenderer expected = new PolylineSvgNodeRenderer();
        expected.setAttribute(SvgConstants.Attributes.FILL,"blue");
        ISvgNodeRenderer actual =expected.createDeepCopy();
        Assert.assertEquals(expected,actual);
    }

}
