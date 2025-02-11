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
package com.itextpdf.layout.renderer;

import com.itextpdf.layout.properties.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler to handle target-counter logic.
 */
public class TargetCounterHandler {

    /**
     * Pages for all renderers with id.
     */
    private Map<String, Integer> renderersPages = new HashMap<>();

    private Map<String, Integer> previousRenderersPages = new HashMap<>();

    /**
     * Creates a copy of the given {@link TargetCounterHandler} instance.
     *
     * @param targetCounterHandler {@link TargetCounterHandler} instance to be copied
     */
    public TargetCounterHandler(TargetCounterHandler targetCounterHandler) {
        this.renderersPages = targetCounterHandler.renderersPages;
        this.previousRenderersPages = targetCounterHandler.previousRenderersPages;
    }

    /**
     * Creates a new {@link TargetCounterHandler} instance.
     */
    public TargetCounterHandler() {}

    /**
     * Adds renderer's page to the root renderer map.
     *
     * @param renderer renderer from which page and root renderer will be taken.
     */
    public static void addPageByID(IRenderer renderer) {
        final String id = renderer.<String>getProperty(Property.ID);
        if (id != null) {
            final TargetCounterHandler targetCounterHandler = getTargetCounterHandler(renderer);
            if (targetCounterHandler != null && renderer.getOccupiedArea() != null) {
                final int currentPageNumber = renderer.getOccupiedArea().getPageNumber();
                targetCounterHandler.renderersPages.put(id, currentPageNumber);
            }
        }
    }

    /**
     * Gets page from renderer using given id.
     *
     * @param renderer renderer from which root renderer will be taken
     * @param id key to the renderersPages Map
     * @return page on which renderer was layouted
     */
    public static Integer getPageByID(IRenderer renderer, String id) {
        final TargetCounterHandler targetCounterHandler = getTargetCounterHandler(renderer);
        return targetCounterHandler == null ? null : targetCounterHandler.previousRenderersPages.get(id);
    }

    /**
     * Indicates if page value was defined for this id.
     *
     * @param renderer renderer from which root renderer will be taken
     * @param id target id
     * @return true if value is defined for this id, false otherwise
     */
    public static boolean isValueDefinedForThisId(IRenderer renderer, String id) {
        final TargetCounterHandler targetCounterHandler = getTargetCounterHandler(renderer);
        return targetCounterHandler != null && targetCounterHandler.renderersPages.containsKey(id);
    }

    /**
     * Indicates if relayout is required.
     *
     * @return true if relayout is required, false otherwise
     */
    public boolean isRelayoutRequired() {
        for (Map.Entry<String, Integer> rendererPage : renderersPages.entrySet()) {
            if (!rendererPage.getValue().equals(previousRenderersPages.get(rendererPage.getKey()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prepares handler to relayout.
     */
    public void prepareHandlerToRelayout() {
        previousRenderersPages = new HashMap<>(renderersPages);
    }

    private static TargetCounterHandler getTargetCounterHandler(IRenderer renderer) {
        IRenderer rootRenderer = renderer;
        while (rootRenderer.getParent() != null) {
            rootRenderer = rootRenderer.getParent();
        }
        if (rootRenderer instanceof DocumentRenderer) {
            return ((DocumentRenderer) rootRenderer).getTargetCounterHandler();
        }
        return null;
    }
}
