/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.layout.element.Footnote;

/**
 * Renderer for {@link Footnote} representing a footnote placed at the bottom of the page.
 */
public class FootnoteRenderer extends BlockRenderer {

    /**
     * Creates a {@link FootnoteRenderer} from its corresponding layout object.
     *
     * @param modelElement the {@link Footnote} which this object should manage
     */
    public FootnoteRenderer(Footnote modelElement) {
        super(modelElement);
    }

    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(FootnoteRenderer.class, this.getClass());
        return new FootnoteRenderer((Footnote) modelElement);
    }
}
