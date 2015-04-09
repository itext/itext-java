package com.itextpdf.model.element;

public class Span extends InlineElement {

    public Span(String text) {
        add(new Text(text));
    }
}
