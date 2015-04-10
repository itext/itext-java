package com.itextpdf.model.element;

public class Paragraph extends BlockElement<Paragraph> {

    public Paragraph() {
    }

    public Paragraph(String text) {
        add(new Text(text));
    }

    public Paragraph add(String text) {
        return add(new Span(text));
    }


}
