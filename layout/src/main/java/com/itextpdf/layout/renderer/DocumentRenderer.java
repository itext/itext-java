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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Transform;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.ArrayList;
import java.util.List;

public class DocumentRenderer extends RootRenderer {

    protected Document document;
    protected List<Integer> wrappedContentPage = new ArrayList<>();
    protected TargetCounterHandler targetCounterHandler = new TargetCounterHandler();

    public DocumentRenderer(Document document) {
        this(document, true);
    }

    public DocumentRenderer(Document document, boolean immediateFlush) {
        this.document = document;
        this.immediateFlush = immediateFlush;
        this.modelElement = document;
    }

    /**
     * Get handler for target-counters.
     *
     * @return the {@link TargetCounterHandler} instance
     */
    public TargetCounterHandler getTargetCounterHandler() {
        return targetCounterHandler;
    }

    /**
     * Indicates if relayout is required for targetCounterHandler.
     *
     * @return true if relayout is required, false otherwise
     */
    public boolean isRelayoutRequired() {
        return targetCounterHandler.isRelayoutRequired();
    }

    @Override
    public LayoutArea getOccupiedArea() {
        throw new IllegalStateException("Not applicable for DocumentRenderer");
    }

    /**
     * For {@link DocumentRenderer}, this has a meaning of the renderer that will be used for relayout.
     *
     * @return relayout renderer.
     */
    @Override
    public IRenderer getNextRenderer() {
        DocumentRenderer renderer = new DocumentRenderer(document, immediateFlush);
        renderer.targetCounterHandler = new TargetCounterHandler(targetCounterHandler);
        return renderer;
    }

    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        flushWaitingDrawingElements(false);
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        if (taggingHelper != null) {
            taggingHelper.releaseFinishedHints();
        }
        AreaBreak areaBreak = overflowResult != null && overflowResult.getAreaBreak() != null ?
                overflowResult.getAreaBreak() : null;
        int currentPageNumber = currentArea == null ? 0 : currentArea.getPageNumber();
        if (areaBreak != null && areaBreak.getType() == AreaBreakType.LAST_PAGE) {
            while (currentPageNumber < document.getPdfDocument().getNumberOfPages()) {
                possiblyFlushPreviousPage(currentPageNumber);
                currentPageNumber++;
            }
        } else {
            possiblyFlushPreviousPage(currentPageNumber);
            currentPageNumber++;
        }
        PageSize customPageSize = areaBreak != null ? areaBreak.getPageSize() : null;
        while (document.getPdfDocument().getNumberOfPages() >= currentPageNumber &&
                document.getPdfDocument().getPage(currentPageNumber).isFlushed()) {
            currentPageNumber++;
        }
        PageSize lastPageSize = ensureDocumentHasNPages(currentPageNumber, customPageSize);
        if (lastPageSize == null) {
            lastPageSize = new PageSize(document.getPdfDocument().getPage(currentPageNumber).getTrimBox());
        }
        return (currentArea = new RootLayoutArea(currentPageNumber, getCurrentPageEffectiveArea(lastPageSize)));
    }

    protected void flushSingleRenderer(IRenderer resultRenderer) {
        linkRenderToDocument(resultRenderer, document.getPdfDocument());

        Transform transformProp = resultRenderer.<Transform>getProperty(Property.TRANSFORM);
        if (!waitingDrawingElements.contains(resultRenderer)) {
            processWaitingDrawing(resultRenderer, transformProp, waitingDrawingElements);
            if (FloatingHelper.isRendererFloating(resultRenderer) || transformProp != null)
                return;
        }

        // TODO Remove checking occupied area to be not null when DEVSIX-1655 is resolved.
        if (!resultRenderer.isFlushed() && null != resultRenderer.getOccupiedArea()) {
            int pageNum = resultRenderer.getOccupiedArea().getPageNumber();

            PdfDocument pdfDocument = document.getPdfDocument();
            ensureDocumentHasNPages(pageNum, null);
            PdfPage correspondingPage = pdfDocument.getPage(pageNum);
            if (correspondingPage.isFlushed()) {
                throw new PdfException(LayoutExceptionMessageConstant.CANNOT_DRAW_ELEMENTS_ON_ALREADY_FLUSHED_PAGES);
            }

            boolean wrapOldContent = pdfDocument.getReader() != null && pdfDocument.getWriter() != null &&
                    correspondingPage.getContentStreamCount() > 0 &&
                    correspondingPage.getLastContentStream().getLength() > 0 &&
                    !wrappedContentPage.contains(pageNum) && pdfDocument.getNumberOfPages() >= pageNum;
            wrappedContentPage.add(pageNum);

            if (pdfDocument.isTagged()) {
                pdfDocument.getTagStructureContext().getAutoTaggingPointer().setPageForTagging(correspondingPage);
            }
            resultRenderer.draw(new DrawContext(pdfDocument,
                    new PdfCanvas(correspondingPage, wrapOldContent), pdfDocument.isTagged()));
        }
    }

    /**
     * Adds new page with defined page size to PDF document.
     *
     * @param customPageSize the size of new page, can be null
     * @return the page size of created page
     */
    protected PageSize addNewPage(PageSize customPageSize) {
        if (customPageSize != null) {
            document.getPdfDocument().addNewPage(customPageSize);
        } else {
            document.getPdfDocument().addNewPage();
        }
        return customPageSize != null ? customPageSize : document.getPdfDocument().getDefaultPageSize();
    }

    /**
     * Ensures that PDF document has n pages. If document has fewer pages,
     * adds new pages by calling {@link #addNewPage(PageSize)} method.
     *
     * @param n the expected number of pages if document
     * @param customPageSize the size of created pages, can be null
     * @return the page size of the last created page, or null if no page was created
     */
    protected PageSize ensureDocumentHasNPages(int n, PageSize customPageSize) {
        PageSize lastPageSize = null;
        while (document.getPdfDocument().getNumberOfPages() < n) {
            lastPageSize = addNewPage(customPageSize);
        }
        return lastPageSize;
    }

    private Rectangle getCurrentPageEffectiveArea(PageSize pageSize) {
        float leftMargin = (float) getPropertyAsFloat(Property.MARGIN_LEFT);
        float bottomMargin = (float) getPropertyAsFloat(Property.MARGIN_BOTTOM);
        float topMargin = (float) getPropertyAsFloat(Property.MARGIN_TOP);
        float rightMargin = (float) getPropertyAsFloat(Property.MARGIN_RIGHT);
        return new Rectangle(pageSize.getLeft() + leftMargin,
                pageSize.getBottom() + bottomMargin,
                pageSize.getWidth() - leftMargin - rightMargin,
                pageSize.getHeight() - bottomMargin - topMargin);
    }

    private void possiblyFlushPreviousPage(int currentPageNumber) {
        if (immediateFlush && currentPageNumber > 1) {
            // We don't flush current page immediately, but only flush previous one
            // because of manipulations with areas in case of keepTogether property
            document.getPdfDocument().getPage(currentPageNumber - 1).flush();
        }
    }
}
