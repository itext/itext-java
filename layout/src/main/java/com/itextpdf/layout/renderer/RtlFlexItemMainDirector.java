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

import com.itextpdf.layout.properties.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for right to left placement of flex items.
 */
class RtlFlexItemMainDirector implements IFlexItemMainDirector {
    RtlFlexItemMainDirector() {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void applyDirectionForLine(List<T> renderers) {
        Collections.reverse(renderers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyJustifyContent(List<FlexUtil.FlexItemCalculationInfo> line, JustifyContent justifyContent,
            float freeSpace) {

        if (freeSpace < 0 && (JustifyContent.SPACE_AROUND == justifyContent ||
                JustifyContent.SPACE_BETWEEN == justifyContent || JustifyContent.SPACE_EVENLY == justifyContent)) {
            return;
        }

        float space;
        switch (justifyContent) {
            case RIGHT:
            case END:
            // stretch in flexbox behaves as flex-start, see https://drafts.csswg.org/css-align/#distribution-flex
            case STRETCH:
            case NORMAL:
            case FLEX_START:
                line.get(line.size() - 1).xShift = freeSpace;
                break;
            case CENTER:
                line.get(line.size() - 1).xShift = freeSpace / 2;
                break;
            case SPACE_BETWEEN:
                if (line.size() == 1) {
                    line.get(0).xShift = freeSpace;
                } else {
                    space = freeSpace / (line.size() - 1);
                    for (int i = 0; i < line.size() - 1; i++) {
                        FlexUtil.FlexItemCalculationInfo item = line.get(i);
                        item.xShift = space;
                    }
                }
                break;
            case SPACE_AROUND:
                space = freeSpace / (line.size() * 2);
                for (int i = 0; i < line.size(); i++) {
                    FlexUtil.FlexItemCalculationInfo item =  line.get(i);
                    item.xShift = i == (line.size() - 1) ? space : space * 2;
                }
                break;
            case SPACE_EVENLY:
                space = freeSpace / (line.size() + 1);
                for (FlexUtil.FlexItemCalculationInfo item : line) {
                    item.xShift = space;
                }
                break;
            case FLEX_END:
            case START:
            case LEFT:
            default:
                // We don't need to do anything in these cases
        }
    }

}
