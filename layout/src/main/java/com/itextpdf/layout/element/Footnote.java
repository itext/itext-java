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
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.renderer.FootnoteRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;

/**
 * Class representing a single footnote which is a note placed at the bottom of the page
 * indicated in the text with superscript numbers (or letters or other symbols).
 */
public class Footnote extends AbstractElement<Footnote> implements IAccessibleElement {

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates new {@link Footnote} instance with text.
     *
     * @param text the textual contents of the footnote
     */
    public Footnote(String text) {
        this(new Paragraph(text).setMarginTop(0).setMarginBottom(0));
    }

    /**
     * Creates new {@link Footnote} instance.
     *
     * @param paragraph {@link Paragraph} representing the contents of the footnote
     */
    public Footnote(Paragraph paragraph) {
        super();
        childElements.add(paragraph);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            // Although we mark is as P here, it'll be an artifact due to PageMarginBoxes#setPageMarginTagRole method.
            // TODO DEVSIX-9997 Support correct footnotes tagging
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.P);
        }
        return tagProperties;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new FootnoteRenderer(this);
    }
}
