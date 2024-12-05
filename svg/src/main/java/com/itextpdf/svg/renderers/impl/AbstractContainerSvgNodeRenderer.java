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
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;

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
     * @return the viewport that applies to this renderer
     */
    Rectangle calculateViewPort(SvgDrawContext context) {
        //TODO: DEVSIX-8775 the logic below should be refactored, first of all it shouldn't be applied to root svg tag
        // (though depending on implementation maybe it won't be a problem), also it need to be adjusted to support em/rem
        // which seems possible for all cases, and as for percents, I'm not sure it's possible for nested svg tags, but
        // it should be possible for symbols
        Rectangle currentViewPort = context.getCurrentViewPort();

        // Set default values to parent viewport in the case of a nested svg tag
        float portX = 0;
        float portY = 0;
        // Default should be parent portWidth if not outermost
        float portWidth = currentViewPort.getWidth();
        // Default should be parent height if not outermost
        float portHeight = currentViewPort.getHeight();

        if (attributesAndStyles != null) {
            if (attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
                portX = CssDimensionParsingUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.X));
            }
            if (attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
                portY = CssDimensionParsingUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.Y));
            }
            if (attributesAndStyles.containsKey(SvgConstants.Attributes.WIDTH)) {
                portWidth = CssDimensionParsingUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.WIDTH));
            }
            if (attributesAndStyles.containsKey(SvgConstants.Attributes.HEIGHT)) {
                portHeight = CssDimensionParsingUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.HEIGHT));
            }
        }

        return new Rectangle(portX, portY, portWidth, portHeight);
    }
}
