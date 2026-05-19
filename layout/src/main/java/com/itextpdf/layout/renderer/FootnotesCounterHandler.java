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
import com.itextpdf.layout.element.Footnote;
import com.itextpdf.layout.element.FootnoteAnchor;
import com.itextpdf.layout.layout.RootLayoutArea;

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

    private final Map<FootnoteAnchor, FootnoteAnchorRenderer> renderers = new HashMap<>();

    private final Map<Footnote, Float> footnotes = new LinkedHashMap<>();

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
     * @param currentArea {@link RootLayoutArea} area to collect placed footnote anchors
     *
     * @return linked map of {@link Footnote} and its height float value
     */
    Map<Footnote, Float> collectFootnotes(RootLayoutArea currentArea) {
        footnotes.clear();
        List<FootnoteAnchor> anchors = new ArrayList<>(renderers.keySet());
        Collections.sort(anchors, new FootnoteAnchorComparator());

        for (FootnoteAnchor footnoteAnchor : anchors) {
            FootnoteAnchorRenderer renderer = renderers.get(footnoteAnchor);

            if (renderer.occupiedArea == null) {
                continue;
            }

            int expectedPageNumber = currentArea.getPageNumber();
            Rectangle intersection = renderer.occupiedArea.getBBox().getIntersection(currentArea.getBBox());

            if (expectedPageNumber == renderer.occupiedArea.getPageNumber() && intersection != null
                    && renderer.occupiedArea.getBBox().equalsWithEpsilon(intersection)) {
                footnotes.put(footnoteAnchor.getFootnote(),
                        renderer.footnoteRenderer.getOccupiedArea().getBBox().getHeight());
            }
        }
        return footnotes;
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
