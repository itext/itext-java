package com.itextpdf.model.element;

public class Div extends BlockElement<Div> {

    public Div add(BlockElement element) {
        childElements.add(element);
        return this;
    }

    public Div add(Image element) {
        childElements.add(element);
        return this;
    }
}
