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
package com.itextpdf.layout.properties.margins;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.FootnoteRenderer;
import com.itextpdf.layout.tagging.TaggingHintKey;

/**
 * Utility class to process footnotes for internal usage only.
 */
public final class FootnotesUtil {

    private FootnotesUtil() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Adds provided footnotes to the specified page via {@link PageMarginBoxes}.
     *
     * @param pageNum page number
     * @param footnotesToAdd list of {@link Footnote} instance to add
     * @param pageMarginBoxes {@link PageMarginBoxes} for the page
     * @param footnotesProperties {@link FootnotesProperties} to apply for footnotes
     */
    public static void addFootnotesToPage(int pageNum, Iterable<FootnoteRenderer> footnotesToAdd,
            PageMarginBoxes pageMarginBoxes, FootnotesProperties footnotesProperties) {
        FootnotesContainer footnotesContainer = new FootnotesContainer(pageNum);
        if (footnotesProperties.getFootnotesContainerStyle() != null) {
            footnotesContainer.addStyle(footnotesProperties.getFootnotesContainerStyle());
        }

        for (FootnoteRenderer footnoteRederer : footnotesToAdd) {
            Footnote footnote = (Footnote) footnoteRederer.getModelElement();
            footnotesContainer.add(footnote, footnoteRederer.<TaggingHintKey>getProperty(Property.TAGGING_HINT_KEY));
            if (footnote.footnoteAnchor != null) {
                footnote.anchors.put(pageNum, footnote.footnoteAnchor);
                footnote.resetFootnoteAnchor();
            }
        }

        PageFootnotesContent pageFootnotesContent = new PageFootnotesContent(footnotesContainer).setPageNumber(pageNum);
        pageMarginBoxes.addFootnotes(pageFootnotesContent);
    }

    /**
     * Sets parent for footnote renderer in order for it to be layouted with correct properties and styles applied.
     *
     * @param footnoteRenderer {@link FootnoteRenderer} to set parent for
     * @param documentRenderer {@link DocumentRenderer} root renderer, the parent of footnotes container renderer
     */
    public static void setParentForFootnoteRenderer(FootnoteRenderer footnoteRenderer,
                                                    DocumentRenderer documentRenderer) {
        FootnotesProperties footnotesProperties =
                ((Document) documentRenderer.getModelElement()).getFootnotesProperties();
        FootnotesContainer footnotesContainer = new FootnotesContainer(-1);
        if (footnotesProperties != null && footnotesProperties.getFootnotesContainerStyle() != null) {
            footnotesContainer.addStyle(footnotesProperties.getFootnotesContainerStyle());
        }
        FootnotesContainerRenderer footnotesContainerRenderer = new FootnotesContainerRenderer(footnotesContainer);
        footnoteRenderer.setParent(footnotesContainerRenderer.setParent(documentRenderer));
    }

    /**
     * Applies {@link Style} storing style properties for footnote anchor that is placed inside the footnote.
     *
     * @param anchor {@link FootnoteAnchor} to apply style for
     * @param footnoteAnchorLabelStyle {@link Style} storing properties for footnote anchor inside the footnote
     */
    public static void applyFootnoteAnchorStyle(FootnoteAnchor anchor, Style footnoteAnchorLabelStyle) {
        anchor.setFootnoteAnchorLabelStyle(footnoteAnchorLabelStyle);
    }
}
