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
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.ListSymbolPosition;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
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
     * Customizes the index of the item in the list.
     *
     * @param ordinalValue the custom value property of an ordered list's list item.
     * @return this listItem.
     */
    public ListItem setListSymbolOrdinalValue(int ordinalValue) {
        setProperty(Property.LIST_SYMBOL_ORDINAL_VALUE, ordinalValue);
        return this;
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
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.LIST_SYMBOL_POSITION:
                return (T1) (Object) ListSymbolPosition.DEFAULT;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * Sets the list item symbol to be used.
     *
     * @param symbol the textual symbol to be used for the item.
     * @return this list item.
     */
    public ListItem setListSymbol(String symbol) {
        return setListSymbol(new Text(symbol));
    }

    /**
     * Sets the list item symbol to be used.
     *
     * @param text the {@link Text} object to be used for the item.
     * @return this list item.
     */
    public ListItem setListSymbol(Text text) {
        setProperty(Property.LIST_SYMBOL, text);
        return this;
    }

    /**
     * Sets the list item symbol to be used.
     *
     * @param image the {@link Image} object to be used for the item.
     * @return this list.
     */
    public ListItem setListSymbol(Image image) {
        setProperty(Property.LIST_SYMBOL, image);
        return this;
    }

    /**
     * Sets the list item numbering type to be used.
     *
     * @param listNumberingType the {@link ListNumberingType} that will generate appropriate prefixes for the {@link ListItem}.
     * @return this list item.
     */
    public ListItem setListSymbol(ListNumberingType listNumberingType) {
        // Do not draw any points after ZapfDingbats special number symbol
        if (listNumberingType == ListNumberingType.ZAPF_DINGBATS_1 || listNumberingType == ListNumberingType.ZAPF_DINGBATS_2 ||
                listNumberingType == ListNumberingType.ZAPF_DINGBATS_3 || listNumberingType == ListNumberingType.ZAPF_DINGBATS_4) {
            setProperty(Property.LIST_SYMBOL_POST_TEXT, " ");
        }
        setProperty(Property.LIST_SYMBOL, listNumberingType);
        return this;
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.LBODY);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new ListItemRenderer(this);
    }
}
