package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.impl.NoDrawOperationSvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.util.EmptyStackException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SvgDrawContextTest {

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
        context = new SvgDrawContext();
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
    public void addPdfFormXObject() {
        String name = "expected";
        PdfFormXObject expected = new PdfFormXObject(new Rectangle(0,0,0,0));
        this.context.addNamedObject(name, expected);
        Object actual = this.context.getNamedObject(name);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void addISvgNodeRender() {
        String name = "expected";
        ISvgNodeRenderer expected = new NoDrawOperationSvgNodeRenderer();
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

        ISvgNodeRenderer expected = new NoDrawOperationSvgNodeRenderer();
        this.context.addNamedObject(null, expected);
    }

    @Test
    public void addNamedObjectWithEmptyName() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.NAMED_OBJECT_NAME_NULL_OR_EMPTY);

        ISvgNodeRenderer expected = new NoDrawOperationSvgNodeRenderer();
        this.context.addNamedObject("", expected);
    }
}
