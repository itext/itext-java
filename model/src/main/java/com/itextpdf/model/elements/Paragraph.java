package com.itextpdf.model.elements;

public class Paragraph extends PositioningElement implements IBlockElement {

    public Paragraph(String text) {

    }

    public Paragraph add(Span span) {
        return this;
    }

    public Paragraph add(String text) {
        return add(new Span(text));
    }

}
