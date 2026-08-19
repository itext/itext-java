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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.SectionBreakUtil;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Transform;
import com.itextpdf.layout.properties.margins.FootnoteNumberingConfig;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentRenderer extends RootRenderer {
    final FootnotesCounterHandler footnotesCounterHandler = new FootnotesCounterHandler();

    protected Document document;
    protected List<Integer> wrappedContentPage = new ArrayList<>();
    protected TargetCounterHandler targetCounterHandler = new TargetCounterHandler();

    private Set<Integer> contentProcessedPages = new HashSet<>();
    private PageMarginBoxesDrawingHandler marginBoxesHandler;
    private boolean dynamicPageMarginsUsed = false;
    private PageSize currentPageSize = null;
    private SectionBreak prevSectionBreak = null;

    public DocumentRenderer(Document document) {
        this(document, true);
    }

    public DocumentRenderer(Document document, boolean immediateFlush) {
        this.document = document;
        this.immediateFlush = immediateFlush;
        this.modelElement = document;
        this.marginBoxesHandler = new DocumentRenderer.PageMarginBoxesDrawingHandler().setDocumentRenderer(this);
        if (this.document != null) {
            this.document.getPdfDocument().addEventHandler(PdfDocumentEvent.END_PAGE, marginBoxesHandler);
        }
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
    public void addChild(IRenderer renderer) {
        if (renderer instanceof SectionBreakRenderer) {
            SectionBreak sectionBreak = (SectionBreak) renderer.getModelElement();
            if (sectionBreak != null) {
                this.currentPageSize = sectionBreak.getPageSize();
            }
        }
        super.addChild(renderer);
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

    /**
     * Removes renderer-owned event handlers before relayout replaces this renderer instance.
     */
    public void removeEventHandlersForRelayout() {
        document.getPdfDocument().removeEventHandler(marginBoxesHandler);
    }

    @Override
    protected void flushOnClose() {
        super.flushOnClose();

        document.getPdfDocument().removeEventHandler(marginBoxesHandler);
        if (!document.getPdfDocument().isClosed()) {
            for (int i = 1; i <= document.getPdfDocument().getNumberOfPages(); ++i) {
                PdfPage page = document.getPdfDocument().getPage(i);
                if (!page.isFlushed()) {
                    marginBoxesHandler.processPage(document.getPdfDocument(), i);
                }
            }
        }
    }

    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        flushWaitingDrawingElements(false);
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        if (taggingHelper != null) {
            taggingHelper.releaseFinishedHints();
        }

        AreaBreak areaBreak = overflowResult != null && overflowResult.getAreaBreak() != null ?
                overflowResult.getAreaBreak() : null;
        SectionBreak sectionBreak = overflowResult != null && overflowResult.getSectionBreak() != null ?
                overflowResult.getSectionBreak() : null;

        if (overflowResult != null && overflowResult.getOccupiedArea() != null) {
            // Persist margins for pages that already received content before moving layout to another page.
            savePageMarginsForProcessedPage(overflowResult.getOccupiedArea().getPageNumber());
        }

        int currentPageNumber = currentArea == null ? 0 : currentArea.getPageNumber();
        if (areaBreak != null && areaBreak.getType() == AreaBreakType.LAST_PAGE) {
            while (currentPageNumber < document.getPdfDocument().getNumberOfPages()) {
                possiblyFlushPreviousPage(currentPageNumber);
                currentPageNumber++;
            }
        } else {
            possiblyFlushPreviousPage(currentPageNumber);
            // Don't bump page number in case SectionBreak is added to the empty page which is not the 1st.
            // Or if page margins weren't changed.
            if (sectionBreak == null || SectionBreakUtil.breakPage(sectionBreak)) {
                currentPageNumber++;
            }
        }

        PageSize customPageSize = currentPageSize;
        if (areaBreak != null) {
            customPageSize = areaBreak.getPageSize();
        } else if (sectionBreak != null) {
            customPageSize = sectionBreak.getPageSize();
            currentPageSize = customPageSize;
        }

        while (document.getPdfDocument().getNumberOfPages() >= currentPageNumber &&
                document.getPdfDocument().getPage(currentPageNumber).isFlushed()) {
            currentPageNumber++;
        }
        PageSize lastPageSize = ensureDocumentHasNPages(currentPageNumber, customPageSize);
        if (lastPageSize == null) {
            lastPageSize = new PageSize(document.getPdfDocument().getPage(currentPageNumber).getTrimBox());
        }

        if (sectionBreak != null) {
            this.document.setPageMargins(currentPageNumber, sectionBreak.getPageMargins());

            FootnotesProperties sectionBreakFootnotesProperties = sectionBreak.getFootnotesProperties();
            if (sectionBreakFootnotesProperties != null) {
                document.setFootnotesProperties(sectionBreakFootnotesProperties);
            }
        }
        FootnotesProperties footnotesProperties = document.getFootnotesProperties();
        FootnoteNumberingConfig footnoteNumberingConfig = footnotesProperties.getFootnoteNumberingConfig();
        if (sectionBreak != null && FootnoteNumberingConfig.PER_SECTION == footnoteNumberingConfig) {
            this.latestFootnoteNumber.put(currentPageNumber, 0);
        } else if (FootnoteNumberingConfig.PER_PAGE != footnoteNumberingConfig) {
            this.latestFootnoteNumber.put(currentPageNumber,
                    this.latestFootnoteNumber.getOrDefault(currentPageNumber - 1, 0));
        }

        computeLayoutMargins(currentPageNumber);
        if (sectionBreak != null) {
            // Save section break to apply same page margins for all the following pages
            // in case their margins were not set via Document by page number, rule or function.
            prevSectionBreak = sectionBreak;
        }

        Rectangle updatedAreaRect = getCurrentPageEffectiveArea(lastPageSize);
        if (sectionBreak != null && currentArea.getPageNumber() == currentPageNumber &&
                overflowResult.getOccupiedArea() != null) {
            updatedAreaRect.setHeight(overflowResult.getOccupiedArea().getBBox().getY() - updatedAreaRect.getY());
        }
        return (currentArea = new RootLayoutArea(currentPageNumber, updatedAreaRect));
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

    @Override
    protected void shrinkCurrentAreaAndProcessRenderer(IRenderer renderer, List<IRenderer> resultRenderers,
            LayoutResult result) {
        if (result != null && result.getOccupiedArea() != null) {
            // Freeze margins when page content is laid out
            savePageMarginsForProcessedPage(result.getOccupiedArea().getPageNumber());
        }
        super.shrinkCurrentAreaAndProcessRenderer(renderer, resultRenderers, result);
    }

    /**
     * Adds new page with defined page size to PDF document.
     *
     * @param customPageSize the size of new page, can be null
     *
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
     *
     * @return the page size of the last created page, or null if no page was created
     */
    protected PageSize ensureDocumentHasNPages(int n, PageSize customPageSize) {
        PageSize lastPageSize = null;
        while (document.getPdfDocument().getNumberOfPages() < n) {
            lastPageSize = addNewPage(customPageSize);
        }
        return lastPageSize;
    }

    private void savePageMarginsForProcessedPage(int pageNumber) {
        if (!contentProcessedPages.contains(pageNumber)) {
            contentProcessedPages.add(pageNumber);
            document.setPageMargins(pageNumber, document.getPageMargins(pageNumber));
        }
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

    private void computeLayoutMargins(int pageNumber) {
        PageMarginBoxes pageMarginBoxes = this.document.getPageMargins(pageNumber);
        PdfPage page = document.getPdfDocument().getPage(pageNumber);
        if (pageMarginBoxes == null) {
            PageMarginBoxes prevPageMarginBoxes = prevSectionBreak == null ? null : prevSectionBreak.getPageMargins();
            if (this.document.isPageMarginsSpecified(pageNumber) || prevPageMarginBoxes == null) {
                this.resetDynamicPageMargins();
                return;
            }
            pageMarginBoxes = new PageMarginBoxes(prevPageMarginBoxes);

            PageSize prevPageSize = prevSectionBreak.getPageSize();
            if (prevPageSize == null) {
                prevPageSize = document.getPdfDocument().getDefaultPageSize();
            }
            if (prevPageSize.equalsWithEpsilon(page.getPageSize())) {
                this.document.setPageMargins(pageNumber, pageMarginBoxes);
                this.setDynamicPageMargins(pageMarginBoxes.getMarginSizes());
                return;
            }
        }

        float[] margins = pageMarginBoxes.layout(this, pageNumber, page.getPageSize());
        pageMarginBoxes.setMarginSizes(margins);

        // Set page margins for page number to prioritize them and save the layout result.
        this.document.setPageMargins(pageNumber, pageMarginBoxes);
        this.setDynamicPageMargins(margins);
    }

    private void setDynamicPageMargins(float[] margins) {
        dynamicPageMarginsUsed = true;
        setProperty(Property.MARGIN_TOP, margins[0]);
        setProperty(Property.MARGIN_RIGHT, margins[1]);
        setProperty(Property.MARGIN_BOTTOM, margins[2]);
        setProperty(Property.MARGIN_LEFT, margins[3]);
    }

    private void resetDynamicPageMargins() {
        if (dynamicPageMarginsUsed) {
            deleteOwnProperty(Property.MARGIN_TOP);
            deleteOwnProperty(Property.MARGIN_RIGHT);
            deleteOwnProperty(Property.MARGIN_BOTTOM);
            deleteOwnProperty(Property.MARGIN_LEFT);
            dynamicPageMarginsUsed = false;
        }
    }

    /**
     * Handler for drawing page margins on {@code END_PAGE} event.
     */
    private static final class PageMarginBoxesDrawingHandler extends AbstractPdfDocumentEventHandler {
        private DocumentRenderer documentRenderer;

        public PageMarginBoxesDrawingHandler() {
            // Default constructor.
        }

        PageMarginBoxesDrawingHandler setDocumentRenderer(DocumentRenderer documentRenderer) {
            this.documentRenderer = documentRenderer;
            return this;
        }

        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            if (event instanceof PdfDocumentEvent) {
                PdfPage page = ((PdfDocumentEvent) event).getPage();
                PdfDocument pdfDoc = event.getDocument();
                int pageNumber = pdfDoc.getPageNumber(page);
                processPage(pdfDoc, pageNumber);
            }
        }

        void processPage(PdfDocument document, int pageNumber) {
            PageMarginBoxes pageMarginBoxes = documentRenderer.document.getPageMargins(pageNumber);
            if (pageMarginBoxes != null) {
                pageMarginBoxes.draw(documentRenderer, document, pageNumber);
            }
        }
    }
}
