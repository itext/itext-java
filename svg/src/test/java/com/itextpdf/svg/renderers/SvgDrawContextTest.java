/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.impl.GroupSvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import java.util.NoSuchElementException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgDrawContextTest extends ExtendedITextTest {

    private PdfDocument tokenDoc;
    private PdfCanvas page1, page2;
    private SvgDrawContext context;

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
    public void drawContextEmptyDequeGetFirstTest() {
        Assert.assertThrows(NoSuchElementException.class, () -> context.getCurrentCanvas());
    }

    @Test
    public void drawContextEmptyDequePopTest() {
        Assert.assertThrows(NoSuchElementException.class, () -> context.popCanvas());
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
        String name = "expected";

        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> this.context.addNamedObject(name, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.NAMED_OBJECT_NULL, e.getMessage());
    }

    @Test
    public void addNamedObjectWithNullName() {
        ISvgNodeRenderer expected = new DummySvgNodeRenderer();

        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> this.context.addNamedObject(null, expected)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.NAMED_OBJECT_NAME_NULL_OR_EMPTY, e.getMessage());
    }

    @Test
    public void addNamedObjectWithEmptyName() {
        ISvgNodeRenderer expected = new DummySvgNodeRenderer();

        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> this.context.addNamedObject("", expected)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.NAMED_OBJECT_NAME_NULL_OR_EMPTY, e.getMessage());
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
