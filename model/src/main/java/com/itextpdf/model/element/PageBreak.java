package com.itextpdf.model.element;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.PageBreakRenderer;

public class PageBreak extends AbstractElement {

    protected PageSize pageSize;

    public PageBreak() {
    }

    public PageBreak(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public void setPageSize(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public IRenderer makeRenderer() {
        if (renderer == null)
            renderer = new PageBreakRenderer(this);
        return renderer;
    }
}
