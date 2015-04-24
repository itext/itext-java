package com.itextpdf.model.element;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.PageBreakRenderer;

public class AreaBreak extends AbstractElement {

    protected PageSize pageSize;

    public AreaBreak() {
    }

    public AreaBreak(PageSize pageSize) {
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
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new PageBreakRenderer(this);
    }
}
