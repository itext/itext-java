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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.SectionBreakUtil;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.itextpdf.layout.renderer.AbstractRenderer.EPS;

/**
 * Renderer for the {@link com.itextpdf.layout.element.SectionBreak} layout element.
 * Will terminate the current page content if any and start a new page.
 */
public class SectionBreakRenderer implements IRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SectionBreakRenderer.class);

    private final SectionBreak sectionBreak;

    private IRenderer parent;

    /**
     * Creates new {@link SectionBreakRenderer} instance.
     *
     * @param sectionBreak the {@link SectionBreak} that will be rendered by this object
     */
    public SectionBreakRenderer(SectionBreak sectionBreak) {
        this.sectionBreak = sectionBreak;
    }

    /**
     * Logs a warning about unexpected use of {@link SectionBreakRenderer}
     * because instances of this class are only used for terminating the current page content.
     *
     * @param renderer {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        LOGGER.warn(LayoutLogMessageConstant.SECTION_BREAK_UNEXPECTED);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        boolean anythingPlaced = false;
        boolean pageMarginsChanged = false;
        boolean pageSizeChanged = false;
        int pageNumber = layoutContext.getArea().getPageNumber();
        IRenderer parentRenderer = getParent();
        while (parentRenderer != null) {
            if (parentRenderer instanceof DocumentRenderer) {
                DocumentRenderer documentRenderer = (DocumentRenderer) parentRenderer;
                PdfPage currentPage = documentRenderer.getPdfDocument().getPage(pageNumber);
                float pageHeight = getPageEffectiveHeight(currentPage.getPageSize(), documentRenderer);
                anythingPlaced = Math.abs(pageHeight - layoutContext.getArea().getBBox().getHeight()) > EPS;

                PageSize sectionBreakPageSize = sectionBreak.getPageSize();
                pageSizeChanged = !currentPage.getPageSize().equalsWithEpsilon(sectionBreakPageSize == null ?
                        documentRenderer.document.getPdfDocument().getDefaultPageSize() : sectionBreakPageSize);

                if (anythingPlaced) {
                    PageMarginBoxes pageMarginBoxes = documentRenderer.document.getPageMargins(pageNumber);
                    PageMarginBoxes sectionBreakMargins = sectionBreak.getPageMargins();
                    if (sectionBreakMargins == null) {
                        pageMarginsChanged = pageMarginBoxes != null;
                    } else {
                        pageMarginsChanged = !sectionBreakMargins.equals(pageMarginBoxes);
                    }
                }
                break;
            }
            parentRenderer = parentRenderer.getParent();
        }
        // We're interested only in bottom coordinate of the already placed content.
        LayoutArea updatedArea = new LayoutArea(layoutContext.getArea().getPageNumber(),
                new Rectangle(0, layoutContext.getArea().getBBox().getTop(), 0, 0));
        SectionBreakUtil.breakPage(sectionBreak, pageSizeChanged || (anythingPlaced && pageMarginsChanged));
        return new LayoutResult(LayoutResult.NOTHING, anythingPlaced ? updatedArea : null, null, null, this)
                .setSectionBreak(sectionBreak);
    }

    /**
     * Logs a warning about unexpected use of {@link SectionBreakRenderer}
     * because instances of this class are only used for terminating the current page content.
     *
     * @param drawContext {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        LOGGER.warn(LayoutLogMessageConstant.SECTION_BREAK_UNEXPECTED);
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current page content.
     *
     * <p>
     * In case there is no current page content, empty area will be returned.
     *
     * @return {@inheritDoc}
     */
    @Override
    public LayoutArea getOccupiedArea() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasProperty(int property) {
        return false;
    }

    @Override
    public boolean hasOwnProperty(int property) {
        return false;
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current page content.
     *
     * @param property {@inheritDoc}
     * @param defaultValue {@inheritDoc}
     * @param <T1> {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public <T1> T1 getProperty(int property, T1 defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T1> T1 getProperty(int key) {
        return (T1) (Object) null;
    }

    @Override
    public <T1> T1 getOwnProperty(int property) {
        return (T1) (Object) null;
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        return (T1) (Object) null;
    }

    /**
     * Logs a warning about unexpected use of {@link SectionBreakRenderer}
     * because instances of this class are only used for terminating the current page content.
     *
     * @param property {@inheritDoc}
     * @param value {@inheritDoc}
     */
    @Override
    public void setProperty(int property, Object value) {
        LOGGER.warn(LayoutLogMessageConstant.SECTION_BREAK_UNEXPECTED);
    }

    @Override
    public void deleteOwnProperty(int property) {
        // Do nothing.
    }

    @Override
    public IRenderer setParent(IRenderer parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public IPropertyContainer getModelElement() {
        return sectionBreak;
    }

    @Override
    public IRenderer getParent() {
        return this.parent;
    }

    @Override
    public List<IRenderer> getChildRenderers() {
        return null;
    }

    @Override
    public boolean isFlushed() {
        return false;
    }

    /**
     * Logs a warning about unexpected use of {@link SectionBreakRenderer}
     * because instances of this class are only used for terminating the current page content.
     *
     * @param dx {@inheritDoc}
     * @param dy {@inheritDoc}
     */
    @Override
    public void move(float dx, float dy) {
        LOGGER.warn(LayoutLogMessageConstant.SECTION_BREAK_UNEXPECTED);
    }

    @Override
    public IRenderer getNextRenderer() {
        return null;
    }

    private static float getPageEffectiveHeight(Rectangle pageSize, DocumentRenderer renderer) {
        float bottomMargin = (float) renderer.getPropertyAsFloat(Property.MARGIN_BOTTOM);
        float topMargin = (float) renderer.getPropertyAsFloat(Property.MARGIN_TOP);
        return pageSize.getHeight() - bottomMargin - topMargin;
    }
}
