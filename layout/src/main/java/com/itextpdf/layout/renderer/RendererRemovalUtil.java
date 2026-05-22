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

import java.util.ArrayList;
import java.util.List;

// TODO DEVSIX-10004: Remove after the change
/**
 * The class stores logic for removing renderers from a renderer tree.
 */
final class RendererRemovalUtil {
    private RendererRemovalUtil() {
        // do nothing
    }

    /**
     * Removes all SectionBreak and AreaBreak instances from the renderer tree.
     *
     * @param renderer {@link IRenderer} from which the renderers will be removed
     *
     * @return <code>boolean</code> value indicating whether a removal occurred.
     */
    static boolean removeAreaBreakAndSectionBreakDescendants(IRenderer renderer) {
        boolean rendererRemoved = false;
        List<IRenderer> descendants = new ArrayList<>();
        descendants.add(renderer);
        while (!descendants.isEmpty()) {
            IRenderer descendant = descendants.remove(descendants.size() - 1);
            if (descendant == null) {
                continue;
            }
            if (descendant instanceof TableRenderer) {
                TableRenderer tableRenderer = (TableRenderer) descendant;
                descendants.add(tableRenderer.headerRenderer);
                descendants.add(tableRenderer.footerRenderer);
            }
            List<IRenderer> descendantChildRenderers = descendant.getChildRenderers();
            if (descendantChildRenderers == null) {
                continue;
            }
            boolean childOfDescendantRemoved = descendantChildRenderers.removeIf(
                    childOfDescendant -> childOfDescendant instanceof AreaBreakRenderer
                            || childOfDescendant instanceof SectionBreakRenderer);
            if (childOfDescendantRemoved) {
                rendererRemoved = true;
            }
            descendants.addAll(descendantChildRenderers);
        }
        return rendererRemoved;
    }
}
