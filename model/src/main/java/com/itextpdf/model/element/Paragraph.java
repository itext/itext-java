package com.itextpdf.model.element;

import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.ParagraphRenderer;

public class Paragraph extends BlockElement<Paragraph> {

    public Paragraph() {
    }

    public Paragraph(String text) {
        this(new Text(text));
    }

    public Paragraph(Text text) {
        add(text);
    }

    public Paragraph add(String text) {
        return add(new Text(text));
    }

    public Paragraph add(ILeafElement element) {
        childElements.add(element);
        return this;
    }

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new ParagraphRenderer(this);
    }

    @Override
    public <T> T getDefaultProperty(Integer propertyKey) {
        switch (propertyKey) {
            case Property.LEADING:
                return (T) new Property.Leading(Property.Leading.MULTIPLIED, 1.5f);
            default:
                return null;
        }
    }

    public Paragraph setFirstLineIndent(float indent) {
        setProperty(Property.FIRST_LINE_INDENT, indent);
        return this;
    }

    public Paragraph setFixedLeading(float leading) {
        setProperty(Property.LEADING, new Property.Leading(Property.Leading.FIXED, leading));
        return this;
    }

    public Paragraph setMultipliedLeading(float leading) {
        setProperty(Property.LEADING, new Property.Leading(Property.Leading.MULTIPLIED, leading));
        return this;
    }

}
