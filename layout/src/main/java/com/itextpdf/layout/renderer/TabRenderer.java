/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.commons.utils.MessageFormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabRenderer extends AbstractRenderer {
    /**
     * Creates a TabRenderer from its corresponding layout object
     *
     * @param tab the {@link Tab} which this object should manage
     */
    public TabRenderer(Tab tab) {
        super(tab);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Float width = retrieveWidth(area.getBBox().getWidth());
        UnitValue height = this.<UnitValue>getProperty(Property.MIN_HEIGHT);
        occupiedArea = new LayoutArea(area.getPageNumber(),
                new Rectangle(area.getBBox().getX(), area.getBBox().getY() + area.getBBox().getHeight(),(float)  width, (float) height.getValue()));

        TargetCounterHandler.addPageByID(this);

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(TabRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Drawing won't be performed."));
            return;
        }
        ILineDrawer leader = this.<ILineDrawer>getProperty(Property.TAB_LEADER);
        if (leader == null)
            return;

        boolean isTagged = drawContext.isTaggingEnabled();
        if (isTagged) {
            drawContext.getCanvas().openTag(new CanvasArtifact());
        }

        beginElementOpacityApplying(drawContext);
        leader.draw(drawContext.getCanvas(), occupiedArea.getBBox());
        endElementOpacityApplying(drawContext);

        if (isTagged) {
            drawContext.getCanvas().closeTag();
        }
    }

    /**
     * Gets a new instance of this class to be used as a next renderer, after this renderer is used, if
     * {@link #layout(LayoutContext)} is called more than once.
     *
     * <p>
     * If a renderer overflows to the next area, iText uses this method to create a renderer
     * for the overflow part. So if one wants to extend {@link TabRenderer}, one should override
     * this method: otherwise the default method will be used and thus the default rather than the custom
     * renderer will be created.
     * @return new renderer instance
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(TabRenderer.class, this.getClass());
        return new TabRenderer((Tab) modelElement);
    }
}
