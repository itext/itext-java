package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;

@Category(UnitTest.class)
public class LineRendererUnitTest extends AbstractRendererUnitTest {

    private static final double EPS = 1e-5;

    @Test
    public void adjustChildPositionsAfterReorderingSimpleTest01() {
        Document dummyDocument = createDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        IRenderer dummyImage = createLayoutedImageRenderer(100, 100, dummyDocument);
        Assert.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummyImage, dummy2), 10);
        Assert.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(37.3359985, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(137.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 4)})
    public void adjustChildPositionsAfterReorderingTestWithPercentMargins01() {
        Document dummyDocument = createDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        dummy1.setProperty(Property.MARGIN_LEFT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.MARGIN_RIGHT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.PADDING_LEFT, UnitValue.createPercentValue(10));
        dummy1.setProperty(Property.PADDING_RIGHT, UnitValue.createPercentValue(10));
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        Assert.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummy2), 10);
        Assert.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        // If margins and paddings are specified in percents, we treat them as point values for now
        Assert.assertEquals(77.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    public void adjustChildPositionsAfterReorderingTestWithFloats01() {
        Document dummyDocument = createDocument();
        IRenderer dummy1 = createLayoutedTextRenderer("Hello", dummyDocument);
        IRenderer dummy2 = createLayoutedTextRenderer("world", dummyDocument);
        IRenderer dummyImage = createLayoutedImageRenderer(100, 100, dummyDocument);
        dummyImage.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        Assert.assertEquals(0, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummy2.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        LineRenderer.adjustChildPositionsAfterReordering(Arrays.asList(dummy1, dummyImage, dummy2), 10);
        Assert.assertEquals(10, dummy1.getOccupiedArea().getBBox().getX(), EPS);
        // Floating renderer is not repositioned
        Assert.assertEquals(0, dummyImage.getOccupiedArea().getBBox().getX(), EPS);
        Assert.assertEquals(37.3359985, dummy2.getOccupiedArea().getBBox().getX(), EPS);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.INLINE_BLOCK_ELEMENT_WILL_BE_CLIPPED)})
    public void inlineBlockWithBigMinWidth01() {
        Document dummyDocument = createDocument();
        LineRenderer lineRenderer = (LineRenderer) new LineRenderer().setParent(dummyDocument.getRenderer());
        Div div = new Div().setMinWidth(2000).setHeight(100);
        DivRenderer inlineBlockRenderer = (DivRenderer) div.createRendererSubTree();
        lineRenderer.addChild(inlineBlockRenderer);
        LayoutResult result = lineRenderer.layout(new LayoutContext(createLayoutArea(1000, 1000)));
        // In case there is an inline block child with large min-width, the inline block child will be force placed (not layouted properly)
        Assert.assertEquals(LayoutResult.FULL, result.getStatus());
        Assert.assertEquals(0, result.getOccupiedArea().getBBox().getHeight(), EPS);
        Assert.assertEquals(true, inlineBlockRenderer.getPropertyAsBoolean(Property.FORCED_PLACEMENT));
    }

}
