package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.AccessibleElementProperties;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.renderer.ListRenderer;

/**
 * A List is a layout element representing a series of objects that are vertically
 * outlined with the same or very similar layout properties, giving it a sense
 * of unity. It contains {@link ListItem} objects that can optionally be prefixed
 * with a symbol and/or numbered.
 */
public class List extends BlockElement<List> {

    public static final String DEFAULT_LIST_SYMBOL = "- ";

    protected String preSymbolText = "";
    protected String postSymbolText = ". ";

    protected PdfName role = PdfName.L;
    protected AccessibleElementProperties tagProperties;

    /**
     * Creates a List with the {@link #DEFAULT_LIST_SYMBOL} as a prefix.
     */
    public List() {
        super();
        setListSymbol(DEFAULT_LIST_SYMBOL);
    }

    /**
     * Creates a List with a custom numbering type.
     * @param listNumberingType a prefix style
     */
    public List(Property.ListNumberingType listNumberingType) {
        super();
        setListSymbol(listNumberingType);
    }

    /**
     * Adds a new {@link ListItem} to the bottom of the List.
     * @param listItem a new list item
     * @return this list.
     */
    public List add(ListItem listItem) {
        childElements.add(listItem);
        return this;
    }

    /**
     * Adds a new {@link ListItem} to the bottom of the List.
     * @param text textual contents of the new list item
     * @return this list.
     */
    public List add(String text) {
        return add(new ListItem(text));
    }

    /**
     * Customizes the index of the first item in the list.
     * @param start the custom index, as an <code>int</code>
     * @return this list.
     */
    public List setItemStartIndex(int start) {
        return setProperty(Property.LIST_START, start);
    }

    /**
     * Sets the list symbol to be used. This will create an unordered list, i.e.
     * all {@link ListItem list items} will be shown with the same prefix.
     * @param symbol the textual symbol to be used for all items.
     * @return this list.
     */
    public List setListSymbol(String symbol) {
        return setListSymbol(new Text(symbol));
    }

    /**
     * Sets the list symbol to be used. This will create an unordered list, i.e.
     * all {@link ListItem list items} will be shown with the same prefix.
     * @param text the {@link Text} object to be used for all items.
     * @return this list.
     */
    public List setListSymbol(Text text) {
        return setProperty(Property.LIST_SYMBOL, text);
    }

    /**
     * Sets the list symbol to be used. This will create an unordered list, i.e.
     * all {@link ListItem list items} will be shown with the same prefix.
     * @param image the {@link Image} object to be used for all items.
     * @return this list.
     */
    public List setListSymbol(Image image) {
        return setProperty(Property.LIST_SYMBOL, image);
    }

    /**
     * Sets the list numbering type to be used. This will create an ordered list,
     * i.e. every {@link ListItem} will have a unique prefix.
     * @param listNumberingType the {@link Property#ListNumberingType} that will generate appropriate prefixes for the {@link ListItem}s.
     * @return this list.
     */
    public List setListSymbol(Property.ListNumberingType listNumberingType) {
        // Do not draw any points after ZapfDingbats special number symbol
        if (listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_1 || listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_2 ||
                listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_3 || listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_4) {
            postSymbolText = " ";
        }
        return setProperty(Property.LIST_SYMBOL, listNumberingType);
    }

    /**
     * Gets the indent offset of the {@link ListItem} symbols.
     * 
     * @return the indent offset as a <code>float</code>.
     */
    public Float getSymbolIndent() {
        return getProperty(Property.LIST_SYMBOL_INDENT);
    }

    /**
     * Sets the indent offset of the {@link ListItem} symbols.
     * 
     * @param symbolIndent the new indent offset.
     * @return this list.
     */
    public List setSymbolIndent(float symbolIndent) {
        return setProperty(Property.LIST_SYMBOL_INDENT, symbolIndent);
    }

    /**
     * Gets the piece of text that is added after the {@link ListItem} symbol.
     * @return the post symbol text
     */
    public String getPostSymbolText() {
        return postSymbolText;
    }

    /**
     * Sets a piece of text that should be added after the {@link ListItem} symbol.
     * @param postSymbolText the post symbol text
     */
    public void setPostSymbolText(String postSymbolText) {
        this.postSymbolText = postSymbolText;
    }

    /**
     * Gets the piece of text that is added before the {@link ListItem} symbol.
     * @return the pre symbol text
     */
    public String getPreSymbolText() {
        return preSymbolText;
    }

    /**
     * Sets a piece of text that should be added before the {@link ListItem} symbol.
     * @param preSymbolText the pre symbol text
     */
    public void setPreSymbolText(String preSymbolText) {
        this.preSymbolText = preSymbolText;
    }

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
        if (PdfName.Artifact.equals(role)) {
            propagateArtifactRoleToChildElements();
        }
    }

    @Override
    public AccessibleElementProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibleElementProperties();
        }
        return tagProperties;
    }

    @Override
    protected ListRenderer makeNewRenderer() {
        return new ListRenderer(this);
    }
}
