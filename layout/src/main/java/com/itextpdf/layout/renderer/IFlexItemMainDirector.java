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
    void applyJustifyContent(List<FlexUtil.FlexItemCalculationInfo> line, JustifyContent justifyContent,
            float freeSpace);
}
