/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.ListSymbolAlignment;
import com.itextpdf.layout.properties.ListSymbolPosition;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ListRenderer;

/**
 * A List is a layout element representing a series of objects that are vertically
 * outlined with the same or very similar layout properties, giving it a sense
 * of unity. It contains {@link ListItem} objects that can optionally be prefixed
 * with a symbol and/or numbered.
 */
public class List extends BlockElement<List> {

    public static final String DEFAULT_LIST_SYMBOL = "- ";

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates a List with the {@link #DEFAULT_LIST_SYMBOL} as a prefix.
     */
    public List() {
        super();
    }

    /**
     * Creates a List with a custom numbering type.
     *
     * @param listNumberingType a prefix style
     */
    public List(ListNumberingType listNumberingType) {
        super();
        setListSymbol(listNumberingType);
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.LIST_SYMBOL:
                return (T1) (Object) new Text(DEFAULT_LIST_SYMBOL);
            case Property.LIST_SYMBOL_PRE_TEXT:
                return (T1) (Object) "";
            case Property.LIST_SYMBOL_POST_TEXT:
                return (T1) (Object) ". ";
            case Property.LIST_SYMBOL_POSITION:
                return (T1) (Object) ListSymbolPosition.DEFAULT;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * Adds a new {@link ListItem} to the bottom of the List.
     *
     * @param listItem a new list item
     * @return this list.
     */
    public List add(ListItem listItem) {
        childElements.add(listItem);
        return this;
    }

    /**
     * Adds a new {@link ListItem} to the bottom of the List.
     *
     * @param text textual contents of the new list item
     * @return this list.
     */
    public List add(String text) {
        return add(new ListItem(text));
    }

    /**
     * Customizes the index of the first item in the list.
     *
     * @param start the custom index, as an <code>int</code>
     * @return this list.
     */
    public List setItemStartIndex(int start) {
        setProperty(Property.LIST_START, start);
        return this;
    }

    /**
     * Sets the list symbol to be used. This will create an unordered list, i.e.
     * all {@link ListItem list items} will be shown with the same prefix.
     *
     * @param symbol the textual symbol to be used for all items.
     * @return this list.
     */
    public List setListSymbol(String symbol) {
        return setListSymbol(new Text(symbol));
    }

    /**
     * Sets the list symbol to be used. This will create an unordered list, i.e.
     * all {@link ListItem list items} will be shown with the same prefix.
     *
     * @param text the {@link Text} object to be used for all items.
     * @return this list.
     */
    public List setListSymbol(Text text) {
        setProperty(Property.LIST_SYMBOL, text);
        return this;
    }

    /**
     * Sets the list symbol to be used. This will create an unordered list, i.e.
     * all {@link ListItem list items} will be shown with the same prefix.
     *
     * @param image the {@link Image} object to be used for all items.
     * @return this list.
     */
    public List setListSymbol(Image image) {
        setProperty(Property.LIST_SYMBOL, image);
        return this;
    }

    /**
     * Sets the list numbering type to be used. This will create an ordered list,
     * i.e. every {@link ListItem} will have a unique prefix.
     *
     * @param listNumberingType the {@link ListNumberingType} that will generate appropriate prefixes for the {@link ListItem}s.
     * @return this list.
     */
    public List setListSymbol(ListNumberingType listNumberingType) {
        // Do not draw any points after ZapfDingbats special number symbol
        if (listNumberingType == ListNumberingType.ZAPF_DINGBATS_1 || listNumberingType == ListNumberingType.ZAPF_DINGBATS_2 ||
                listNumberingType == ListNumberingType.ZAPF_DINGBATS_3 || listNumberingType == ListNumberingType.ZAPF_DINGBATS_4) {
            setPostSymbolText(" ");
        }
        setProperty(Property.LIST_SYMBOL, listNumberingType);
        return this;
    }

    /**
     * A specialized enum containing alignment properties for list symbols.
     * {@link ListSymbolAlignment#LEFT} means that the items will be aligned as follows:
     * 9.  Item 9
     * 10. Item 10
     * <p>
     * Whereas {@link ListSymbolAlignment#RIGHT} means the items will be aligned as follows:
     * 9. Item 9
     * 10. Item 10
     *
     * @param alignment the alignment of the list symbols
     * @return this element
     */
    public List setListSymbolAlignment(ListSymbolAlignment alignment) {
        setProperty(Property.LIST_SYMBOL_ALIGNMENT, alignment);
        return this;
    }

    /**
     * Gets the indent offset of the {@link ListItem} symbols.
     *
     * @return the indent offset as a <code>float</code>.
     */
    public Float getSymbolIndent() {
        return this.<Float>getProperty(Property.LIST_SYMBOL_INDENT);
    }

    /**
     * Sets the indent offset of the {@link ListItem} symbols.
     *
     * @param symbolIndent the new indent offset.
     * @return this list.
     */
    public List setSymbolIndent(float symbolIndent) {
        setProperty(Property.LIST_SYMBOL_INDENT, symbolIndent);
        return this;
    }

    /**
     * Gets the piece of text that is added after the {@link ListItem} symbol.
     *
     * @return the post symbol text
     */
    public String getPostSymbolText() {
        return this.<String>getProperty(Property.LIST_SYMBOL_POST_TEXT);
    }

    /**
     * Sets a piece of text that should be added after the {@link ListItem} symbol.
     *
     * @param postSymbolText the post symbol text
     */
    public void setPostSymbolText(String postSymbolText) {
        setProperty(Property.LIST_SYMBOL_POST_TEXT, postSymbolText);
    }

    /**
     * Gets the piece of text that is added before the {@link ListItem} symbol.
     *
     * @return the pre symbol text
     */
    public String getPreSymbolText() {
        return this.<String>getProperty(Property.LIST_SYMBOL_PRE_TEXT);
    }

    /**
     * Sets a piece of text that should be added before the {@link ListItem} symbol.
     *
     * @param preSymbolText the pre symbol text
     */
    public void setPreSymbolText(String preSymbolText) {
        setProperty(Property.LIST_SYMBOL_PRE_TEXT, preSymbolText);
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.L);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new ListRenderer(this);
    }
}
