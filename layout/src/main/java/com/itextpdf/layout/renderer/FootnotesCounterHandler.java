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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.numbering.EnglishAlphabetNumbering;
import com.itextpdf.kernel.numbering.GreekAlphabetNumbering;
import com.itextpdf.kernel.numbering.RomanNumbering;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnoteNumberingConfig;
import com.itextpdf.layout.properties.margins.FootnoteNumberingType;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.properties.margins.FootnotesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper handler class to collect and count footnotes placed on the page.
 */
class FootnotesCounterHandler {
    private static final int DEFAULT_FONT_SIZE = 6;
    private static final int DEFAULT_TEXT_RISE = 7;

    private final Map<FootnoteAnchor, FootnoteAnchorRenderer> renderers = new HashMap<>();

    private final Map<FootnoteRenderer, Float> footnotes = new LinkedHashMap<FootnoteRenderer, Float>();

    /**
     * Creates a new {@link FootnotesCounterHandler} instance.
     */
    public FootnotesCounterHandler() {
        // Empty constructor.
    }

    /**
     * Adds footnote anchor info after {@link FootnoteAnchor} layout.
     *
     * @param renderer renderer for {@link FootnoteAnchor} which stores layout information
     */
    static void addFootnoteAnchor(FootnoteAnchorRenderer renderer) {
        final FootnotesCounterHandler footnotesCounterHandler = getFootnotesCounterHandler(renderer);
        if (footnotesCounterHandler != null) {
            FootnoteAnchor footnoteAnchor = (FootnoteAnchor) renderer.modelElement;
            footnotesCounterHandler.renderers.put(footnoteAnchor, renderer);
        }
    }

    /**
     * Gets {@link FootnotesCounterHandler} used in root {@link DocumentRenderer}.
     *
     * @param renderer {@link IRenderer} any renderer in the current tree
     *
     * @return {@link FootnotesCounterHandler} used in root {@link DocumentRenderer}
     */
    static FootnotesCounterHandler getFootnotesCounterHandler(IRenderer renderer) {
        IRenderer rootRenderer = renderer;
        while (rootRenderer.getParent() != null) {
            rootRenderer = rootRenderer.getParent();
        }
        if (rootRenderer instanceof DocumentRenderer) {
            return ((DocumentRenderer) rootRenderer).footnotesCounterHandler;
        }
        return null;
    }

    /**
     * Resets current {@link FootnotesCounterHandler} before collecting placed footnotes.
     */
    void reset() {
        renderers.clear();
        footnotes.clear();
    }

    /**
     * Collects footnotes which anchors are placed in the current area
     * in order their anchors are placed on a page from top to bottom and left to right.
     *
     * @param currentArea {@link LayoutArea} area to collect placed footnote anchors
     *
     * @return linked map of {@link Footnote} and its height float value
     */
    Map<FootnoteRenderer, Float> collectFootnotes(LayoutArea currentArea) {
        footnotes.clear();
        List<FootnoteAnchor> anchors = new ArrayList<>(renderers.keySet());
        Collections.sort(anchors, new FootnoteAnchorComparator());

        for (FootnoteAnchor footnoteAnchor : anchors) {
            FootnoteAnchorRenderer renderer = renderers.get(footnoteAnchor);

            if (renderer.occupiedArea == null) {
                continue;
            }

            int expectedPageNumber = currentArea.getPageNumber();
            // Check whether footnote anchor is inside the currentArea (if the overlap is greater than 50 percent).
            boolean isAnchorInsideCurrentArea = currentArea.getBBox().overlaps(renderer.occupiedArea.getBBox(),
                    0.5F * Math.min(renderer.occupiedArea.getBBox().getWidth(),
                            renderer.occupiedArea.getBBox().getHeight()));

            if (expectedPageNumber == renderer.occupiedArea.getPageNumber() && isAnchorInsideCurrentArea) {
                footnotes.put(renderer.footnoteRenderer,
                        renderer.footnoteRenderer.getOccupiedArea().getBBox().getHeight());
            }
        }
        return footnotes;
    }

    /**
     * Updates footnote anchors using automatic numbering and styles configured via {@link FootnotesProperties}.
     *
     * @param footnotesProperties {@link FootnotesProperties} with optional {@link FootnoteNumberingType}
     * specifying type for numbering of the footnote anchors and optional styles for footnote anchors
     * @param latestFootnoteNum the number of the previous placed footnote based on {@link FootnoteNumberingConfig}
     */
    void updateFootnoteNumberingAndStyles(FootnotesProperties footnotesProperties, int latestFootnoteNum) {
        if (footnotesProperties == null) {
            return;
        }
        Style footnoteAnchorLabelStyle = footnotesProperties.getFootnoteAnchorLabelStyle();
        if (footnoteAnchorLabelStyle != null) {
            for (FootnoteAnchor anchor : renderers.keySet()) {
                FootnotesUtil.applyFootnoteAnchorStyle(anchor, footnoteAnchorLabelStyle);
            }
        }
        if (footnotesProperties.getFootnoteNumberingType() == null) {
            return;
        }
        FootnoteNumberingType footnoteNumberingType = footnotesProperties.getFootnoteNumberingType();
        List<FootnoteAnchor> anchors = new ArrayList<>(renderers.keySet());
        Collections.sort(anchors, new FootnoteAnchorComparator());

        int footnoteNum = latestFootnoteNum + 1;
        for (FootnoteAnchor anchor : anchors) {
            FootnoteAnchorRenderer renderer = renderers.get(anchor);
            IRenderer currentSymbolRenderer = makeFootnoteNumSymbolRenderer(footnoteNum, footnoteNumberingType);
            ++footnoteNum;
            renderer.addSymbolRenderer(currentSymbolRenderer);
        }

    }

    private static IRenderer makeFootnoteNumSymbolRenderer(int index, FootnoteNumberingType numberingType) {
        String numberText;
        switch (numberingType) {
            case DECIMAL:
                numberText = String.valueOf(index);
                break;
            case ROMAN_LOWER:
                numberText = RomanNumbering.toRomanLowerCase(index);
                break;
            case ROMAN_UPPER:
                numberText = RomanNumbering.toRomanUpperCase(index);
                break;
            case ENGLISH_LOWER:
                numberText = EnglishAlphabetNumbering.toLatinAlphabetNumberLowerCase(index);
                break;
            case ENGLISH_UPPER:
                numberText = EnglishAlphabetNumbering.toLatinAlphabetNumberUpperCase(index);
                break;
            case GREEK_LOWER:
                numberText = GreekAlphabetNumbering.toGreekAlphabetNumber(index, false, true);
                break;
            case GREEK_UPPER:
                numberText = GreekAlphabetNumbering.toGreekAlphabetNumber(index, true, true);
                break;
            default:
                throw new IllegalStateException();
        }
        Style defaultStyle = new Style();
        // TODO DEVSIX-10031 Do not specify constant font size by default,
        //  it should depend on parent paragraph font size.
        defaultStyle.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(DEFAULT_FONT_SIZE));
        defaultStyle.setProperty(Property.TEXT_RISE, DEFAULT_TEXT_RISE);
        Text textElement = new Text(numberText).addStyle(defaultStyle);
        return new TextRenderer(textElement);
    }

    private final class FootnoteAnchorComparator implements Comparator<FootnoteAnchor> {
        @Override
        public int compare(FootnoteAnchor o1, FootnoteAnchor o2) {
            Rectangle rectangle1 = renderers.get(o1).occupiedArea.getBBox();
            Rectangle rectangle2 = renderers.get(o2).occupiedArea.getBBox();
            int result = Float.compare(-rectangle1.getY(), -rectangle2.getY());
            if (result == 0) {
                result = Float.compare(rectangle1.getX(), rectangle2.getX());
            }
            return result;
        }
    }
}
