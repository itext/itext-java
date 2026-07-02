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

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.SectionBreakRenderer;

/**
 * A layout object that terminates the current page content if any and starts the new page.
 *
 * <p>
 * If no {@link PageSize} and {@link PageMarginBoxes} are given,
 * the new content section will have default page size and page margins.
 *
 * <p>
 * Specified (or default if not specified) {@link PageSize} and {@link PageMarginBoxes}
 * will be applied for all next pages until it'll be overridden by other {@link SectionBreak}
 * or {@link AreaBreak} elements.
 */
public class SectionBreak extends AbstractElement<SectionBreak> {
    private PageSize pageSize;
    private PageMarginBoxes pageMarginBoxes;
    private FootnotesProperties footnotesProperties;

    private boolean breakPage;

    /**
     * Creates new {@link SectionBreak} instance.
     *
     * <p>
     * The new content section will have default page size and page margins.
     */
    public SectionBreak() {
        // Default constructor.
    }

    /**
     * Creates new {@link SectionBreak} instance.
     *
     * <p>
     * The new content section will have the specified page size and default page margins.
     *
     * @param pageSize {@link PageSize} page size of the new content section
     */
    public SectionBreak(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Creates new {@link SectionBreak} instance.
     *
     * <p>
     * The new content section will have the specified page margins and default page size.
     *
     * @param pageMarginBoxes {@link PageMarginBoxes} page margins of the new content section
     */
    public SectionBreak(PageMarginBoxes pageMarginBoxes) {
        this.pageMarginBoxes = pageMarginBoxes;
    }

    /**
     * Creates new {@link SectionBreak} instance.
     *
     * <p>
     * The new content section will have the specified page size and page margins.
     *
     * @param pageSize {@link PageSize} page size of the new content section
     * @param pageMarginBoxes {@link PageMarginBoxes} page margins of the new content section
     */
    public SectionBreak(PageSize pageSize, PageMarginBoxes pageMarginBoxes) {
        this.pageSize = pageSize;
        this.pageMarginBoxes = pageMarginBoxes;
    }

    /**
     * Gets the page size.
     *
     * @return the {@link PageSize page size} of the next content section
     */
    public PageSize getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size.
     *
     * @param pageSize the new {@link PageSize page size} of the next content section
     *
     * @return this same instance
     */
    public SectionBreak setPageSize(PageSize pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Gets the page margins.
     *
     * @return the {@link PageMarginBoxes page margins} of the next content section
     */
    public PageMarginBoxes getPageMargins() {
        return pageMarginBoxes;
    }

    /**
     * Sets the page margins.
     *
     * @param pageMarginBoxes the {@link PageMarginBoxes page margins} of the next content section
     *
     * @return this same instance
     */
    public SectionBreak setPageMargins(PageMarginBoxes pageMarginBoxes) {
        this.pageMarginBoxes = pageMarginBoxes;
        return this;
    }

    /**
     * Gets {@link FootnotesProperties} specified for the document via {@link SectionBreak} to customize footnotes.
     *
     * @return {@link FootnotesProperties} specified for the document
     */
    public FootnotesProperties getFootnotesProperties() {
        return footnotesProperties;
    }

    /**
     * Sets {@link FootnotesProperties} for the document.
     *
     * @param footnotesProperties {@link FootnotesProperties} to customize footnotes
     *
     * @return this same {@link SectionBreak} instance
     */
    public SectionBreak setFootnotesProperties(FootnotesProperties footnotesProperties) {
        this.footnotesProperties = footnotesProperties;
        return this;
    }

    /**
     * Checks whether this {@link SectionBreak} should add page break.
     *
     * <p>
     * Page won't break in case SectionBreak is added to the empty page with the same page size
     * or if page margins and page size were not changed. So {@code breakPage} field also checks
     * whether SectionBreak changes page margins or page size and is not the 1st element on the page.
     *
     * @return {@code true} if page break is expected, {@code false} otherwise
     */
    boolean breakPage() {
        return breakPage;
    }

    /**
     * Defines whether this {@link SectionBreak} should add page break.
     * Controlled by {@link SectionBreakRenderer#layout(LayoutContext)}.
     *
     * <p>
     * Page shouldn't break in case SectionBreak is added to the empty page with the same page size
     * or if page margins and page size were not changed. So {@code breakPage} field also checks
     * whether SectionBreak changes page margins or page size and is not the 1st element on the page.
     *
     * @param breakPage {@code true} if page break is expected, {@code false} otherwise
     *
     * @return this same {@link SectionBreak} instance
     */
    SectionBreak breakPage(boolean breakPage) {
        this.breakPage = breakPage;
        return this;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new SectionBreakRenderer(this);
    }
}
