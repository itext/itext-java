package com.itextpdf.model.element;

public class Paragraph extends BlockElement {

    public Paragraph(String text) {
        add(new Text(text));
    }

    @Override
    public Paragraph add(BlockElement element) {
        super.add(element);
        return this;
    }

    @Override
    public Paragraph add(InlineElement element) {
        super.add(element);
        return this;
    }

    public Paragraph add(String text) {
        return add(new Span(text));
    }


}
