package com.itextpdf.model.element;

import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.ListRenderer;

public class List extends BlockElement<List> {

    public static final String DEFAULT_LIST_SYMBOL = "- ";

    protected String preSymbolText = "";
    protected String postSymbolText = ". ";

    public List() {
        super();
        setListSymbol(DEFAULT_LIST_SYMBOL);
    }

    public List(Property.ListNumberingType listNumberingType) {
        super();
        setListSymbol(listNumberingType);
    }

    public List add(ListItem listItem) {
        childElements.add(listItem);
        return this;
    }

    public List add(String text) {
        return add(new ListItem(text));
    }

    public List setListSymbol(String symbol) {
        return setListSymbol(new Text(symbol));
    }

    public List setListSymbol(Text text) {
        return setProperty(Property.LIST_SYMBOL, text);
    }

    public List setListSymbol(Image image) {
        return setProperty(Property.LIST_SYMBOL, image);
    }

    public List setListSymbol(Property.ListNumberingType listNumberingType) {
        return setProperty(Property.LIST_SYMBOL, listNumberingType);
    }

    public Float getSymbolIndent() {
        return getProperty(Property.LIST_SYMBOL_INDENT);
    }

    public List setSymbolIndent(float symbolIndent) {
        return setProperty(Property.LIST_SYMBOL_INDENT, symbolIndent);
    }

    public String getPostSymbolText() {
        return postSymbolText;
    }

    public void setPostSymbolText(String postSymbolText) {
        this.postSymbolText = postSymbolText;
    }

    public String getPreSymbolText() {
        return preSymbolText;
    }

    public void setPreSymbolText(String preSymbolText) {
        this.preSymbolText = preSymbolText;
    }

    @Override
    protected ListRenderer makeNewRenderer() {
        return new ListRenderer(this);
    }
}
