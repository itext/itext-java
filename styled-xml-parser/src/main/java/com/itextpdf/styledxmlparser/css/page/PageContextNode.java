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
package com.itextpdf.styledxmlparser.css.page;

import com.itextpdf.styledxmlparser.css.CssContextNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link CssContextNode} implementation for page contexts.
 */
public class PageContextNode extends CssContextNode {
    
    /** The page type name. */
    private String pageTypeName;
    
    /** The page classes. */
    private List<String> pageClasses;

    /**
     * Creates a new {@link PageContextNode} instance.
     */
    public PageContextNode() {
        this(null);
    }

    /**
     * Creates a new {@link PageContextNode} instance.
     *
     * @param parentNode the parent node
     */
    public PageContextNode(INode parentNode) {
        super(parentNode);
        this.pageClasses = new ArrayList<>();
    }

    /**
     * Adds a page class.
     *
     * @param pageClass the page class
     * @return the page context node
     */
    public PageContextNode addPageClass(String pageClass) {
        this.pageClasses.add(pageClass.toLowerCase());
        return this;
    }

    /**
     * Gets the page type name.
     *
     * @return the page type name
     */
    public String getPageTypeName() {
        return this.pageTypeName;
    }

    /**
     * Sets the page type name.
     *
     * @param pageTypeName the page type name
     * @return the page context node
     */
    public PageContextNode setPageTypeName(String pageTypeName) {
        this.pageTypeName = pageTypeName;
        return this;
    }

    /**
     * Gets the list of page classes.
     *
     * @return the page classes
     */
    public List<String> getPageClasses() {
        return Collections.unmodifiableList(pageClasses);
    }
}
