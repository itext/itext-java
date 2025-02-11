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
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.commons.utils.MessageFormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LinkRenderer extends TextRenderer {

    /**
     * Creates a LinkRenderer from its corresponding layout object.
     * @param link the {@link com.itextpdf.layout.element.Link} which this object should manage
     */
    public LinkRenderer(Link link) {
        this (link, link.getText());
    }

    /**
     * Creates a LinkRenderer from its corresponding layout object, with a custom
     * text to replace the contents of the {@link com.itextpdf.layout.element.Link}.
     *
     * @param linkElement the {@link com.itextpdf.layout.element.Link} which this object should manage
     * @param text the replacement text
     */
    public LinkRenderer(Link linkElement, String text) {
        super(linkElement, text);
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(LinkRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Drawing won't be performed."));
            return;
        }
        super.draw(drawContext);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }


    }

    /**
     * Gets a new instance of this class to be used as a next renderer, after this renderer is used, if
     * {@link #layout(LayoutContext)} is called more than once.
     *
     * <p>
     * If a renderer overflows to the next area, iText uses this method to create a renderer
     * for the overflow part. So if one wants to extend {@link LinkRenderer}, one should override
     * this method: otherwise the default method will be used and thus the default rather than the custom
     * renderer will be created.
     * @return new renderer instance
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(LinkRenderer.class, this.getClass());
        return new LinkRenderer((Link) modelElement);
    }
}
