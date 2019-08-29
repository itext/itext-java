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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.impl.GroupSvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SvgDrawContextTest extends ExtendedITextTest {

    private PdfDocument tokenDoc;
    private PdfCanvas page1, page2;
    private SvgDrawContext context;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void setUp() {
        tokenDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        page1 = new PdfCanvas(tokenDoc.addNewPage());
        page2 = new PdfCanvas(tokenDoc.addNewPage());
        context = new SvgDrawContext(null, null);
    }

    @After
    public void tearDown() {
        // release all resources
        tokenDoc.close();
    }

    @Test
    public void drawContextEmptyStackPeekTest() {
        junitExpectedException.expect(EmptyStackException.class);
        context.getCurrentCanvas();
    }

    @Test
    public void drawContextEmptyStackPopTest() {
        junitExpectedException.expect(EmptyStackException.class);
        context.popCanvas();
    }
    
    @Test
    public void drawContextEmptyStackCountTest() {
        Assert.assertEquals(0, context.size());
    }
    
    @Test
    public void drawContextPushCountTest() {
        context.pushCanvas(page1);
        Assert.assertEquals(1, context.size());
    }
    
    @Test
    public void drawContextPushPeekTest() {
        context.pushCanvas(page1);
        Assert.assertEquals(page1, context.getCurrentCanvas());
    }
    
    @Test
    public void drawContextPushPopCountTest() {
        context.pushCanvas(page1);
        context.popCanvas();
        Assert.assertEquals(0, context.size());
    }
    
    @Test
    public void drawContextPushPopTest() {
        context.pushCanvas(page1);
        Assert.assertEquals(page1, context.popCanvas());
    }
    
    @Test
    public void drawContextPushTwiceCountTest() {
        context.pushCanvas(page1);
        context.pushCanvas(page2);
        Assert.assertEquals(2, context.size());
    }
    
    @Test
    public void drawContextPushTwicePeekTest() {
        context.pushCanvas(page1);
        context.pushCanvas(page2);
        Assert.assertEquals(page2, context.getCurrentCanvas());
        Assert.assertEquals(2, context.size());
    }
    
    @Test
    public void drawContextPushTwicePopTest() {
        context.pushCanvas(page1);
        context.pushCanvas(page2);
        Assert.assertEquals(page2, context.popCanvas());
        Assert.assertEquals(1, context.size());
        Assert.assertEquals(page1, context.popCanvas());
    }

    @Test
    public void addISvgNodeRender() {
        String name = "expected";
        ISvgNodeRenderer expected = new GroupSvgNodeRenderer();
        this.context.addNamedObject(name, expected);
        Object actual = this.context.getNamedObject(name);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addNullToNamedObjects() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.NAMED_OBJECT_NULL);

        String name = "expected";
        this.context.addNamedObject(name, null);
    }

    @Test
    public void addNamedObjectWithNullName() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.NAMED_OBJECT_NAME_NULL_OR_EMPTY);

        ISvgNodeRenderer expected = new DummySvgNodeRenderer();
        this.context.addNamedObject(null, expected);
    }

    @Test
    public void addNamedObjectWithEmptyName() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.NAMED_OBJECT_NAME_NULL_OR_EMPTY);

        ISvgNodeRenderer expected = new DummySvgNodeRenderer();
        this.context.addNamedObject("", expected);
    }

    @Test
    public void addNamedRenderer() {
        ISvgNodeRenderer expected = new DummySvgNodeRenderer();
        String dummyName = "dummy";
        this.context.addNamedObject(dummyName, expected);
        Object actual = this.context.getNamedObject(dummyName);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addNamedObjects(){
        ISvgNodeRenderer expectedOne = new DummySvgNodeRenderer();
        ISvgNodeRenderer expectedTwo = new DummySvgNodeRenderer();
        ISvgNodeRenderer expectedThree = new DummySvgNodeRenderer();
        String dummyNameOne = "Ed";
        String dummyNameTwo="Edd";
        String dummyNameThree="Eddy";
        Map<String,ISvgNodeRenderer> toAdd = new HashMap<>();
        toAdd.put(dummyNameOne,expectedOne);
        toAdd.put(dummyNameTwo,expectedTwo);
        toAdd.put(dummyNameThree,expectedThree);
        this.context.addNamedObjects(toAdd);
        Object actualThree = this.context.getNamedObject(dummyNameThree);
        Object actualTwo = this.context.getNamedObject(dummyNameTwo);
        Object actualOne = this.context.getNamedObject(dummyNameOne);
        Assert.assertEquals(expectedOne, actualOne);
        Assert.assertEquals(expectedTwo, actualTwo);
        Assert.assertEquals(expectedThree, actualThree);
    }

    @Test
    public void addNamedObjectAndTryToAddDuplicate(){
        ISvgNodeRenderer expectedOne = new DummySvgNodeRenderer();
        ISvgNodeRenderer expectedTwo = new DummySvgNodeRenderer();
        String dummyName = "Ed";

        context.addNamedObject(dummyName,expectedOne);
        context.addNamedObject(dummyName,expectedTwo);
        Object actual = context.getNamedObject(dummyName);
        Assert.assertEquals(expectedOne,actual);

    }
}
