package com.itextpdf.model.layout;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.model.renderer.IRenderer;

public class LayoutResult {

    public static final int FULL = 1;
    public static final int PARTIAL = 2;
    public static final int NOTHING = 3;

    protected int status;
    protected LayoutArea occupiedArea;
    protected IRenderer splitRenderer;
    protected IRenderer overflowRenderer;
    protected PageSize newPageSize;

    public LayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        this.status = status;
        this.occupiedArea = occupiedArea;
        this.splitRenderer = splitRenderer;
        this.overflowRenderer = overflowRenderer;
    }

    public int getStatus() {
        return status;
    }

    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    public IRenderer getSplitRenderer() {
        return splitRenderer;
    }

    public IRenderer getOverflowRenderer() {
        return overflowRenderer;
    }

    public PageSize getNewPageSize() {
        return newPageSize;
    }

    public LayoutResult setNewPageSize(PageSize pageSize) {
        this.newPageSize = pageSize;
        return this;
    }
}
