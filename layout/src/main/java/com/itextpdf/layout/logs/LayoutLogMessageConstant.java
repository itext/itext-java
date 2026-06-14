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
package com.itextpdf.layout.logs;

/**
 * Class containing constants to be used in layout.
 */
public final class LayoutLogMessageConstant {

    public static final String AREA_BREAK_UNEXPECTED = "Unexpected use of AreaBreakRenderer detected, " +
            "which may indicate an issue with layout processing.";

    public static final String ELEMENT_DOES_NOT_FIT_AREA = "Element does not fit current area. {0}";

    public static final String FLEX_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK =
            "Flex container should not contain area break or section break.";

    public static final String FLEX_ITEM_CONTAINS_AREA_BREAK_OR_SECTION_BREAK = "Flex item contains AreaBreak or "
            + "SectionBreak causing unexpected layout result. The cross size of the flex item will be 0.";

    public static final String FLEX_ITEM_LAYOUT_RESULT_IS_NOT_FULL =
            "Flex item layout result isn't full, but it must be. The cross size of the flex item will be 0.";

    public static final String FOOTNOTE_NUM_PER_DOCUMENT_CANNOT_BE_CHANGED = "Footnote numbering per document " +
            "cannot be changed once it's specified. New numbering config will be ignored.";

    public static final String FOOTNOTE_NUM_PER_DOCUMENT_SHOULD_BE_FIRST = "Footnote numbering per document cannot " +
            "be set after other types. Previous numbering config will be used.";

    public static final String GRID_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK =
            "Grid container should not contain area break or section break.";

    public static final String GRID_ITEM_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK =
            "Grid item should not contain area break or section break.";

    public static final String PAGE_CONTENT_CANNOT_BE_DRAWN =
            "Page {0} content cannot be drawn for page {1}.";

    public static final String SECTION_BREAK_LAYOUT_ON_PAGE_0 = "An attempt to layout SectionBreak on page 0 "
            + "happened, which may indicate that SectionBreak was added to an element that does not support it.";

    public static final String SECTION_BREAK_UNEXPECTED = "Unexpected use of SectionBreakRenderer detected, " +
            "which may indicate an issue with layout processing.";

    public static final String TYPOGRAPHY_NOT_FOUND_INFO = "Cannot find pdfCalligraph module, "
            + "some languages in {0} might require this module when the following OpenTypeFont features are "
            + "obligatory for text rendering {1}";

    public static final String TYPOGRAPHY_NOT_FOUND_WARNING = "Cannot find typography module (pdfCalligraph), "
            + "which was implicitly required by {0}. "
            + "See https://itextpdf.com/products/pdfcalligraph for more information.";

    private LayoutLogMessageConstant() {
        // Private constructor will prevent the instantiation of this class directly.
    }
}
