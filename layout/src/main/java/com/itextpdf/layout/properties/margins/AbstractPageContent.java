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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.IElement;

/**
 * Abstract class representing page content such as margins of footnotes.
 */
public abstract class AbstractPageContent {
    private final IElement content;

    private Rectangle rectangle;

    /**
     * Creates new {@link AbstractPageContent} instance.
     *
     * @param content {@link IElement} layout element with page content
     */
    protected AbstractPageContent(IElement content) {
        this.content = content;
    }

    /**
     * Creates new {@link AbstractPageContent} instance by copying existing one.
     *
     * @param other {@link AbstractPageContent} instance to copy
     */
    protected AbstractPageContent(AbstractPageContent other) {
        this.content = other.content;
        this.rectangle = other.rectangle;
    }

    /**
     * Returns layout element representing page content.
     *
     * @return {@link IElement} layout element for page margin content
     */
    public IElement getContent() {
        return content;
    }

    /**
     * Sets the rectangle in which page content is shown.
     *
     * @param rectangle {@link Rectangle} defining position and dimensions of the content area
     */
    void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * Gets the rectangle in which page content should be shown.
     *
     * @return the {@link Rectangle} defining position and dimensions of the content area
     */
    Rectangle getRectangle() {
        return rectangle;
    }
}
