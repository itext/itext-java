/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;

import java.util.List;

/**
 * Abstract {@link BlockRenderer} for a single line of text content in a form field.
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

    @Override
    public void move(float dxRight, float dyUp) {
        super.move(dxRight, dyUp);
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
    private void setContentHeight(IRenderer flatRenderer, float height) {
        Rectangle bBox = flatRenderer.getOccupiedArea().getBBox();
        float dy = (height - bBox.getHeight()) / 2;
        bBox.moveDown(dy);
        bBox.setHeight(height);
        flatRenderer.move(0, -dy);
    }
}
