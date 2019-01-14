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
    private Element element;
    
    /** The attributes. */
    private IAttributes attributes;
    
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

