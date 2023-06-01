package com.itextpdf.layout.renderer;

import com.itextpdf.layout.properties.JustifyContent;

import java.util.List;

/**
 * Base interface to customize placing flex items on main flex direction.
 */
interface IFlexItemMainDirector {
    /**
     * Apply the direction for placement the items in flex container.
     *
     * @param lines flex lines calculated by {@link FlexUtil}.
     * @return All child renderers in updated order.
     */
    List<IRenderer> applyDirection(List<List<FlexItemInfo>> lines);

    /**
     * Apply the direction for placement the items in flex line.
     *
     * @param renderers list of renderers or {@link FlexItemInfo}.
     */
    <T> void applyDirectionForLine(List<T> renderers);

    /**
     * Apply alignment on main flex direction.
     *
     * @param line flex line of items to apply alignment to.
     * @param justifyContent alignment to apply.
     * @param freeSpace precalculated free space to distribute between flex items in a line.
     */
    void applyAlignment(List<FlexUtil.FlexItemCalculationInfo> line, JustifyContent justifyContent, float freeSpace);
}
