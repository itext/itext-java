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
