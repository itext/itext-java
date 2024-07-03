/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class AreaBreakRendererUnitTest extends ExtendedITextTest {

    @Test
    public void addChildTestUnsupported() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());

        Assertions.assertNull(areaBreakRenderer.getChildRenderers());
        Assertions.assertThrows(Exception.class, () -> areaBreakRenderer.addChild(new TextRenderer(new Text("Test"))));
    }

    @Test
    public void drawTestUnsupported() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());

        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> areaBreakRenderer.draw(new DrawContext(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())), null))
        );
    }

    @Test
    public void getOccupiedAreaTestUnsupported() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());

        Assertions.assertThrows(UnsupportedOperationException.class, () -> areaBreakRenderer.getOccupiedArea());
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is false for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void hasPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertFalse(areaBreakRenderer.hasProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is false for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void hasOwnPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertFalse(areaBreakRenderer.hasProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is null for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void getPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertNull(areaBreakRenderer.<Property>getProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is null for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void getOwnPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertNull(areaBreakRenderer.<Property>getOwnProperty(Property.BORDER));
    }

    @Test
    //Properties are not supported for AbstractRenderer, and it's expected that the result is null for all the properties.
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    public void getDefaultPropertyTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertNull(areaBreakRenderer.<Property>getDefaultProperty(Property.BORDER));
    }

    @Test
    //The BORDER_RADIUS property is chosen without any specific intention. It could be replaced with any other property.
    public void getPropertyWithDefaultValueTestUnsupported() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());

        Assertions.assertThrows(UnsupportedOperationException.class, () -> areaBreakRenderer.getProperty(Property.BORDER_RADIUS, 3));
    }

    @Test
    //The BORDER_RADIUS property is chosen without any specific intention. It could be replaced with any other property.
    public void setPropertyTestUnsupported() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());

        Assertions.assertThrows(UnsupportedOperationException.class, () -> areaBreakRenderer.setProperty(Property.BORDER_RADIUS, 5));
    }

    @Test
    //The BORDER property is chosen without any specific intention. It could be replaced with any other property.
    //Here we just check that no exception has been thrown.
    public void deleteOwnProperty() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        AssertUtil.doesNotThrow(() -> areaBreakRenderer.deleteOwnProperty(Property.BORDER));
    }

    @Test
    public void getModelElementTest() {
        AreaBreak areaBreak = new AreaBreak();
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(areaBreak);
        Assertions.assertNull(areaBreakRenderer.getModelElement());
    }

    @Test
    public void getParentTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertNull(areaBreakRenderer.getParent());
    }

    @Test
    public void setParentTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertEquals(areaBreakRenderer, areaBreakRenderer.setParent(new AreaBreakRenderer(new AreaBreak())));
    }

    @Test
    public void isFlushedTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertFalse(areaBreakRenderer.isFlushed());
    }

    @Test
    public void moveTestUnsupported() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());

        Assertions.assertThrows(UnsupportedOperationException.class, () -> areaBreakRenderer.move(2.0f, 2.0f));
    }

    @Test
    public void getNextRendererTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        Assertions.assertNull(areaBreakRenderer.getNextRenderer());
    }

    @Test
    public void layoutTest() {
        AreaBreakRenderer areaBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        LayoutResult layoutResult = areaBreakRenderer.layout(new LayoutContext(null));
        Assertions.assertEquals(LayoutResult.NOTHING, layoutResult.getStatus());
        Assertions.assertNull(layoutResult.getOccupiedArea());
        Assertions.assertNull(layoutResult.getSplitRenderer());
        Assertions.assertNull(layoutResult.getOverflowRenderer());
        Assertions.assertEquals(areaBreakRenderer, layoutResult.getCauseOfNothing());
        Assertions.assertEquals(areaBreakRenderer.areaBreak, layoutResult.getAreaBreak());
    }

}
