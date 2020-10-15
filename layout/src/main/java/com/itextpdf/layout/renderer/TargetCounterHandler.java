package com.itextpdf.layout.renderer;

import com.itextpdf.layout.property.Property;

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

    /**
     * Indicates if relayout is required.
     */
    private boolean isRelayoutRequired = false;

    /**
     * Creates a copy of the given {@link TargetCounterHandler} instance.
     *
     * @param targetCounterHandler {@link TargetCounterHandler} instance to be copied
     */
    public TargetCounterHandler(TargetCounterHandler targetCounterHandler) {
        this.renderersPages = targetCounterHandler.renderersPages;
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
                final Integer prevPageNumber = targetCounterHandler.renderersPages.get(id);
                final int currentPageNumber = renderer.getOccupiedArea().getPageNumber();
                if (prevPageNumber == null || currentPageNumber > prevPageNumber) {
                    targetCounterHandler.renderersPages.put(id, currentPageNumber);
                    targetCounterHandler.isRelayoutRequired = true;
                }
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
        return targetCounterHandler == null ? null : targetCounterHandler.renderersPages.get(id);
    }

    /**
     * Indicates if page value was defined for this id.
     *
     * @param renderer renderer from which root renderer will be taken
     * @param id target id
     * @return true if value is defined for this id, false otherwise
     */
    public static boolean isValueDefinedForThisID(IRenderer renderer, String id) {
        final TargetCounterHandler targetCounterHandler = getTargetCounterHandler(renderer);
        return targetCounterHandler != null && targetCounterHandler.renderersPages.containsKey(id);
    }

    /**
     * Indicates if relayout is required.
     *
     * @return true if relayout is required, false otherwise
     */
    public boolean isRelayoutRequired() {
        return isRelayoutRequired;
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
