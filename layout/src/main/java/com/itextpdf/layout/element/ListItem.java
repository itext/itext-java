package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.layout.renderer.ListItemRenderer;

/**
 * A list item is a layout element that is one entry in a {@link List}. The list
 * object controls the prefix, postfix, and numbering of the list items.
 */
public class ListItem extends Div {

    /**
     * Creates a ListItem.
     */
    public ListItem() {
        super();
        role = PdfName.LBody;
    }

    /**
     * Creates a list item with text.
     * 
     * @param text the textual contents of the list item
     */
    public ListItem(String text) {
        this();
        add(new Paragraph(text).setMarginTop(0).setMarginBottom(0));
    }

    /**
     * Creates a list item with an image.
     * 
     * @param image the graphical contents of the list item
     */
    public ListItem(Image image) {
        this();
        add(image);
    }

    @Override
    protected ListItemRenderer makeNewRenderer() {
        return new ListItemRenderer(this);
    }

}
