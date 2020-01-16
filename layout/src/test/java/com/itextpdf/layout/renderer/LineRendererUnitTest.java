/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
