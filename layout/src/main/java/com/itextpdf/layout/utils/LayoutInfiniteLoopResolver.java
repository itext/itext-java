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
package com.itextpdf.layout.utils;

/**
 * This resolver is used during the layout process to prevent infinite loops.
 */
public class LayoutInfiniteLoopResolver {
    private static final int DEFAULT_LIMIT = 1_000_000;
    private final int maxPagesCountForSingleElement;

    /**
     * Creates default instance of {@link LayoutInfiniteLoopResolver}. Limit in this case is set to 333_333 pages.
     */
    public LayoutInfiniteLoopResolver() {
        maxPagesCountForSingleElement = DEFAULT_LIMIT;
    }

    /**
     * Creates {@link LayoutInfiniteLoopResolver} instance.
     * <p>
     * This resolver is used during the layout process to prevent infinite loops. In particular,
     * it limits the amount of times same element will be split across multiple pages. If the limit is exceeded,
     * exception is thrown. It is guaranteed, that this limit will not be exceeded,
     * unless the document contains at least the same amount of pages, as specified in the limit.
     *
     * @param maxPagesCountForSingleElement property which defines,
     *                                      how many times single element can be split across multiple pages
     */
    public LayoutInfiniteLoopResolver(int maxPagesCountForSingleElement) {
        // In here we multiply provided number by 3,
        // because only after the multiplication it corresponds to the number of pages per each element.
        // In particular same element may be layouted on the same page if keep_together or forced_placement is set.
        this.maxPagesCountForSingleElement = maxPagesCountForSingleElement * 3;
    }

    /**
     * Gets maximum pages count per element.
     * <p>
     * This property defines, how many times single element can be split across multiple pages.
     * It is used to detect potential infinite loops during the layout process.
     *
     * @return maximum pages count per element
     */
    public int getMaxPagesCountForSingleElement() {
        return maxPagesCountForSingleElement;
    }
}
