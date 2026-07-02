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
package com.itextpdf.layout.element;

import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.renderer.SectionBreakRenderer;

/**
 * This file is a helper class for {@link SectionBreak} for internal usage only.
 * Be aware that its API and functionality may be changed in the future.
 */
public final class SectionBreakUtil {

    /**
     * Checks whether provided {@link SectionBreak} should add page break.
     *
     * <p>
     * Page won't break in case SectionBreak is added to the empty page with the same page size
     * or if page margins and page size were not changed. So {@code breakPage} field also checks
     * whether SectionBreak changes page margins or page size and is not the 1st element on the page.
     *
     * @param sectionBreak {@link SectionBreak} to check
     *
     * @return {@code true} if page break is expected, {@code false} otherwise
     */
    public static boolean breakPage(SectionBreak sectionBreak) {
        return sectionBreak.breakPage();
    }

    /**
     * Defines whether provided {@link SectionBreak} should add page break.
     * Controlled by {@link SectionBreakRenderer#layout(LayoutContext)}.
     *
     * <p>
     * Page shouldn't break in case SectionBreak is added to the empty page with the same page size
     * or if page margins and page size were not changed. So {@code breakPage} field also checks
     * whether SectionBreak changes page margins or page size and is not the 1st element on the page.
     *
     * @param sectionBreak {@link SectionBreak} to check
     * @param breakPage {@code true} if page break is expected, {@code false} otherwise
     */
    public static void breakPage(SectionBreak sectionBreak, boolean breakPage) {
        sectionBreak.breakPage(breakPage);
    }

    private SectionBreakUtil() {
        // Private constructor will prevent the instantiation of this class directly.
    }
}
