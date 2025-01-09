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
package com.itextpdf.styledxmlparser.css.resolve;


import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that allows you to get the default values of CSS properties.
 */
public class CssDefaults {

    /** A map with properties and their default values. */
    private static final Map<String, String> defaultValues = new HashMap<>();

    static {
        defaultValues.put(CommonCssConstants.COLOR, "black"); // not specified, varies from browser to browser
        defaultValues.put(CommonCssConstants.OPACITY, "1");

        defaultValues.put(CommonCssConstants.BACKGROUND_ATTACHMENT, CommonCssConstants.SCROLL);
        defaultValues.put(CommonCssConstants.BACKGROUND_BLEND_MODE, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.BACKGROUND_COLOR, CommonCssConstants.TRANSPARENT);
        defaultValues.put(CommonCssConstants.BACKGROUND_IMAGE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.BACKGROUND_POSITION, "0% 0%");
        defaultValues.put(CommonCssConstants.BACKGROUND_POSITION_X, "0%");
        defaultValues.put(CommonCssConstants.BACKGROUND_POSITION_Y, "0%");
        defaultValues.put(CommonCssConstants.BACKGROUND_REPEAT, CommonCssConstants.REPEAT);
        defaultValues.put(CommonCssConstants.BACKGROUND_CLIP, CommonCssConstants.BORDER_BOX);
        defaultValues.put(CommonCssConstants.BACKGROUND_ORIGIN, CommonCssConstants.PADDING_BOX);
        defaultValues.put(CommonCssConstants.BACKGROUND_SIZE, CommonCssConstants.AUTO);

        defaultValues.put(CommonCssConstants.BORDER_BOTTOM_COLOR, CommonCssConstants.CURRENTCOLOR);
        defaultValues.put(CommonCssConstants.BORDER_LEFT_COLOR, CommonCssConstants.CURRENTCOLOR);
        defaultValues.put(CommonCssConstants.BORDER_RIGHT_COLOR, CommonCssConstants.CURRENTCOLOR);
        defaultValues.put(CommonCssConstants.BORDER_TOP_COLOR, CommonCssConstants.CURRENTCOLOR);
        defaultValues.put(CommonCssConstants.BORDER_BOTTOM_STYLE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.BORDER_LEFT_STYLE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.BORDER_RIGHT_STYLE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.BORDER_TOP_STYLE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.BORDER_BOTTOM_WIDTH, CommonCssConstants.MEDIUM);
        defaultValues.put(CommonCssConstants.BORDER_LEFT_WIDTH, CommonCssConstants.MEDIUM);
        defaultValues.put(CommonCssConstants.BORDER_RIGHT_WIDTH, CommonCssConstants.MEDIUM);
        defaultValues.put(CommonCssConstants.BORDER_TOP_WIDTH, CommonCssConstants.MEDIUM);
        defaultValues.put(CommonCssConstants.BORDER_WIDTH, CommonCssConstants.MEDIUM);
        defaultValues.put(CommonCssConstants.BORDER_IMAGE, CommonCssConstants.NONE);

        defaultValues.put(CommonCssConstants.BORDER_RADIUS, "0");
        defaultValues.put(CommonCssConstants.BORDER_BOTTOM_LEFT_RADIUS, "0");
        defaultValues.put(CommonCssConstants.BORDER_BOTTOM_RIGHT_RADIUS, "0");
        defaultValues.put(CommonCssConstants.BORDER_TOP_LEFT_RADIUS, "0");
        defaultValues.put(CommonCssConstants.BORDER_TOP_RIGHT_RADIUS, "0");

        defaultValues.put(CommonCssConstants.BOX_SHADOW, CommonCssConstants.NONE);

        defaultValues.put(CommonCssConstants.FLEX_BASIS, CommonCssConstants.AUTO);
        defaultValues.put(CommonCssConstants.FLEX_DIRECTION, CommonCssConstants.ROW);
        defaultValues.put(CommonCssConstants.FLEX_GROW, "0");
        defaultValues.put(CommonCssConstants.FLEX_SHRINK, "1");
        defaultValues.put(CommonCssConstants.FLEX_WRAP, CommonCssConstants.NOWRAP);

        defaultValues.put(CommonCssConstants.FLOAT, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.FONT_FAMILY, "times");
        defaultValues.put(CommonCssConstants.FONT_SIZE, CommonCssConstants.MEDIUM);
        defaultValues.put(CommonCssConstants.FONT_STYLE, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.FONT_VARIANT, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.FONT_WEIGHT, CommonCssConstants.NORMAL);

        defaultValues.put(CommonCssConstants.HEIGHT, CommonCssConstants.AUTO);
        defaultValues.put(CommonCssConstants.HYPHENS, CommonCssConstants.MANUAL);

        defaultValues.put(CommonCssConstants.LINE_HEIGHT, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.LIST_STYLE_TYPE, CommonCssConstants.DISC);
        defaultValues.put(CommonCssConstants.LIST_STYLE_IMAGE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.LIST_STYLE_POSITION, CommonCssConstants.OUTSIDE);

        defaultValues.put(CommonCssConstants.MARGIN_BOTTOM, "0");
        defaultValues.put(CommonCssConstants.MARGIN_LEFT, "0");
        defaultValues.put(CommonCssConstants.MARGIN_RIGHT, "0");
        defaultValues.put(CommonCssConstants.MARGIN_TOP, "0");

        defaultValues.put(CommonCssConstants.MIN_HEIGHT, "0");

        defaultValues.put(CommonCssConstants.OUTLINE_COLOR, CommonCssConstants.CURRENTCOLOR);
        defaultValues.put(CommonCssConstants.OUTLINE_STYLE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.OUTLINE_WIDTH, CommonCssConstants.MEDIUM);

        defaultValues.put(CommonCssConstants.PADDING_BOTTOM, "0");
        defaultValues.put(CommonCssConstants.PADDING_LEFT, "0");
        defaultValues.put(CommonCssConstants.PADDING_RIGHT, "0");
        defaultValues.put(CommonCssConstants.PADDING_TOP, "0");

        defaultValues.put(CommonCssConstants.PAGE_BREAK_AFTER, CommonCssConstants.AUTO);
        defaultValues.put(CommonCssConstants.PAGE_BREAK_BEFORE, CommonCssConstants.AUTO);
        defaultValues.put(CommonCssConstants.PAGE_BREAK_INSIDE, CommonCssConstants.AUTO);

        defaultValues.put(CommonCssConstants.POSITION, CommonCssConstants.STATIC);

        defaultValues.put(CommonCssConstants.QUOTES, "\"\\00ab\" \"\\00bb\"");

        defaultValues.put(CommonCssConstants.TEXT_ALIGN, CommonCssConstants.START);
        defaultValues.put(CommonCssConstants.TEXT_DECORATION, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.TEXT_DECORATION_LINE, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.TEXT_DECORATION_STYLE, CommonCssConstants.SOLID);
        defaultValues.put(CommonCssConstants.TEXT_DECORATION_COLOR, CommonCssConstants.CURRENTCOLOR);
        defaultValues.put(CommonCssConstants.TEXT_TRANSFORM, CommonCssConstants.NONE);

        defaultValues.put(CommonCssConstants.WHITE_SPACE, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.WIDTH, CommonCssConstants.AUTO);

        defaultValues.put(CommonCssConstants.ORPHANS, "2");
        defaultValues.put(CommonCssConstants.WIDOWS, "2");

        defaultValues.put(CommonCssConstants.JUSTIFY_CONTENT, CommonCssConstants.FLEX_START);
        defaultValues.put(CommonCssConstants.ALIGN_ITEMS, CommonCssConstants.STRETCH);

        // Other css properties default values will be added as needed
    }

    /**
     * Gets the default value of a property.
     *
     * @param property the property
     * @return the default value
     */
    public static String getDefaultValue(String property) {
        String defaultVal = defaultValues.get(property);
        if (defaultVal == null) {
            Logger logger = LoggerFactory.getLogger(CssDefaults.class);
            logger.error(
                    MessageFormatUtil.format(StyledXmlParserLogMessageConstant.DEFAULT_VALUE_OF_CSS_PROPERTY_UNKNOWN,
                            property));
        }
        return defaultVal;
    }
}
