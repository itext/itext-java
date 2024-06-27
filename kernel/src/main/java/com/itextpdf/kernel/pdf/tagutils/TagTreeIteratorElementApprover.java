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
 * Element checker for {@link TagTreeIterator}.
 * It is used to check whether specific element should be traversed.
 */
public class TagTreeIteratorElementApprover {

    /**
     * Creates a new instance of {@link TagTreeIteratorElementApprover}
     */
    public TagTreeIteratorElementApprover() {
        // Empty constructor
    }

    /**
     * Checks whether the element should be traversed.
     *
     * @param elem the element to check
     * @return {@code true} if the element should be traversed, {@code false otherwise}
     */
    public boolean approve(IStructureNode elem) {
        return elem != null;
    }
}
