package com.itextpdf.model.element;

import com.itextpdf.model.renderer.ListItemRenderer;

public class ListItem extends Div {

    public ListItem() {
        super();
    }

    public ListItem(String text) {
        this();
        add(new Paragraph(text).setMarginTop(0).setMarginBottom(0));
    }

    public ListItem(Image image) {
        this();
        add(image);
    }

    @Override
    protected ListItemRenderer makeNewRenderer() {
        return new ListItemRenderer(this);
    }

}
