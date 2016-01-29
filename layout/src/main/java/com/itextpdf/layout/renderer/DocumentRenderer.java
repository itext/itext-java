package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;

import java.util.ArrayList;
import java.util.List;

public class DocumentRenderer extends RootRenderer {

    protected Document document;
    protected List<Integer> wrappedContentPage = new ArrayList<>();

    public DocumentRenderer(Document document) {
        this(document, true);
    }

    public DocumentRenderer(Document document, boolean immediateFlush) {
        this.document = document;
        this.immediateFlush = immediateFlush;
        this.modelElement = document;
    }

    @Override
    public LayoutArea getOccupiedArea() {
        throw new IllegalStateException("Not applicable for DocumentRenderer");
    }

    @Override
    public DocumentRenderer getNextRenderer() {
        return null;
    }

    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        moveToNextPage();
        PageSize customPageSize = overflowResult != null ? overflowResult.getNewPageSize() : null;
        while (document.getPdfDocument().getNumberOfPages() >= currentPageNumber && document.getPdfDocument().getPage(currentPageNumber).isFlushed()) {
            currentPageNumber++;
        }
        PageSize lastPageSize = ensureDocumentHasNPages(currentPageNumber, customPageSize);
        if (lastPageSize == null) {
            lastPageSize = new PageSize(document.getPdfDocument().getPage(currentPageNumber).getPageSize());
        }
        return (currentArea = new LayoutArea(currentPageNumber, document.getPageEffectiveArea(lastPageSize)));
    }

    protected void flushSingleRenderer(IRenderer resultRenderer) {
        if (!resultRenderer.isFlushed()) {
            int pageNum = resultRenderer.getOccupiedArea().getPageNumber();

            PdfDocument pdfDocument = document.getPdfDocument();
            ensureDocumentHasNPages(pageNum, null);
            PdfPage correspondingPage = pdfDocument.getPage(pageNum);

            boolean wrapOldContent = pdfDocument.getReader() != null && pdfDocument.getWriter() != null &&
                    correspondingPage.getContentStreamCount() > 0 && correspondingPage.getLastContentStream().getLength() > 0 &&
                    !wrappedContentPage.contains(pageNum) && pdfDocument.getNumberOfPages() >= pageNum;
            wrappedContentPage.add(pageNum);

            if (pdfDocument.isTagged()) {
                pdfDocument.getTagStructure().setPage(correspondingPage);
            }
            resultRenderer.draw(new DrawContext(pdfDocument, new PdfCanvas(correspondingPage, wrapOldContent), pdfDocument.isTagged()));
        }
    }

    protected PageSize addNewPage(PageSize customPageSize) {
        if (customPageSize != null) {
            document.getPdfDocument().addNewPage(customPageSize);
        } else {
            document.getPdfDocument().addNewPage();
        }
        return customPageSize != null ? customPageSize : document.getPdfDocument().getDefaultPageSize();
    }

    /**
     * Adds some pages so that the overall number is at least n.
     * Returns the page size of the n'th page.
     */
    private PageSize ensureDocumentHasNPages(int n, PageSize customPageSize) {
        PageSize lastPageSize = null;
        while (document.getPdfDocument().getNumberOfPages() < n) {
            lastPageSize = addNewPage(customPageSize);
        }
        return lastPageSize;
    }

    private void moveToNextPage() {
        // We don't flush this page immediately, but only flush previous one because of manipulations with areas in case
        // of keepTogether property.
        if (immediateFlush && currentPageNumber > 1) {
            document.getPdfDocument().getPage(currentPageNumber - 1).flush();
        }
        currentPageNumber++;
    }

}
