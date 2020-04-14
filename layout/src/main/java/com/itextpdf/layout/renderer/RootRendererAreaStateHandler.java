package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.RootLayoutArea;

import java.util.ArrayList;
import java.util.List;

class RootRendererAreaStateHandler {
    private RootLayoutArea storedPreviousArea;
    private RootLayoutArea storedNextArea;

    private List<Rectangle> storedPreviousFloatRenderAreas = null;
    private List<Rectangle> storedNextFloatRenderAreas = null;

    public boolean attemptGoBackToStoredPreviousStateAndStoreNextState(RootRenderer rootRenderer) {
        boolean result = false;
        if (storedPreviousArea != null) {
            storedNextArea = rootRenderer.currentArea;

            rootRenderer.currentArea = storedPreviousArea;
            rootRenderer.currentPageNumber = storedPreviousArea.getPageNumber();

            storedNextFloatRenderAreas = new ArrayList<>(rootRenderer.floatRendererAreas);
            rootRenderer.floatRendererAreas = storedPreviousFloatRenderAreas;

            storedPreviousFloatRenderAreas = null;
            storedPreviousArea = null;

            result = true;
        }
        return result;
    }

    public boolean attemptGoForwardToStoredNextState(RootRenderer rootRenderer) {
        if (storedNextArea != null) {
            rootRenderer.currentArea = storedNextArea;
            rootRenderer.currentPageNumber = storedNextArea.getPageNumber();
            rootRenderer.floatRendererAreas = storedNextFloatRenderAreas;

            storedNextArea = null;
            storedNextFloatRenderAreas = null;
            return true;
        } else {
            return false;
        }
    }

    public RootRendererAreaStateHandler storePreviousState(RootRenderer rootRenderer) {
        storedPreviousArea = rootRenderer.currentArea;
        storedPreviousFloatRenderAreas = new ArrayList<>(rootRenderer.floatRendererAreas);
        return this;
    }
}
