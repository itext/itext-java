package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.draw.Drawable;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Tab;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

public class TabRenderer extends AbstractRenderer {

    public TabRenderer(Tab tab) {
        super(tab);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Float width = getPropertyAsFloat(Property.WIDTH);
        Float height = getPropertyAsFloat(Property.HEIGHT);
        occupiedArea = new LayoutArea(area.getPageNumber(),
                new Rectangle(area.getBBox().getX(), area.getBBox().getY() + area.getBBox().getHeight(), width, height));

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        Drawable leader = getProperty(Property.TAB_LEADER);
        if (leader == null)
            return;

        leader.draw(canvas, occupiedArea.getBBox());
    }
}
