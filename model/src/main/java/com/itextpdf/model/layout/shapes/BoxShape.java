package com.itextpdf.model.layout.shapes;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.geom.Rectangle;

public class BoxShape extends Rectangle implements ILayoutShape {

    public BoxShape(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public BoxShape(PageSize pageSize) {
        super(pageSize.getX() + pageSize.getLeftMargin(),
                pageSize.getY() + pageSize.getBottomMargin(),
                pageSize.getWidth() - pageSize.getX() - pageSize.getRightMargin(),
                pageSize.getHeight() - pageSize.getY() - pageSize.getTopMargin());
    }

}
