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
package com.itextpdf.layout.margincollapse;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class MarginsCollapseHandlerTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 2)
    })
    // This test's aim is to test message logging.
    public void testDefiningMarginCollapse() {
        ParagraphRenderer paragraphRenderer = new ParagraphRenderer(new Paragraph());
        Rectangle rectangle = new Rectangle(0f, 0f);
        paragraphRenderer.getModelElement().setProperty(Property.MARGIN_TOP, UnitValue.createPercentValue(0f));
        paragraphRenderer.getModelElement().setProperty(Property.MARGIN_BOTTOM, UnitValue.createPercentValue(0f));

        MarginsCollapseHandler marginsCollapseHandler = new MarginsCollapseHandler(paragraphRenderer, null);
        AssertUtil.doesNotThrow(() -> marginsCollapseHandler.startMarginsCollapse(rectangle));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 2)
    })
    // This test's aim is to test message logging.
    public void testHasPadding() {
        ParagraphRenderer paragraphRenderer = new ParagraphRenderer(new Paragraph());
        Rectangle rectangle = new Rectangle(0f, 0f);
        paragraphRenderer.getModelElement().setProperty(Property.PADDING_TOP, UnitValue.createPercentValue(0f));
        paragraphRenderer.getModelElement().setProperty(Property.PADDING_BOTTOM, UnitValue.createPercentValue(0f));

        MarginsCollapseHandler marginsCollapseHandler = new MarginsCollapseHandler(paragraphRenderer, null);
        AssertUtil.doesNotThrow(() -> marginsCollapseHandler.startMarginsCollapse(rectangle));
    }
}
