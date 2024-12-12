/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;

public abstract class AbstractContainerSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {
    @Override
    public boolean canConstructViewPort(){ return true;}

    @Override
    protected boolean canElementFill() {
        return false;
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        context.addViewPort(this.calculateViewPort(context));
        super.doDraw(context);
    }

    /**
     * Calculate the viewport based on the context.
     *
     * @param context the SVG draw context
     *
     * @return the viewport that applies to this renderer
     */
    Rectangle calculateViewPort(SvgDrawContext context) {
        Rectangle percentBaseBox;
        if (getParent() instanceof PdfRootSvgNodeRenderer || !(getParent() instanceof AbstractSvgNodeRenderer)) {
            // If the current container is a top level SVG, make a copy of the current viewport.
            // It is needed to avoid double percent resolving. For absolute sized viewport we
            // will get the same viewport, so save resources and just make a copy.
            return context.getCurrentViewPort().clone();
        } else {
            // If the current container is nested container, take a view box as a percent base
            percentBaseBox = ((AbstractSvgNodeRenderer) getParent()).getCurrentViewBox(context);
        }

        float portX = 0;
        float portY = 0;
        float portWidth = percentBaseBox.getWidth();
        float portHeight = percentBaseBox.getHeight();

        if (attributesAndStyles != null) {
            portX = SvgCssUtils.parseAbsoluteLength(this, attributesAndStyles.get(Attributes.X),
                    percentBaseBox.getWidth(), 0, context);
            portY = SvgCssUtils.parseAbsoluteLength(this, attributesAndStyles.get(Attributes.Y),
                    percentBaseBox.getHeight(), 0, context);

            String widthStr = attributesAndStyles.get(Attributes.WIDTH);
            // In case widthStr==null, according to SVG spec default value is 100%, it is why default
            // value is percentBaseBox.getWidth(). See SvgConstants.Values.DEFAULT_WIDTH_AND_HEIGHT_VALUE
            portWidth = SvgCssUtils.parseAbsoluteLength(this, widthStr, percentBaseBox.getWidth(),
                    percentBaseBox.getWidth(), context);

            String heightStr = attributesAndStyles.get(Attributes.HEIGHT);
            // In case heightStr==null, according to SVG spec default value is 100%, it is why default
            // value is percentBaseBox.getHeight(). See SvgConstants.Values.DEFAULT_WIDTH_AND_HEIGHT_VALUE
            portHeight = SvgCssUtils.parseAbsoluteLength(this, heightStr, percentBaseBox.getHeight(),
                    percentBaseBox.getHeight(), context);
        }

        return new Rectangle(portX, portY, portWidth, portHeight);
    }
}
