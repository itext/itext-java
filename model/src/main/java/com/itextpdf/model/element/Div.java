package com.itextpdf.model.element;

public class Div extends BlockElement<Div> {

    public <T extends Div> T add(BlockElement element) {
        childElements.add(element);
        return (T) this;
    }

    public <T extends Div> T add(Image element) {
        childElements.add(element);
        return (T) this;
    }
}
