/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.Property;

import java.util.ArrayList;
import java.util.List;

final class TableBorderUtil {
    private TableBorderUtil() {
    }

    public static Border getCellSideBorder(Cell cellModel, int borderType) {
        Border cellModelSideBorder = cellModel.<Border>getProperty(borderType);
        if (null == cellModelSideBorder && !cellModel.hasProperty(borderType)) {
            cellModelSideBorder = cellModel.<Border>getProperty(Property.BORDER);
            if (null == cellModelSideBorder && !cellModel.hasProperty(Property.BORDER)) {
                cellModelSideBorder = cellModel.<Border>getDefaultProperty(Property.BORDER);
            }
        }
        return cellModelSideBorder;
    }

    public static Border getWidestBorder(List<Border> borderList) {
        Border theWidestBorder = null;
        if (0 != borderList.size()) {
            for (Border border : borderList) {
                if (null != border && (null == theWidestBorder || border.getWidth() > theWidestBorder.getWidth())) {
                    theWidestBorder = border;
                }
            }
        }
        return theWidestBorder;
    }

    public static Border getWidestBorder(List<Border> borderList, int start, int end) {
        Border theWidestBorder = null;
        if (0 != borderList.size()) {
            for (Border border : borderList.subList(start, end)) {
                if (null != border && (null == theWidestBorder || border.getWidth() > theWidestBorder.getWidth())) {
                    theWidestBorder = border;
                }
            }
        }
        return theWidestBorder;
    }

    public static List<Border> createAndFillBorderList(Border border, int size) {
        List<Border> borderList = new ArrayList<Border>();
        for (int i = 0; i < size; i++) {
            borderList.add(border);
        }
        return borderList;
    }

    public static List<Border> createAndFillBorderList(List<Border> originalList, Border borderToCollapse, int size) {
        List<Border> borderList;
        if (null != originalList) {
            borderList = new ArrayList<>(originalList.size() + size);
            borderList.addAll(originalList);
        }else{
            borderList = new ArrayList<>(size);
        }
        while (borderList.size() < size) {
            borderList.add(borderToCollapse);
        }
        int end = null == originalList ? size : Math.min(originalList.size(), size);
        for (int i = 0; i < end; i++) {
            if (null == borderList.get(i) || (null != borderToCollapse && borderList.get(i).getWidth() <= borderToCollapse.getWidth())) {
                borderList.set(i, borderToCollapse);
            }
        }
        return borderList;
    }
}
