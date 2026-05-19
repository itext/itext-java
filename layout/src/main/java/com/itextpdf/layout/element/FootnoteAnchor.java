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

import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.renderer.FootnoteAnchorRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;

/**
 * Class representing an anchor for a footnote which is placed at the bottom of the page.
 * Footnote anchor indicates footnote in the text with superscript numbers (or letters or other symbols).
 */
public class FootnoteAnchor extends AbstractElement<FootnoteAnchor> implements IAccessibleElement {
    private static final int DEFAULT_FONT_SIZE = 6;
    private static final int DEFAULT_TEXT_RISE = 7;

    private final IElement footnoteAnchor;
    private final Footnote footnote;

    /**
     * Creates new {@link FootnoteAnchor} instance.
     *
     * @param text superscript text for anchor to indicate a footnote
     * @param footnote {@link Footnote} linked to this anchor
     */
    public FootnoteAnchor(String text, Footnote footnote) {
        this(new Text(text).setFontSize(DEFAULT_FONT_SIZE).setTextRise(DEFAULT_TEXT_RISE), footnote);
    }

    /**
     * Creates new {@link FootnoteAnchor} instance.
     *
     * @param text {@link Text} for anchor to indicate a footnote
     * @param footnote {@link Footnote} linked to this anchor
     */
    public FootnoteAnchor(Text text, Footnote footnote) {
        this.footnoteAnchor = text;
        this.footnote = footnote;
    }

    /**
     * Creates new {@link FootnoteAnchor} instance.
     *
     * @param image {@link Image} to use as footnote anchor
     * @param footnote {@link Footnote} linked to this anchor
     */
    public FootnoteAnchor(Image image, Footnote footnote) {
        this.footnoteAnchor = image;
        this.footnote = footnote;
    }

    /**
     * Gets layout element {@link IElement} representing footnote anchor.
     *
     * @return {@link IElement} representing footnote anchor (can be {@link Text} or {@link Image})
     */
    public IElement getFootnoteAnchor() {
        return footnoteAnchor;
    }

    /**
     * Gets {@link Footnote} linked to this anchor.
     *
     * @return {@link Footnote} linked to this anchor
     */
    public Footnote getFootnote() {
        return footnote;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        // TODO DEVSIX-9997 Support correct footnotes tagging
        return footnoteAnchor instanceof IAccessibleElement ?
                ((IAccessibleElement) footnoteAnchor).getAccessibilityProperties() : null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new FootnoteAnchorRenderer(this);
    }
}
