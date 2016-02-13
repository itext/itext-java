package com.itextpdf.layout.layout;

import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.renderer.IRenderer;

public class LayoutResult {

    public static final int FULL = 1;
    public static final int PARTIAL = 2;
    public static final int NOTHING = 3;

    protected int status;
    protected LayoutArea occupiedArea;
    protected IRenderer splitRenderer;
    protected IRenderer overflowRenderer;
    protected AreaBreak areaBreak;

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

    public void setSplitRenderer(IRenderer splitRenderer) {
        this.splitRenderer = splitRenderer;
    }

    public IRenderer getOverflowRenderer() {
        return overflowRenderer;
    }

    public void setOverflowRenderer(IRenderer overflowRenderer) {
        this.overflowRenderer = overflowRenderer;
    }

    public AreaBreak getAreaBreak() {
        return areaBreak;
    }

    public LayoutResult setAreaBreak(AreaBreak areaBreak) {
        this.areaBreak = areaBreak;
        return this;
    }
}
