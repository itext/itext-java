package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.canvas.draw.LineDrawer;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;

public class LineSeparatorRenderer extends BlockRenderer {

    public LineSeparatorRenderer(LineSeparator lineSeparator) {
        super(lineSeparator);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LineDrawer lineDrawer = getProperty(Property.LINE_DRAWER);
        float height = lineDrawer != null ? lineDrawer.getLineWidth() : 0;
        occupiedArea = layoutContext.getArea().clone();
        applyMargins(occupiedArea.getBBox(), false);
        if (occupiedArea.getBBox().getHeight() < height) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this);
        }
        occupiedArea.getBBox().moveUp(occupiedArea.getBBox().getHeight() - height).setHeight(height);
        applyMargins(occupiedArea.getBBox(), true);
        return new LayoutResult(LayoutResult.FULL, occupiedArea, this, null);
    }

    @Override
    public BlockRenderer getNextRenderer() {
        return new LineSeparatorRenderer((LineSeparator) modelElement);
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);
        LineDrawer lineDrawer = getProperty(Property.LINE_DRAWER);
        if (lineDrawer != null) {
            lineDrawer.draw(drawContext.getCanvas(), occupiedArea.getBBox());
        }
    }
}
