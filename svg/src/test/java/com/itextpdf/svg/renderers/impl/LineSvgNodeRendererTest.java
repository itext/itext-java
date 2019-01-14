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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class LineSvgNodeRendererTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/LineSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/LineSvgNodeRendererTest/";


    @BeforeClass
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
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
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
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void invalidAttributeTest01() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(LogMessageConstant.NAN, "notAnum"));

        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
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
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = com.itextpdf.styledxmlparser.LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED))
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

        int numPoints = ( (LineSvgNodeRenderer) root ).attributesAndStyles.size();
        Assert.assertEquals(numPoints, 0);
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void getAttributeTest() {
        float expected = 0.75f;
        LineSvgNodeRenderer lineSvgNodeRenderer = new LineSvgNodeRenderer();

        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "1.0");

        float actual = lineSvgNodeRenderer.getAttribute(attributes, "key");

        Assert.assertEquals(expected, actual, 0f);
    }

    @Test
    public void getNotPresentAttributeTest() {
        float expected = 0f;
        LineSvgNodeRenderer lineSvgNodeRenderer = new LineSvgNodeRenderer();

        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "1.0");

        float actual = lineSvgNodeRenderer.getAttribute(attributes, "notHere");

        Assert.assertEquals(expected, actual, 0f);
    }

    @Test
    public void deepCopyTest(){
        LineSvgNodeRenderer expected = new LineSvgNodeRenderer();
        expected.setAttribute(SvgConstants.Attributes.STROKE,"blue");
        ISvgNodeRenderer actual =expected.createDeepCopy();
        Assert.assertEquals(expected,actual);
    }


}
