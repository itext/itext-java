/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;svg&gt; tag.
 */
public class SvgTagSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        context.addViewPort(this.calculateViewPort(context));
        super.doDraw(context);
    }
    @Override
    public boolean canConstructViewPort(){ return true;}
    /**
     * Calculate the viewport based on the context.
     *
     * @param context the SVG draw context
     * @return the viewport that applies to this renderer
     */
    Rectangle calculateViewPort(SvgDrawContext context) {
        Rectangle currentViewPort = context.getCurrentViewPort();

        float portX = 0f;
        float portY = 0f;
        float portWidth = 0f;
        float portHeight = 0f;


        // set default values to parent viewport in the case of a nested svg tag
        portX = currentViewPort.getX();
        portY = currentViewPort.getY();
        portWidth = currentViewPort.getWidth(); // default should be parent portWidth if not outermost
        portHeight = currentViewPort.getHeight(); // default should be parent height if not outermost


        if (attributesAndStyles != null) {

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
                portX = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.X));
            }

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
                portY = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.Y));
            }

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.WIDTH)) {
                portWidth = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.WIDTH));
            }

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.HEIGHT)) {
                portHeight = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.HEIGHT));
            }
        }

        return new Rectangle(portX, portY, portWidth, portHeight);
    }

    @Override
    protected boolean canElementFill() {
        return false;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        SvgTagSvgNodeRenderer copy = new SvgTagSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }
}
