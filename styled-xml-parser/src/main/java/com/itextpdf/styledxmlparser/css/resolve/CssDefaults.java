/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.resolve;


import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
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

        defaultValues.put(CommonCssConstants.FLOAT, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.FONT_FAMILY, "times");
        defaultValues.put(CommonCssConstants.FONT_SIZE, CommonCssConstants.MEDIUM);
        defaultValues.put(CommonCssConstants.FONT_STYLE, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.FONT_VARIANT, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.FONT_WEIGHT, CommonCssConstants.NORMAL);

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
        defaultValues.put(CommonCssConstants.TEXT_TRANSFORM, CommonCssConstants.NONE);
        defaultValues.put(CommonCssConstants.TEXT_DECORATION, CommonCssConstants.NONE);

        defaultValues.put(CommonCssConstants.WHITE_SPACE, CommonCssConstants.NORMAL);
        defaultValues.put(CommonCssConstants.WIDTH, CommonCssConstants.AUTO);

        // TODO not complete
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
            logger.error(MessageFormatUtil.format(LogMessageConstant.DEFAULT_VALUE_OF_CSS_PROPERTY_UNKNOWN, property));
        }
        return defaultVal;
    }
}
