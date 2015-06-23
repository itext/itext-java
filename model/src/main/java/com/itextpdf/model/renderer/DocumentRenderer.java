package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.Document;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DocumentRenderer extends AbstractRenderer {

    protected Document document;
    protected boolean immediateFlush = true;
    protected LayoutArea currentArea = null;
    protected int currentPageNumber = 0;

    public DocumentRenderer(Document document) {
        this(document, true);
    }

    public DocumentRenderer(Document document, boolean immediateFlush) {
        this.document = document;
        this.immediateFlush = immediateFlush;
        this.modelElement = document;
    }

    @Override
    public void addChild(IRenderer renderer) {
        super.addChild(renderer);

        if (currentArea == null) {
            currentArea = getNextArea();
//            try {
//                new PdfCanvas(document.getPdfDocument().getPage(currentArea.getPageNumber())).rectangle(currentArea.getBBox()).stroke();
//            }  catch (Exception exc) {}
        }

        if (childRenderers.size() != 0 && childRenderers.get(childRenderers.size() - 1) == renderer) {
            List<IRenderer> resultRenderers = new ArrayList<>();
            LayoutResult result = null;

            LayoutArea storedArea = null;
            LayoutArea nextStoredArea = null;
            while (renderer != null && (result = renderer.layout(new LayoutContext(currentArea.clone()))).getStatus() != LayoutResult.FULL) {
                if (result.getStatus() == LayoutResult.PARTIAL) {
                    resultRenderers.add(result.getSplitRenderer());
                    if (nextStoredArea != null){
                        currentArea = nextStoredArea;
                        currentPageNumber = nextStoredArea.getPageNumber();
                        nextStoredArea = null;
                    } else {
                        getNextArea();
                    }
                } else if (result.getStatus() == LayoutResult.NOTHING) {
                    if (currentArea.isEmptyArea() && !(renderer instanceof AreaBreakRenderer)){
                        Logger logger = LoggerFactory.getLogger(DocumentRenderer.class);
                        logger.warn("Element doesn't fit current area. KeepTogether property will be ignored.");
                        result.getOverflowRenderer().getModelElement().setProperty(Property.KEEP_TOGETHER, false);
                        renderer = result.getOverflowRenderer();
                        if (storedArea != null){
                            nextStoredArea = currentArea;
                            currentArea = storedArea;
                            currentPageNumber = storedArea.getPageNumber();
                        }
                        continue;
                    }
                    storedArea = currentArea;
                    if (result.getNewPageSize() != null)
                        getNextPageArea(result.getNewPageSize());
                    else {
                        getNextArea();
                    }
                }

                renderer = result.getOverflowRenderer();
            }
            assert result != null;
            currentArea.getBBox().setHeight(currentArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());
            currentArea.setEmptyArea(false);
            if (renderer != null)
                resultRenderers.add(renderer);

            for (IRenderer resultRenderer : resultRenderers) {
                alignChildHorizontally(resultRenderer, currentArea.getBBox().getWidth());
            }

            // TODO flush by page, not by elements?
            if (immediateFlush) {
                for (IRenderer resultRenderer : resultRenderers) {
                    flushSingleRenderer(resultRenderer);
                }
            }

            childRenderers.remove(childRenderers.size() - 1);
            childRenderers.addAll(resultRenderers);
        } else {
            Integer positionedPageNumber = renderer.getProperty(Property.PAGE_NUMBER);
            if (positionedPageNumber == null)
                positionedPageNumber = currentPageNumber;
            renderer.layout(new LayoutContext(new LayoutArea(positionedPageNumber, currentArea.getBBox().clone())));

            // TODO flush by page, not by elements?
            if (immediateFlush) {
                flushSingleRenderer(renderer);
            }
        }
    }

    // Drawing of content. Might need to rename.
    public void flush() {
        for (IRenderer resultRenderer: childRenderers) {
            flushSingleRenderer(resultRenderer);
        }
        for (IRenderer resultRenderer : positionedRenderers) {
            flushSingleRenderer(resultRenderer);
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        throw new RuntimeException();
    }

    public LayoutArea getNextArea() {
        currentPageNumber++;
        ensureDocumentHasNPages(currentPageNumber);
        PdfPage newPage = document.getPdfDocument().getPage(currentPageNumber);
        return (currentArea = new LayoutArea(currentPageNumber, document.getPdfDocument().getDefaultPageSize().getEffectiveArea()));
    }

    public LayoutArea getNextPageArea(PageSize pageSize) {
        PdfPage newPage = document.getPdfDocument().addNewPage(pageSize);
        return (currentArea = new LayoutArea(document.getPdfDocument().getNumOfPages(), pageSize.getEffectiveArea()));
    }

    @Override
    public LayoutArea getOccupiedArea() {
        throw new RuntimeException();
    }

    protected void flushSingleRenderer(IRenderer resultRenderer) {
        if (!resultRenderer.isFlushed()) {
            ensureDocumentHasNPages(resultRenderer.getOccupiedArea().getPageNumber());
            PdfPage correspondingPage = document.getPdfDocument().getPage(resultRenderer.getOccupiedArea().getPageNumber());
            resultRenderer.draw(document.getPdfDocument(), new PdfCanvas(correspondingPage));
        }
    }

    private void ensureDocumentHasNPages(int n) {
        while (document.getPdfDocument().getNumOfPages() < n) {
            PdfPage newPage = document.getPdfDocument().addNewPage();
        }
    }


}
