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
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.ListSymbolPosition;
import com.itextpdf.layout.property.Property;
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
