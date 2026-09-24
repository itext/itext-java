/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.element;

import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAnchor;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class VerticalParagraphTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY,
            logLevel = LogLevelConstants.WARN)})
    public void settingUnsupportedPropertiesMustLogWarning() {
        VerticalParagraph verticalParagraph = new VerticalParagraph(false);
        Leading leading = new Leading(Leading.MULTIPLIED, 3f);
        verticalParagraph.setProperty(com.itextpdf.layout.properties.Property.FLOAT,
                com.itextpdf.layout.properties.FloatPropertyValue.LEFT);
        verticalParagraph.setProperty(com.itextpdf.layout.properties.Property.LEADING,
                leading);

        verticalParagraph.getRenderer().setParent(new TestRenderer());
        // Keep sonar happy; the real test is through the log messages, so we just assert true here.
        Assertions.assertEquals(leading, verticalParagraph.<Leading>getProperty(Property.LEADING));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY,
            logLevel = LogLevelConstants.WARN),})
    public void unsupportedInheritedPropertiesMustLogWarning() {
        VerticalParagraph verticalParagraph = new VerticalParagraph(false);
        TestRenderer parentRenderer = new TestRenderer();
        parentRenderer.setProperty(Property.TEXT_ANCHOR, TextAnchor.END);
        IRenderer verticalParagraphRenderer = verticalParagraph.getRenderer();
        verticalParagraphRenderer.setParent(parentRenderer);

        // Actual test is done through the log messages, so we just assert here to keep sonar happy.
        Assertions.assertNull(verticalParagraph.<TextAnchor>getProperty(Property.TEXT_ANCHOR));
    }

    private static class TestRenderer extends AbstractRenderer {

        public TestRenderer() {
            super();
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            return null;
        }

        @Override
        public IRenderer getNextRenderer() {
            return null;
        }
    }
}
