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

import com.itextpdf.layout.properties.AlignmentPropertyValue;
import com.itextpdf.layout.properties.JustifyContent;
import com.itextpdf.layout.renderer.FlexUtil.FlexItemCalculationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for left to right placement of flex items.
 */
class LtrFlexItemMainDirector implements IFlexItemMainDirector {
    LtrFlexItemMainDirector() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRenderer> applyDirection(List<List<FlexItemInfo>> lines) {
        List<IRenderer> renderers = new ArrayList<>();
        for (List<FlexItemInfo> line : lines) {
            applyDirectionForLine(line);
            for (FlexItemInfo itemInfo : line) {
                renderers.add(itemInfo.getRenderer());
            }
        }

        return renderers;
    }

    @Override
    public <T> void applyDirectionForLine(List<T> renderers) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyJustifyContent(List<FlexUtil.FlexItemCalculationInfo> line, JustifyContent justifyContent,
                               float freeSpace) {
        switch (justifyContent) {
            case RIGHT:
            case END:
            case SELF_END:
            case FLEX_END:
                line.get(0).xShift = freeSpace;
                break;
            case CENTER:
                line.get(0).xShift = freeSpace / 2;
                break;
            case FLEX_START:
            case NORMAL:
            case STRETCH:
            case START:
            case LEFT:
            case SELF_START:
            default:
                // We don't need to do anything in these cases
        }
    }
}
