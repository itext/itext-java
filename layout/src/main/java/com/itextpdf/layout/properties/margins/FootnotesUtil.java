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

import com.itextpdf.layout.element.Footnote;

import java.util.List;

/**
 * Utility class to process footnotes for internal usage only.
 */
public final class FootnotesUtil {

    private FootnotesUtil() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Adds provided footnotes to the specified page via {@link PageMarginBoxes}.
     *
     * @param pageNum page number
     * @param footnotesToAdd list of {@link Footnote} instance to add
     * @param pageMarginBoxes {@link PageMarginBoxes} for the page
     */
    public static void addFootnotesToPage(int pageNum, List<Footnote> footnotesToAdd, PageMarginBoxes pageMarginBoxes) {
        // TODO DEVSIX-9981 We want to be able to customize this container by user.
        FootnotesContainer footnotesContainer = new FootnotesContainer();
        for (Footnote footnote : footnotesToAdd) {
            footnotesContainer.add(footnote);
        }
        PageFootnotesContent pageFootnotesContent = new PageFootnotesContent(footnotesContainer).setPageNumber(pageNum);
        pageMarginBoxes.addFootnotes(pageFootnotesContent);
    }
}
