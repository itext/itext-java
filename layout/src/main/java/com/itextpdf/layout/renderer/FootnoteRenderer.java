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

import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.tagging.FootnoteTaggingHelper;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

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

    @Override
    public void draw(DrawContext drawContext) {
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        FootnoteTaggingHelper.repairFootnoteTagIfNeeded(this, taggingHelper);
        if (!childRenderers.isEmpty() && !childRenderers.get(0).getChildRenderers().isEmpty()) {
            IRenderer footnoteParagraphContainer = childRenderers.get(0);
            IRenderer footnoteAnchorContent = footnoteParagraphContainer.getChildRenderers().get(0);

            if (taggingHelper != null && taggingHelper.isArtifact(this)) {
                // We remove these properties in case tagging is enabled, but tag is marked as artifact.
                // We need to do that in order to not create link annotation and destinations,
                // because annotations need to be tagged. But since this content is artifact, we can't properly tag it.
                footnoteAnchorContent.setProperty(Property.LINK_ANNOTATION, null);
                footnoteAnchorContent.setProperty(Property.DESTINATION, null);
            }
            FootnoteTaggingHelper.wrapAnchorInsideFootnoteIntoLbl(footnoteAnchorContent, taggingHelper);
        }

        super.draw(drawContext);
    }
}