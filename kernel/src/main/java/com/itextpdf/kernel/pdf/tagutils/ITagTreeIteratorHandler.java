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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.IStructureNode;

/**
 * Handler for {@link TagTreeIterator}.
 * Is used to handle specific events during the traversal.
 */
public interface ITagTreeIteratorHandler {

    /**
     * Called when the next element is reached during the traversal.
     *
     * @param elem the next element
     *
     * @return {@code true} if the iteration should be continued, {@code false} otherwise. Note that this value
     * is relevant only in case {@link TagTreeIterator.TreeTraversalOrder#PRE_ORDER} is used for the traversal
     */
    boolean nextElement(IStructureNode elem);
}
