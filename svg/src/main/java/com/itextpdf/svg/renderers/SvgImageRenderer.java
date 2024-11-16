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
package com.itextpdf.svg.renderers;

import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.ImageRenderer;
import com.itextpdf.svg.element.SvgImage;

/**
 * Represents a renderer for the {@link SvgImage} layout element.
 */
public class SvgImageRenderer extends ImageRenderer {
    /**
     * Creates an SvgImageRenderer from its corresponding layout object.
     *
     * @param image the {@link SvgImage} which this object should manage
     */
    public SvgImageRenderer(SvgImage image) {
        super(image);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        ((SvgImage) modelElement).generate(drawContext.getDocument());
        super.draw(drawContext);
    }
    //TODO: DEVSIX-8775 probably we need to override ImageRenderer#calculateImageDimensions and calcualte percent
    // values of svg width and height there by altering layoutBox or affine transform, this is needed to correctly
    // calculate percent values in root svg element. Though this won't help with svg's loaded as background image in
    // html2pdf, so this has to be carefully investigated
}
