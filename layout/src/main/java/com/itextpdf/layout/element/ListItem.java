package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.layout.Property;
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

    /**
     * Sets the list item symbol to be used.
     * @param symbol the textual symbol to be used for the item.
     * @return this list item.
     */
    public ListItem setListSymbol(String symbol) {
        return setListSymbol(new Text(symbol));
    }

    /**
     * Sets the list item symbol to be used.
     * @param text the {@link Text} object to be used for the item.
     * @return this list item.
     */
    public ListItem setListSymbol(Text text) {
        return setProperty(Property.LIST_SYMBOL, text);
    }

    /**
     * Sets the list item symbol to be used.
     * @param image the {@link Image} object to be used for the item.
     * @return this list.
     */
    public ListItem setListSymbol(Image image) {
        return setProperty(Property.LIST_SYMBOL, image);
    }

    /**
     * Sets the list item numbering type to be used.
     * @param listNumberingType the {@link Property#ListNumberingType} that will generate appropriate prefixes for the {@link ListItem}.
     * @return this list item.
     */
    public ListItem setListSymbol(Property.ListNumberingType listNumberingType) {
        // Do not draw any points after ZapfDingbats special number symbol
        if (listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_1 || listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_2 ||
                listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_3 || listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_4) {
            setProperty(Property.LIST_SYMBOL_POST_TEXT, " ");
        }
        return setProperty(Property.LIST_SYMBOL, listNumberingType);
    }

    @Override
    protected ListItemRenderer makeNewRenderer() {
        return new ListItemRenderer(this);
    }

}
