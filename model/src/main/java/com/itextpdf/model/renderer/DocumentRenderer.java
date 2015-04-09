package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.Document;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.List;

public class DocumentRenderer extends AbstractRenderer {

    protected Document document;
    protected boolean immediateFlush = true;
    protected LayoutArea currentArea = null;

    public DocumentRenderer(Document document) {
        this(document, true);
    }

    public DocumentRenderer(Document document, boolean immediateFlush) {
        this.document = document;
        this.immediateFlush = immediateFlush;
    }

    @Override
    public void addChild(IRenderer renderer) {
        super.addChild(renderer);
        List<IRenderer> resultRenderers = new ArrayList<IRenderer>();
        LayoutResult result = null;
        if (currentArea == null)
            currentArea = getNextArea();
        while (renderer != null && (result = renderer.layout(new LayoutContext(currentArea))).getStatus() != LayoutResult.FULL) {
            if (result.getStatus() == LayoutResult.PARTIAL) {
                resultRenderers.add(renderer);
            } else if (result.getStatus() == LayoutResult.NOTHING) {
                if (result.getNewPageSize() != null)
                    getNextPageArea(result.getNewPageSize());
                else
                    getNextArea();
            }

            renderer = result.getOverflowRenderer();
        }
        currentArea.getBBox().setHeight(currentArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());
        if (renderer != null)
            resultRenderers.add(renderer);

        if (immediateFlush) {
            for (IRenderer resultRenderer: resultRenderers) {
                try {
                    PdfPage correspondingPage = document.getPdfDocument().getPage(resultRenderer.getOccupiedArea().getPageNumber());
                    resultRenderer.draw(new PdfCanvas(correspondingPage));
                } catch (PdfException exc) {
                    throw new RuntimeException(exc);
                }
            }
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        throw new RuntimeException();
    }

    @Override
    public IRenderer split() {
        throw new RuntimeException();
    }

    @Override
    public LayoutArea getNextArea() {
        try {
            PdfPage newPage = document.getPdfDocument().addNewPage();
            return (currentArea = new LayoutArea(document.getPdfDocument().getNumOfPages(), newPage.getMediaBox()));
        } catch (PdfException exc) {
            throw new RuntimeException();
        }
    }

    public LayoutArea getNextPageArea(PageSize pageSize) {
        try {
            PdfPage newPage = document.getPdfDocument().addNewPage(pageSize);
            return (currentArea = new LayoutArea(document.getPdfDocument().getNumOfPages(), newPage.getMediaBox()));
        } catch (PdfException exc) {
            throw new RuntimeException();
        }
    }

    @Override
    public LayoutArea getOccupiedArea() {
        throw new RuntimeException();
    }

}
