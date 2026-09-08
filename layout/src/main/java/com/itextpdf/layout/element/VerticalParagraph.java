package com.itextpdf.layout.element;

import com.itextpdf.commons.logs.LazyLogger;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Paragraph} that is laid out vertically, with text flowing from top to bottom.
 */
public class VerticalParagraph extends AbstractParagraph<VerticalParagraph> {

    private static final LazyLogger LOGGER = new LazyLogger(VerticalParagraph.class);

    private static final Map<Integer, String> unsupportedProperties = new HashMap<Integer, String>();
    static {
        unsupportedProperties.put(Property.FLOAT, "Float");
        unsupportedProperties.put(Property.LEADING, "Leading");
    }

    /**
     * Creates a new {@link VerticalParagraph} instance.
     */
    public VerticalParagraph() {
        super();
        super.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        super.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        super.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
    }

    /**
     * Creates a {@link VerticalParagraph}, initialized with a piece of text.
     *
     * @param text the initial textual content, as a {@link String}
     */
    public VerticalParagraph(String text) {
        this(new Text(text));
    }

    /**
     * Creates a {@link VerticalParagraph}, initialized with a piece of text.
     *
     * @param text the initial textual content, as a {@link Text}
     */
    public VerticalParagraph(Text text) {
        this();
        super.add(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(int property, Object value) {
        if (unsupportedProperties.containsKey(property)) {
            LOGGER.warn(() ->
                    MessageFormatUtil.format(
                            LayoutLogMessageConstant.UNSUPPORTED_PROPERTY, getClass().getSimpleName(),
                            unsupportedProperties.get(property)));
            return;
        }
        super.setProperty(property, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, String> getUnsupportedProperties() {
        return unsupportedProperties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new ParagraphRenderer(this);
    }
}
