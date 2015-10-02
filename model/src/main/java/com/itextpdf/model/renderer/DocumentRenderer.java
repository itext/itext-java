package com.itextpdf.model.renderer;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.Document;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        }

        // Static layout
        if (childRenderers.size() != 0 && childRenderers.get(childRenderers.size() - 1) == renderer) {
            List<IRenderer> resultRenderers = new ArrayList<>();
            LayoutResult result = null;

            LayoutArea storedArea = null;
            LayoutArea nextStoredArea = null;
            while (renderer != null && (result = renderer.layout(new LayoutContext(currentArea.clone()))).getStatus() != LayoutResult.FULL) {
                if (result.getStatus() == LayoutResult.PARTIAL) {
                    if (result.getOverflowRenderer() instanceof ImageRenderer) {
                        ((ImageRenderer) result.getOverflowRenderer()).autoScale(currentArea);
                    } else {
                        processRenderer(result.getSplitRenderer(), resultRenderers);
                        if (nextStoredArea != null) {
                            currentArea = nextStoredArea;
                            currentPageNumber = nextStoredArea.getPageNumber();
                            nextStoredArea = null;
                        } else {
                            getNextArea();
                        }
                    }
                } else if (result.getStatus() == LayoutResult.NOTHING) {
                    if (result.getOverflowRenderer() instanceof ImageRenderer) {
                        if (currentArea.getBBox().getHeight() < ((ImageRenderer) result.getOverflowRenderer()).imageHeight) {
                            getNextArea();
                        }
                        ((ImageRenderer)result.getOverflowRenderer()).autoScale(currentArea);
                    } else {
                        if (currentArea.isEmptyArea() && !(renderer instanceof AreaBreakRenderer)) {
                            if (Boolean.valueOf(true).equals(result.getOverflowRenderer().getModelElement().getProperty(Property.KEEP_TOGETHER))) {
                                result.getOverflowRenderer().getModelElement().setProperty(Property.KEEP_TOGETHER, false);
                                Logger logger = LoggerFactory.getLogger(DocumentRenderer.class);
                                logger.warn(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA);
                            } else {
                                throw new PdfException(PdfException.ElementCannotFitAnyArea);
                            }
                            renderer = result.getOverflowRenderer();
                            if (storedArea != null) {
                                nextStoredArea = currentArea;
                                currentArea = storedArea;
                                currentPageNumber = storedArea.getPageNumber();
                            }
                            storedArea = currentArea;
                            continue;
                        }
                        storedArea = currentArea;
                        if (result.getNewPageSize() != null)
                            getNextArea(result.getNewPageSize());
                        else {
                            getNextArea();
                        }
                    }
                }
                renderer = result.getOverflowRenderer();
            }
            currentArea.getBBox().setHeight(currentArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());
            currentArea.setEmptyArea(false);
            if (renderer != null) {
                processRenderer(renderer, resultRenderers);
            }

            childRenderers.remove(childRenderers.size() - 1);
            if (!immediateFlush) {
                childRenderers.addAll(resultRenderers);
            }
        } else if (positionedRenderers.size() > 0 && positionedRenderers.get(positionedRenderers.size() - 1) == renderer) {
            Integer positionedPageNumber = renderer.getProperty(Property.PAGE_NUMBER);
            if (positionedPageNumber == null)
                positionedPageNumber = currentPageNumber;
            renderer.layout(new LayoutContext(new LayoutArea(positionedPageNumber, currentArea.getBBox().clone())));

            if (immediateFlush) {
                flushSingleRenderer(renderer);
                positionedRenderers.remove(positionedRenderers.size() - 1);
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
        childRenderers.clear();
        positionedRenderers.clear();
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        throw new RuntimeException();
    }

    public LayoutArea getNextArea() {
        moveToNextPage();
        PageSize lastPageSize = ensureDocumentHasNPages(currentPageNumber);
        if (lastPageSize == null) {
            PageSize defaultPageSize = document.getPdfDocument().getDefaultPageSize();
            lastPageSize = new PageSize(document.getPdfDocument().getPage(currentPageNumber).getPageSize()).
                    setMargins(defaultPageSize.getTopMargin(), defaultPageSize.getRightMargin(), defaultPageSize.getBottomMargin(), defaultPageSize.getLeftMargin());
        }
        return (currentArea = new LayoutArea(currentPageNumber, lastPageSize.getEffectiveArea()));
    }

    public LayoutArea getNextArea(PageSize pageSize) {
        moveToNextPage();
        document.getPdfDocument().addNewPage(pageSize);
        return (currentArea = new LayoutArea(document.getPdfDocument().getNumOfPages(), pageSize.getEffectiveArea()));
    }

    @Override
    public LayoutArea getOccupiedArea() {
        throw new IllegalStateException("Not applicable for DocumentRenderer");
    }

    protected void flushSingleRenderer(IRenderer resultRenderer) {
        if (!resultRenderer.isFlushed()) {
            ensureDocumentHasNPages(resultRenderer.getOccupiedArea().getPageNumber());
            PdfPage correspondingPage = document.getPdfDocument().getPage(resultRenderer.getOccupiedArea().getPageNumber());
            resultRenderer.draw(document.getPdfDocument(), new PdfCanvas(correspondingPage));
        }
    }

    protected PageSize addNewPage() {
        document.getPdfDocument().addNewPage();
        return document.getPdfDocument().getDefaultPageSize();
    }

    /**
     * Adds some pages so that the overall number is at least n.
     * Returns the page size of the n'th page.
     */
    private PageSize ensureDocumentHasNPages(int n) {
        PageSize lastPageSize = null;
        while (document.getPdfDocument().getNumOfPages() < n) {
            lastPageSize = addNewPage();
        }
        return lastPageSize;
    }

    private void processRenderer(IRenderer renderer, List<IRenderer> resultRenderers) {
        alignChildHorizontally(renderer, currentArea.getBBox().getWidth());
        if (immediateFlush) {
            flushSingleRenderer(renderer);
        } else {
            resultRenderers.add(renderer);
        }
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
