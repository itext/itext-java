/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.ListSymbolAlignment;
import com.itextpdf.layout.property.ListSymbolPosition;
import com.itextpdf.layout.property.Property;
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
