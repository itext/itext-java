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
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.FootnoteRenderer;
import com.itextpdf.layout.tagging.TaggingHintKey;

/**
 * Utility class to process footnotes for internal usage only.
 */
public final class FootnotesUtil {

    private static final float DEFAULT_FOOTNOTE_ANCHOR_FONT_SIZE_SCALE_FACTOR = 0.5F;
    private static final float DEFAULT_FOOTNOTE_ANCHOR_TEXT_RISE_SCALE_FACTOR = 0.6F;

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
            if (footnote.getInjectedFootnoteAnchor() != null) {
                footnote.anchors.put(pageNum, footnote.getInjectedFootnoteAnchor());
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

    /**
     * Gets injected footnote anchor element, which is a copy of a footnote anchor in the main content.
     *
     * @param footnote {@link Footnote} from which injected footnote anchor is retrieved
     *
     * @return injected footnote anchor element
     */
    public static IElement getInjectedFootnoteAnchor(Footnote footnote) {
        return footnote.getInjectedFootnoteAnchor();
    }

    /**
     * Indicates whether a default style should be applied to injected footnote anchor copy.
     *
     * @param footnote {@link Footnote} containing injected anchor copy
     *
     * @return {@code true} if default style is needed, {@code false} otherwise
     */
    public static boolean isDefaultStyleNeededForInjectedFootnoteAnchor(Footnote footnote) {
        return footnote.isDefaultStyleNeededForInjectedFootnoteAnchor();
    }

    /**
     * Indicates whether a default style should be applied to the footnote anchor.
     *
     * @param anchor {@link FootnoteAnchor} to check
     *
     * @return {@code true} if default style is needed, {@code false} otherwise
     */
    public static boolean isDefaultStyleNeeded(FootnoteAnchor anchor) {
        return anchor.isDefaultStyleNeeded();
    }

    /**
     * Creates the default style for a footnote anchor in the main content.
     * <p>
     * The resulting style uses a reduced font size and positive text rise relative to the parent font size:
     * <p>
     * font size = parent font size * 0.5
     * <p>
     * text rise = parent font size * 0.6
     * <p>
     * If {@code parentFontSize} is {@code null} {@code 12pt} is used as the base size.
     *
     * @param parentFontSize parent font size unit value
     *
     * @return default style for a footnote anchor
     */
    public static Style createDefaultFootnoteAnchorStyle(UnitValue parentFontSize) {
        float fontSize;
        if (parentFontSize == null) {
            fontSize = 12;
        } else {
            fontSize = parentFontSize.getValue();
        }

        Style defaultStyle = new Style();

        final float defaultFontSize = fontSize * DEFAULT_FOOTNOTE_ANCHOR_FONT_SIZE_SCALE_FACTOR;
        defaultStyle.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(defaultFontSize));

        final float defaultTextRise = fontSize * DEFAULT_FOOTNOTE_ANCHOR_TEXT_RISE_SCALE_FACTOR;
        defaultStyle.setProperty(Property.TEXT_RISE, defaultTextRise);

        return defaultStyle;
    }
}
