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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;

import java.util.List;

/**
 * Abstract {@link AbstractTextFieldRenderer} for a single line of text content in a form field.
 */
public abstract class AbstractOneLineTextFieldRenderer extends AbstractTextFieldRenderer {

    /**
     * Creates a new {@link AbstractOneLineTextFieldRenderer} instance.
     *
     * @param modelElement the model element
     */
    AbstractOneLineTextFieldRenderer(IFormField modelElement) {
        super(modelElement);
    }

    /**
     * Crops the content lines.
     *
     * @param lines a list of lines
     * @param bBox  the bounding box
     */
    void cropContentLines(List<LineRenderer> lines, Rectangle bBox) {
        adjustNumberOfContentLines(lines, bBox, 1);
        updateParagraphHeight();
    }

    /**
     * Updates the paragraph height.
     */
    private void updateParagraphHeight() {
        Float height = retrieveHeight();
        Float minHeight = retrieveMinHeight();
        Float maxHeight = retrieveMaxHeight();
        float originalHeight = flatRenderer.getOccupiedArea().getBBox().getHeight();
        if (height != null && (float) height > 0) {
            setContentHeight(flatRenderer, (float) height);
        } else if (minHeight != null && (float) minHeight > originalHeight) {
            setContentHeight(flatRenderer, (float) minHeight);
        } else if (maxHeight != null && (float) maxHeight > 0 && (float) maxHeight < originalHeight) {
            setContentHeight(flatRenderer, (float) maxHeight);
        }
    }

    /**
     * Sets the content height.
     *
     * @param flatRenderer  the flat renderer
     * @param height        the height
     */
    void setContentHeight(IRenderer flatRenderer, float height) {
        Rectangle bBox = flatRenderer.getOccupiedArea().getBBox();
        float dy = (height - bBox.getHeight()) / 2;
        bBox.moveDown(dy);
        bBox.setHeight(height);
        flatRenderer.move(0, -dy);
    }
}
