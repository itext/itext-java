/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.CssContextNode;
import com.itextpdf.styledxmlparser.node.INode;

/**
 * {@link CssContextNode} implementation for page margin box contexts.
 */
public class PageMarginBoxContextNode extends CssContextNode {

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
     * @param pageMarginBoxRectangle the {@link Rectangle} defining position and dimensions of the margin box content area
     */
    public void setPageMarginBoxRectangle(Rectangle pageMarginBoxRectangle) {
        this.pageMarginBoxRectangle = pageMarginBoxRectangle;
    }

    /**
     * Gets the rectangle in which page margin box contents should be shown.
     * @return the {@link Rectangle} defining position and dimensions of the margin box content area
     */
    public Rectangle getPageMarginBoxRectangle() {
        return pageMarginBoxRectangle;
    }

    /**
     * Sets the containing block rectangle for the margin box, which is used for calculating
     * some of the margin box properties relative values.
     * @param containingBlockForMarginBox the {@link Rectangle} which is used as a reference for some
     *                                    margin box relative properties calculations.
     */
    public void setContainingBlockForMarginBox(Rectangle containingBlockForMarginBox) {
        this.containingBlockForMarginBox = containingBlockForMarginBox;
    }

    /**
     * @return the {@link Rectangle} which is used as a reference for some
     *                                    margin box relative properties calculations.
     */
    public Rectangle getContainingBlockForMarginBox() {
        return containingBlockForMarginBox;
    }
}
