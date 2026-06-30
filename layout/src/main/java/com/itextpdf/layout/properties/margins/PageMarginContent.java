/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.properties.margins;

import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;

import java.util.Objects;

/**
 * Class to store information about page margin content represented by {@link IElement} linked to {@link MarginBoxName}.
 */
public class PageMarginContent extends AbstractPageContent {
    private final MarginBoxName marginBoxName;

    /**
     * Creates new {@link PageMarginContent} instance.
     *
     * @param marginBoxName {@link MarginBoxName} specifying margin name based on its location on the page
     * @param marginContent {@link IElement} layout element with margin content
     */
    public PageMarginContent(MarginBoxName marginBoxName, IElement marginContent) {
        super(marginContent);
        this.marginBoxName = marginBoxName;
    }

    /**
     * Creates new {@link PageMarginContent} instance.
     *
     * <p>
     * The margin will have the specified size in points.
     *
     * @param marginBoxName {@link MarginBoxName} specifying margin name based on its location on the page
     * @param marginInPoints <code>float</code> specifying the margin in points
     */
    public PageMarginContent(MarginBoxName marginBoxName, float marginInPoints) {
        this(marginBoxName, getStaticMarginContent(marginBoxName, marginInPoints));
    }

    /**
     * Creates new {@link PageMarginContent} instance by copying existing one.
     *
     * @param other {@link PageMarginContent} instance to copy
     */
    public PageMarginContent(PageMarginContent other) {
        super(other);
        this.marginBoxName = other.marginBoxName;
    }

    /**
     * Gets the page margin box name {@link MarginBoxName} which is based on its location on the page.
     *
     * @return the margin box name
     */
    public MarginBoxName getMarginBoxName() {
        return marginBoxName;
    }

    /**
     * Creates {@link Div} layout element of the fixed size to represent a static margin.
     *
     * @param marginBoxName {@link MarginBoxName} specifying margin name based on its location on the page
     * @param marginInPoints {@code float} specifying the margin in points
     *
     * @return {@link Div} layout element with static size
     */
    private static Div getStaticMarginContent(MarginBoxName marginBoxName, float marginInPoints) {
        Div staticMarginContent = new Div();
        if (marginBoxName == MarginBoxName.TOP || marginBoxName == MarginBoxName.BOTTOM) {
            staticMarginContent.setHeight(marginInPoints);
        } else if (marginBoxName == MarginBoxName.LEFT || marginBoxName == MarginBoxName.RIGHT) {
            staticMarginContent.setWidth(marginInPoints);
        }
        return staticMarginContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageMarginContent that = (PageMarginContent) o;
        return Objects.equals(marginBoxName, that.marginBoxName) && Objects.equals(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) marginBoxName, getContent());
    }
}
