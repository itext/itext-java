/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.styledxmlparser.node.impl.jsoup.node;

import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link IElementNode} interface; wrapper for the JSoup {@link JsoupNode} class.
 */
public class JsoupElementNode extends JsoupNode implements IElementNode {

    /** The JSoup element. */
    private final Element element;
    
    /** The attributes. */
    private final IAttributes attributes;
    
    /** The resolved styles. */
    private Map<String, String> elementResolvedStyles;
    
    /** The custom default styles. */
    private List<Map<String, String>> customDefaultStyles;
    
    /** The language. */
    private String lang = null;

    /**
     * Creates a new {@link JsoupElementNode} instance.
     *
     * @param element the element
     */
    public JsoupElementNode(Element element) {
        super(element);
        this.element = element;
        this.attributes = new JsoupAttributes(element.attributes());
        this.lang = getAttribute(CommonAttributeConstants.LANG);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#name()
     */
    @Override
    public String name() {
        return element.nodeName();
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getAttributes()
     */
    public IAttributes getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String key) {
        return attributes.getAttribute(key);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IStylesContainer#setStyles(java.util.Map)
     */
    @Override
    public void setStyles(Map<String, String> elementResolvedStyles) {
        this.elementResolvedStyles = elementResolvedStyles;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IStylesContainer#getStyles()
     */
    @Override
    public Map<String, String> getStyles() {
        return this.elementResolvedStyles;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getAdditionalHtmlStyles()
     */
    @Override
    public List<Map<String, String>> getAdditionalHtmlStyles() {
        return customDefaultStyles;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#addAdditionalHtmlStyles(java.util.Map)
     */
    @Override
    public void addAdditionalHtmlStyles(Map<String, String> styles) {
        if (customDefaultStyles == null) {
            customDefaultStyles = new ArrayList<>();
        }
        customDefaultStyles.add(styles);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.node.IElementNode#getLang()
     */
    @Override
    public String getLang() {
        if (lang != null) {
            return lang;
        } else {
            INode parent = parentNode;
            lang = parent instanceof IElementNode ? ((IElementNode) parent).getLang() : null;
            if (lang == null) {
                // Set to empty string to "cache", i.e. not to traverse parent chain each time the method is called for
                // documents with no "lang" attribute
                lang = "";
            }
            return lang;
        }
    }

    /**
     * Returns the element text.
     *
     * @return the text
     */
    public String text() {
        return element.text();
    }
}

