package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.Document;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutResult;

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

    protected LayoutArea getNextArea(LayoutResult overflowResult) {
        moveToNextPage();
        PageSize customPageSize = overflowResult != null ? overflowResult.getNewPageSize() : null;
        while (document.getPdfDocument().getNumOfPages() >= currentPageNumber && document.getPdfDocument().getPage(currentPageNumber).isFlushed()) {
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
            ensureDocumentHasNPages(resultRenderer.getOccupiedArea().getPageNumber(), null);
            int pageNum = resultRenderer.getOccupiedArea().getPageNumber();
            PdfPage correspondingPage = document.getPdfDocument().getPage(pageNum);

            if(document.getPdfDocument().getReader() != null && document.getPdfDocument().getWriter() != null){
                if(!wrappedContentPage.contains(pageNum)) {
                    correspondingPage.newContentStreamBefore().getOutputStream().writeBytes(OutputStream.getIsoBytes("q\n"));
                    correspondingPage.newContentStreamAfter().getOutputStream().writeBytes(OutputStream.getIsoBytes("Q\n"));
                    wrappedContentPage.add(pageNum);
                }
            }

            resultRenderer.draw(document.getPdfDocument(), new PdfCanvas(correspondingPage));
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
        while (document.getPdfDocument().getNumOfPages() < n) {
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
