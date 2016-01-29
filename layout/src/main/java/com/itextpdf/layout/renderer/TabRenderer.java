package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.draw.Drawable;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;

public class TabRenderer extends AbstractRenderer {

    public TabRenderer(Tab tab) {
        super(tab);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Float width = retrieveWidth(area.getBBox().getWidth());
        Float height = getPropertyAsFloat(Property.HEIGHT);
        occupiedArea = new LayoutArea(area.getPageNumber(),
                new Rectangle(area.getBBox().getX(), area.getBBox().getY() + area.getBBox().getHeight(), width, height));

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(DrawContext drawContext) {
        Drawable leader = getProperty(Property.TAB_LEADER);
        if (leader == null)
            return;

        boolean isTagged = drawContext.isTaggingEnabled();
        if (isTagged) {
            drawContext.getCanvas().openTag(new CanvasArtifact());
        }

        leader.draw(drawContext.getCanvas(), occupiedArea.getBBox());

        if (isTagged) {
            drawContext.getCanvas().closeTag();
        }
    }

    @Override
    public TabRenderer getNextRenderer() {
        return new TabRenderer((Tab) modelElement);
    }
}
