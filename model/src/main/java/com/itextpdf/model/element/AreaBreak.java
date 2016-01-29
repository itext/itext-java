package com.itextpdf.model.element;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.model.renderer.AreaBreakRenderer;

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
