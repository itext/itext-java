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
package com.itextpdf.styledxmlparser.css.resolve.shorthand;


import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BackgroundPositionShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BackgroundShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderBottomShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderColorShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderLeftShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderRadiusShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderRightShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderStyleShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderTopShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.BorderWidthShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.ColumnRuleShortHandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.ColumnsShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.FlexFlowShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.FlexShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.FontShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.GapShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.GridColumnShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.GridRowShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.GridShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.GridTemplateShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.ListStyleShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.MarginShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.MarkerShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.OutlineShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.PaddingShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.PlaceItemsShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.TextDecorationShorthandResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for creating ShorthandResolver objects.
 */
public class ShorthandResolverFactory {
    
    /** The map of shorthand resolvers. */
    private static final Map<String, IShorthandResolver> shorthandResolvers;
    static {
        shorthandResolvers = new HashMap<>();
        shorthandResolvers.put(CommonCssConstants.BACKGROUND, new BackgroundShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BACKGROUND_POSITION, new BackgroundPositionShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER, new BorderShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_BOTTOM, new BorderBottomShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_COLOR, new BorderColorShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_LEFT, new BorderLeftShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_RADIUS, new BorderRadiusShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_RIGHT, new BorderRightShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_STYLE, new BorderStyleShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_TOP, new BorderTopShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.BORDER_WIDTH, new BorderWidthShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.FONT, new FontShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.LIST_STYLE, new ListStyleShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.MARGIN, new MarginShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.OUTLINE, new OutlineShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.PADDING, new PaddingShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.TEXT_DECORATION, new TextDecorationShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.FLEX, new FlexShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.FLEX_FLOW, new FlexFlowShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.GAP, new GapShorthandResolver(CommonCssConstants.GAP));
        shorthandResolvers.put(CommonCssConstants.GRID_GAP, new GapShorthandResolver(CommonCssConstants.GRID_GAP));
        shorthandResolvers.put(CommonCssConstants.PLACE_ITEMS, new PlaceItemsShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.COLUMNS, new ColumnsShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.COLUMN_RULE, new ColumnRuleShortHandResolver());
        shorthandResolvers.put(CommonCssConstants.GRID_ROW, new GridRowShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.GRID_COLUMN, new GridColumnShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.GRID_TEMPLATE, new GridTemplateShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.GRID, new GridShorthandResolver());
        shorthandResolvers.put(CommonCssConstants.MARKER, new MarkerShorthandResolver());
    }

    /**
     * Gets a shorthand resolver.
     *
     * @param shorthandProperty the property
     * @return the shorthand resolver
     */
    public static IShorthandResolver getShorthandResolver(String shorthandProperty) {
        return shorthandResolvers.get(shorthandProperty);
    }
}
