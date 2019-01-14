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
package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link IShorthandResolver} implementation for backgrounds.
 */
public class BackgroundShorthandResolver implements IShorthandResolver {

    /** The Constant UNDEFINED_TYPE. */
    private static final int UNDEFINED_TYPE = -1;

    /** The Constant BACKGROUND_COLOR_TYPE. */
    private static final int BACKGROUND_COLOR_TYPE = 0;

    /** The Constant BACKGROUND_IMAGE_TYPE. */
    private static final int BACKGROUND_IMAGE_TYPE = 1;

    /** The Constant BACKGROUND_POSITION_TYPE. */
    private static final int BACKGROUND_POSITION_TYPE = 2;

    /** The Constant BACKGROUND_POSITION_OR_SIZE_TYPE. */
    private static final int BACKGROUND_POSITION_OR_SIZE_TYPE = 3; // might have the same type, but position always precedes size

    /** The Constant BACKGROUND_REPEAT_TYPE. */
    private static final int BACKGROUND_REPEAT_TYPE = 4;

    /** The Constant BACKGROUND_ORIGIN_OR_CLIP_TYPE. */
    private static final int BACKGROUND_ORIGIN_OR_CLIP_TYPE = 5; // have the same possible values but apparently origin values precedes clip value

    /** The Constant BACKGROUND_CLIP_TYPE. */
    private static final int BACKGROUND_CLIP_TYPE = 6;

    /** The Constant BACKGROUND_ATTACHMENT_TYPE. */
    private static final int BACKGROUND_ATTACHMENT_TYPE = 7;

    // With CSS3, you can apply multiple backgrounds to elements. These are layered atop one another
    // with the first background you provide on top and the last background listed in the back. Only
    // the last background can include a background color.

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (CommonCssConstants.INITIAL.equals(shorthandExpression) || CommonCssConstants.INHERIT.equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.BACKGROUND_COLOR, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_ATTACHMENT, shorthandExpression)
            );
        }

        List<String> commaSeparatedExpressions = splitMultipleBackgrounds(shorthandExpression);

        // TODO ignore multiple backgrounds at the moment
        String backgroundExpression = commaSeparatedExpressions.get(0);
        String[] resolvedProps = new String[8];

        String[] props = backgroundExpression.split("\\s+");
        boolean slashEncountered = false;
        for (String value : props) {
            int slashCharInd = value.indexOf('/');
            if (slashCharInd > 0 && !value.contains("url(")) {
                slashEncountered = true;
                String value1 = value.substring(0, slashCharInd);
                String value2 = value.substring(slashCharInd + 1, value.length());
                putPropertyBasedOnType(resolvePropertyType(value1), value1, resolvedProps, false);
                putPropertyBasedOnType(resolvePropertyType(value2), value2, resolvedProps, true);
            } else {
                putPropertyBasedOnType(resolvePropertyType(value), value, resolvedProps, slashEncountered);
            }

        }

        for (int i = 0; i < resolvedProps.length; ++i) {
            if (resolvedProps[i] == null) {
                resolvedProps[i] = CommonCssConstants.INITIAL;
            }
        }
        List<CssDeclaration> cssDeclarations = Arrays.asList(
                new CssDeclaration(CommonCssConstants.BACKGROUND_COLOR, resolvedProps[BACKGROUND_COLOR_TYPE]),
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, resolvedProps[BACKGROUND_IMAGE_TYPE]),
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION, resolvedProps[BACKGROUND_POSITION_TYPE]),
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, resolvedProps[BACKGROUND_POSITION_OR_SIZE_TYPE]),
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, resolvedProps[BACKGROUND_REPEAT_TYPE]),
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, resolvedProps[BACKGROUND_ORIGIN_OR_CLIP_TYPE]),
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, resolvedProps[BACKGROUND_CLIP_TYPE]),
                new CssDeclaration(CommonCssConstants.BACKGROUND_ATTACHMENT, resolvedProps[BACKGROUND_ATTACHMENT_TYPE])
        );

        return cssDeclarations;
    }

    /**
     * Resolves the property type.
     *
     * @param value the value
     * @return the property type value
     */
    private int resolvePropertyType(String value) {
        if (value.contains("url(") || CommonCssConstants.NONE.equals(value)) {
            return BACKGROUND_IMAGE_TYPE;
        } else if (CommonCssConstants.BACKGROUND_REPEAT_VALUES.contains(value)) {
            return BACKGROUND_REPEAT_TYPE;
        } else if (CommonCssConstants.BACKGROUND_ATTACHMENT_VALUES.contains(value)) {
            return BACKGROUND_ATTACHMENT_TYPE;
        } else if (CommonCssConstants.BACKGROUND_POSITION_VALUES.contains(value)) {
            return BACKGROUND_POSITION_TYPE;
        } else if (CssUtils.isNumericValue(value) || CssUtils.isMetricValue(value) || CssUtils.isRelativeValue(value)) {
            return BACKGROUND_POSITION_OR_SIZE_TYPE;
        } else if (CommonCssConstants.BACKGROUND_SIZE_VALUES.contains(value)) {
            return BACKGROUND_POSITION_OR_SIZE_TYPE;
        } else if(CssUtils.isColorProperty(value)) {
            return BACKGROUND_COLOR_TYPE;
        } else if (CommonCssConstants.BACKGROUND_ORIGIN_OR_CLIP_VALUES.contains(value)) {
            return BACKGROUND_ORIGIN_OR_CLIP_TYPE;
        }
        return UNDEFINED_TYPE;
    }

    /**
     * Registers a property based on its type.
     *
     * @param type the property type
     * @param value the property value
     * @param resolvedProps the resolved properties
     * @param slashEncountered indicates whether a slash was encountered
     */
    private void putPropertyBasedOnType(int type, String value, String[] resolvedProps, boolean slashEncountered) {
        if (type == UNDEFINED_TYPE) {
            Logger logger = LoggerFactory.getLogger(BackgroundShorthandResolver.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES, value));
            return;
        }
        if (type == BACKGROUND_POSITION_OR_SIZE_TYPE && !slashEncountered) {
            type = BACKGROUND_POSITION_TYPE;
        }

        if (type == BACKGROUND_ORIGIN_OR_CLIP_TYPE && resolvedProps[BACKGROUND_ORIGIN_OR_CLIP_TYPE] != null) {
            type = BACKGROUND_CLIP_TYPE;
        }

        if ((type == BACKGROUND_POSITION_OR_SIZE_TYPE || type == BACKGROUND_POSITION_TYPE) && resolvedProps[type] != null) {
            resolvedProps[type] += " " + value;
        } else {
            resolvedProps[type] = value;
        }
    }

    /**
     * Splits multiple backgrounds.
     *
     * @param shorthandExpression the shorthand expression
     * @return the list of backgrounds
     */
    private List<String> splitMultipleBackgrounds(String shorthandExpression) {
        List<String> commaSeparatedExpressions = new ArrayList<>();
        boolean isInsideParentheses = false; // in order to avoid split inside rgb/rgba color definition
        int prevStart = 0;
        for (int i = 0; i < shorthandExpression.length(); ++i) {
            if (shorthandExpression.charAt(i) == ',' && !isInsideParentheses) {
                commaSeparatedExpressions.add(shorthandExpression.substring(prevStart, i));
                prevStart = i + 1;
            } else if (shorthandExpression.charAt(i) == '(') {
                isInsideParentheses = true;
            } else if (shorthandExpression.charAt(i) == ')') {
                isInsideParentheses = false;
            }
        }

        if (commaSeparatedExpressions.isEmpty()) {
            commaSeparatedExpressions.add(shorthandExpression);
        }
        return commaSeparatedExpressions;
    }
}
