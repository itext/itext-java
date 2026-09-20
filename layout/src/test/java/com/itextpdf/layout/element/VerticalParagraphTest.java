package com.itextpdf.layout.element;

import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
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
            logLevel = LogLevelConstants.WARN, count = 2)})
    public void settingUnsupportedPropertiesMustLogWarning() {
        VerticalParagraph verticalParagraph = new VerticalParagraph(false);
        Leading original = verticalParagraph.<Leading>getProperty(Property.LEADING);
        verticalParagraph.setProperty(com.itextpdf.layout.properties.Property.FLOAT,
                com.itextpdf.layout.properties.FloatPropertyValue.LEFT);
        verticalParagraph.setProperty(com.itextpdf.layout.properties.Property.LEADING,
                new Leading(Leading.MULTIPLIED, 3f));
        // Keep sonar happy; the real test is through the log messages, so we just assert true here.
        Assertions.assertEquals(original, verticalParagraph.<Leading>getProperty(Property.LEADING));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY,
            logLevel = LogLevelConstants.WARN),})
    public void unsupportedInheritedPropertiesMustLogWarning() {
        VerticalParagraph verticalParagraph = new VerticalParagraph(false);
        TestRenderer parentRenderer = new TestRenderer();
        parentRenderer.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 3f));
        parentRenderer.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        IRenderer verticalParagraphRenderer = verticalParagraph.getRenderer();
        verticalParagraphRenderer.setParent(parentRenderer);

        Assertions.assertNull(verticalParagraphRenderer.<Leading>getProperty(Property.LEADING));
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