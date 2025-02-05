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

import com.itextpdf.layout.Document;

/**
 * A {@link ILargeElement} is a layout element which may get added to
 * indefinitely, making the object prohibitively large.
 * In order to avoid consuming and holding on to undesirable amounts of
 * resources, the contents of a {@link ILargeElement} can be flushed regularly
 * by client code, e.g. at page boundaries or after a certain amount of additions.
 */
public interface ILargeElement extends IElement {

    /**
     * Checks whether an element has already been marked as complete.
     * @return the completion marker boolean
     */
    boolean isComplete();

    /**
     * Indicates that all the desired content has been added to this large element.
     */
    void complete();

    /**
     * Writes the newly added content to the document.
     */
    void flush();

    /**
     * Writes to the output document the content which has just been added to it.
     *
     * <p>
     * This method is called automatically for the newly added {@link ILargeElement} to be immediately placed
     * in the page contents after it is added to the {@link Document}, so it shouldn't be used in any other places.
     */
    void flushContent();

    /**
     * Sets the document this element is bound to.
     * We cannot write a large element into several documents simultaneously because we would need
     * more bulky interfaces for this feature. For now we went for simplicity.
     * @param document the document
     */
    void setDocument(Document document);
}
