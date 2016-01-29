package com.itextpdf.layout.element;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.renderer.AreaBreakRenderer;

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
    protected AreaBreakRenderer makeNewRenderer() {
        return new AreaBreakRenderer(this);
    }
}
