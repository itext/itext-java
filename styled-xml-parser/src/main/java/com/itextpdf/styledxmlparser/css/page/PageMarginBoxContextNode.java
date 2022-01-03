/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.CssContextNode;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IAttributes;
import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@link CssContextNode} implementation for page margin box contexts.
 */
public class PageMarginBoxContextNode extends CssContextNode implements ICustomElementNode {

    /** The Constant PAGE_MARGIN_BOX_TAG. */
    public static final String PAGE_MARGIN_BOX_TAG = "_064ef03_page-margin-box";

    /** The margin box name. */
    private String marginBoxName;

    private Rectangle pageMarginBoxRectangle;
    private Rectangle containingBlockForMarginBox;

    /**
     * Creates a new {@link PageMarginBoxContextNode} instance.
     *
     * @param parentNode the parent node
     * @param marginBoxName the margin box name
     */
    public PageMarginBoxContextNode(INode parentNode, String marginBoxName) {
        super(parentNode);
        this.marginBoxName = marginBoxName;
        if (!(parentNode instanceof PageContextNode)) {
            throw new IllegalArgumentException("Page-margin-box context node shall have a page context node as parent.");
        }
    }

    /**
     * Gets the margin box name.
     *
     * @return the margin box name
     */
    public String getMarginBoxName() {
        return marginBoxName;
    }

    /**
     * Sets the rectangle in which page margin box contents are shown.
     *
     * @param pageMarginBoxRectangle the {@link Rectangle} defining position and dimensions of the margin box content
     *                               area
     */
    public void setPageMarginBoxRectangle(Rectangle pageMarginBoxRectangle) {
        this.pageMarginBoxRectangle = pageMarginBoxRectangle;
    }

    /**
     * Gets the rectangle in which page margin box contents should be shown.
     *
     * @return the {@link Rectangle} defining position and dimensions of the margin box content area
     */
    public Rectangle getPageMarginBoxRectangle() {
        return pageMarginBoxRectangle;
    }

    /**
     * Sets the containing block rectangle for the margin box, which is used for calculating
     * some of the margin box properties relative values.
     *
     * @param containingBlockForMarginBox the {@link Rectangle} which is used as a reference for some
     *                                    margin box relative properties calculations.
     */
    public void setContainingBlockForMarginBox(Rectangle containingBlockForMarginBox) {
        this.containingBlockForMarginBox = containingBlockForMarginBox;
    }

    /**
     * @return the {@link Rectangle} which is used as a reference for some
     * margin box relative properties calculations.
     */
    public Rectangle getContainingBlockForMarginBox() {
        return containingBlockForMarginBox;
    }

    @Override
    public String name() {
        return PageMarginBoxContextNode.PAGE_MARGIN_BOX_TAG;
    }

    @Override
    public IAttributes getAttributes() {
        return new AttributesStub();
    }

    @Override
    public String getAttribute(String key) {
        return null;
    }

    @Override
    public List<Map<String, String>> getAdditionalHtmlStyles() {
        return null;
    }

    @Override
    public void addAdditionalHtmlStyles(Map<String, String> styles) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLang() {
        throw new UnsupportedOperationException();
    }

    /**
     * A simple {@link IAttributes} implementation.
     */
    private static class AttributesStub implements IAttributes {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAttribute(String key) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setAttribute(String key, String value) {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<IAttribute> iterator() {
            return Collections.<IAttribute>emptyIterator();
        }
    }
}
