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
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnoteNumberingConfig;
import com.itextpdf.layout.properties.margins.FootnoteNumberingType;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.properties.margins.FootnotesUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper handler class to collect and count footnotes placed on the page.
 */
class FootnotesCounterHandler {
    private static final int DEFAULT_FONT_SIZE = 6;
    private static final int DEFAULT_TEXT_RISE = 7;

    private final Map<Footnote, FootnoteRenderer> footnotes = new LinkedHashMap<>();

    /**
     * Creates a new {@link FootnotesCounterHandler} instance.
     */
    public FootnotesCounterHandler() {
        // Empty constructor.
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
        footnotes.clear();
    }

    /**
     * Collects footnotes which anchors are placed in the current area
     * in order their anchors are placed on a page from top to bottom and left to right.
     *
     * @param renderer parent renderer to collect footnotes from
     * @param footnotesAnchorsFound a list to store the encountered footnote anchors
     *
     * @return linked map of {@link Footnote} and corresponding renderers.
     */
    Map<Footnote, FootnoteRenderer> collectFootnotes(IRenderer renderer,
            List<FootnoteAnchorRenderer> footnotesAnchorsFound) {
        footnotesAnchorsFound.clear();
        footnotes.clear();

        collectFromTree(renderer, footnotes, footnotesAnchorsFound);
        return footnotes;
    }


    /**
     * Updates footnote anchors using automatic numbering and styles configured via {@link FootnotesProperties}.
     *
     * @param footnotesProperties {@link FootnotesProperties} with optional {@link FootnoteNumberingType}
     * specifying type for numbering of the footnote anchors and optional styles for footnote anchors
     * @param latestFootnoteNum the number of the previous placed footnote based on {@link FootnoteNumberingConfig}
     * @param anchorsToNumber the list of anchors to apply the renumbering on
     */
    void updateFootnoteNumberingAndStyles(FootnotesProperties footnotesProperties, int latestFootnoteNum,
            Collection<FootnoteAnchorRenderer> anchorsToNumber) {
        if (footnotesProperties == null) {
            return;
        }
        Style footnoteAnchorLabelStyle = footnotesProperties.getFootnoteAnchorLabelStyle();
        if (footnoteAnchorLabelStyle != null) {
            for (FootnoteAnchorRenderer renderer : anchorsToNumber) {
                FootnotesUtil.applyFootnoteAnchorStyle((FootnoteAnchor) renderer.getModelElement(),
                        footnoteAnchorLabelStyle);
            }
        }
        if (footnotesProperties.getFootnoteNumberingType() == null) {
            return;
        }
        FootnoteNumberingType footnoteNumberingType = footnotesProperties.getFootnoteNumberingType();
        List<FootnoteAnchorRenderer> anchors = anchorsToNumber.stream().sorted(
                (renderer1, renderer2) -> {
                    int result = Float.compare(-renderer1.yPos, -renderer2.yPos);
                    if (result == 0) {
                        Rectangle rectangle1 = renderer1.occupiedArea.getBBox();
                        Rectangle rectangle2 = renderer2.occupiedArea.getBBox();
                        result = Float.compare(rectangle1.getX(), rectangle2.getX());
                    }
                    return result;
                }
        ).collect(
                Collectors.toList());

        int footnoteNum = latestFootnoteNum + 1;
        for (FootnoteAnchorRenderer renderer : anchors) {
            IRenderer currentSymbolRenderer = makeFootnoteNumSymbolRenderer(footnoteNum, footnoteNumberingType);
            ++footnoteNum;
            renderer.addSymbolRenderer(currentSymbolRenderer);
        }
    }

    private static void collectFromTree(IRenderer renderer, Map<Footnote, FootnoteRenderer> footnotes,
            List<FootnoteAnchorRenderer> footnotesAnchorsFound) {
        if (renderer == null) {
            return;
        }
        TableRenderer tableRenderer = null;
        if (renderer instanceof TableRenderer) {
            tableRenderer = (TableRenderer) renderer;
            if (tableRenderer.headerRenderer != null) {
                collectFromTree(tableRenderer.headerRenderer, footnotes, footnotesAnchorsFound);
            }
        }
        for (IRenderer child : renderer.getChildRenderers()) {
            if (child instanceof FootnoteAnchorRenderer) {
                footnotesAnchorsFound.add((FootnoteAnchorRenderer) child);
                FootnoteRenderer footnoteRenderer = ((FootnoteAnchorRenderer) child).footnoteRenderer;
                if (footnoteRenderer == null) {
                    continue;
                }
                footnotes.put((Footnote) footnoteRenderer.getModelElement(), footnoteRenderer);
            } else {
                collectFromTree(child, footnotes, footnotesAnchorsFound);
            }
        }
        if (tableRenderer != null && tableRenderer.footerRenderer != null) {
            collectFromTree(tableRenderer.footerRenderer, footnotes, footnotesAnchorsFound);
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
}
