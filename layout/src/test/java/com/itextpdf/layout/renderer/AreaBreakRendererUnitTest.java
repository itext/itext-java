package com.itextpdf.layout.renderer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class AreaBreakRendererUnitTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void addChildTestUnsupported() {
        junitExpectedException.expect(RuntimeException.class);

        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertNull(areaBreakRenderer.getChildRenderers());

        areaBreakRenderer.addChild(new TextRenderer(new Text("Test")));
    }

    @Test
    public void drawTestUnsupported() {
        junitExpectedException.expect(UnsupportedOperationException.class);

        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        areaBreakRenderer.draw(new DrawContext(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())), null));
    }

    @Test
    public void getOccupiedAreaTestUnsupported() {
        junitExpectedException.expect(UnsupportedOperationException.class);

        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        areaBreakRenderer.getOccupiedArea();
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is false for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void hasPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertFalse(areaBreakRenderer.hasProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is false for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void hasOwnPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertFalse(areaBreakRenderer.hasProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is null for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void getPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertNull(areaBreakRenderer.<Property>getProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is null for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void getOwnPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertNull(areaBreakRenderer.<Property>getOwnProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is null for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void getDefaultPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertNull(areaBreakRenderer.<Property>getDefaultProperty(Property.BORDER));
    }

    @Test
    //The BORDER_RADIUS property is chosen without any specific intention. It could be replaced with any other property.
    public void getPropertyWithDefaultValueTestUnsupported() {
        junitExpectedException.expect(UnsupportedOperationException.class);

        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        areaBreakRenderer.getProperty(Property.BORDER_RADIUS, 3);
    }

    @Test
    //The BORDER_RADIUS property is chosen without any specific intention. It could be replaced with any other property.
    public void setPropertyTestUnsupported() {
        junitExpectedException.expect(UnsupportedOperationException.class);

        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        areaBreakRenderer.setProperty(Property.BORDER_RADIUS, 5);
    }

    @Test
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    //Here we just check that no exception has been thrown.
    public void deleteOwnProperty() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        areaBreakRenderer.deleteOwnProperty(Property.BORDER);
        Assert.assertTrue(true);
    }

    @Test
    public void getModelElementTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertNull(areaBreakRenderer.getModelElement());
    }

    @Test
    public void getParentTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertNull(areaBreakRenderer.getParent());
    }

    @Test
    public void setParentTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertEquals(areaBreakRenderer, areaBreakRenderer.setParent(new AreaBreakRenderer(new AreaBreak())));
    }

    @Test
    public void isFlushedTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertFalse(areaBreakRenderer.isFlushed());
    }

    @Test
    public void moveTestUnsupported() {
        junitExpectedException.expect(UnsupportedOperationException.class);

        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        areaBreakRenderer.move(2.0f, 2.0f);
    }

    @Test
    public void getNextRendererTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assert.assertNull(areaBreakRenderer.getNextRenderer());
    }

    @Test
    public void layoutTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        LayoutResult layoutResult = areaBreakRenderer.layout(new LayoutContext(null));
        Assert.assertEquals(LayoutResult.NOTHING, layoutResult.getStatus());
        Assert.assertNull(layoutResult.getOccupiedArea());
        Assert.assertNull(layoutResult.getSplitRenderer());
        Assert.assertNull(layoutResult.getOverflowRenderer());
        Assert.assertEquals(areaBreakRenderer, layoutResult.getCauseOfNothing());
        Assert.assertEquals(areaBreakRenderer.areaBreak, layoutResult.getAreaBreak());
    }

}
