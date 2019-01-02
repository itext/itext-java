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
package com.itextpdf.styledxmlparser.css.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities class that parses {@link String} values into {@link MediaQuery} or {@link MediaExpression} values.
 */
public final class MediaQueryParser {

    /**
     * Creates a {@link MediaQueryParser} instance.
     */
    private MediaQueryParser() {
    }

    /**
     * Parses a {@link String} into a {@link List} of {@link MediaQuery} values.
     *
     * @param mediaQueriesStr the media queries in the form of a {@link String}
     * @return the resulting {@link List} of {@link MediaQuery} values
     */
    static List<MediaQuery> parseMediaQueries(String mediaQueriesStr) {
        String[] mediaQueryStrs = mediaQueriesStr.split(",");
        List<MediaQuery> mediaQueries = new ArrayList<>();
        for (String mediaQueryStr : mediaQueryStrs) {
            MediaQuery mediaQuery = parseMediaQuery(mediaQueryStr);
            if (mediaQuery != null) {
                mediaQueries.add(mediaQuery);
            }
        }
        return mediaQueries;
    }

    /**
     * Parses a {@link String} into a {@link MediaQuery} value.
     *
     * @param mediaQueryStr the media query in the form of a {@link String}
     * @return the resulting {@link MediaQuery} value
     */
    static MediaQuery parseMediaQuery(String mediaQueryStr) {
        mediaQueryStr = mediaQueryStr.trim().toLowerCase();
        boolean only = false;
        boolean not = false;
        if (mediaQueryStr.startsWith(MediaRuleConstants.ONLY)) {
            only = true;
            mediaQueryStr = mediaQueryStr.substring(MediaRuleConstants.ONLY.length()).trim();
        } else if (mediaQueryStr.startsWith(MediaRuleConstants.NOT)) {
            not = true;
            mediaQueryStr = mediaQueryStr.substring(MediaRuleConstants.NOT.length()).trim();
        }

        int indexOfSpace = mediaQueryStr.indexOf(' ');
        String firstWord = indexOfSpace != -1 ? mediaQueryStr.substring(0, indexOfSpace) : mediaQueryStr;

        String mediaType = null;
        List<MediaExpression> mediaExpressions = null;

        if (only || not || MediaType.isValidMediaType(firstWord)) {
            mediaType = firstWord;
            mediaExpressions = parseMediaExpressions(mediaQueryStr.substring(firstWord.length()), true);
        } else {
            mediaExpressions = parseMediaExpressions(mediaQueryStr, false);
        }

        return new MediaQuery(mediaType, mediaExpressions, only, not);
    }

    /**
     * Parses a {@link String} into a list of {@link MediaExpression} values.
     *
     * @param mediaExpressionsStr the media expressions in the form of a {@link String}
     * @param shallStartWithAnd   indicates if the media expression shall start with "and"
     * @return the resulting list of {@link MediaExpression} values
     */
    private static List<MediaExpression> parseMediaExpressions(String mediaExpressionsStr, boolean shallStartWithAnd) {
        mediaExpressionsStr = mediaExpressionsStr.trim();
        boolean startsWithEnd = mediaExpressionsStr.startsWith(MediaRuleConstants.AND);

        boolean firstExpression = true;
        String[] mediaExpressionStrs = mediaExpressionsStr.split(MediaRuleConstants.AND);
        List<MediaExpression> expressions = new ArrayList<>();
        for (String mediaExpressionStr : mediaExpressionStrs) {
            MediaExpression expression = parseMediaExpression(mediaExpressionStr);
            if (expression != null) {
                if (firstExpression) {
                    if (shallStartWithAnd && !startsWithEnd) {
                        throw new IllegalStateException("Expected 'and' while parsing media expression");
                    }
                }
                firstExpression = false;
                expressions.add(expression);
            }
        }
        return expressions;
    }

    /**
     * Parses a {@link String} into a {@link MediaExpression} value.
     *
     * @param mediaExpressionStr the media expression in the form of a {@link String}
     * @return the resulting {@link MediaExpression} value
     */
    private static MediaExpression parseMediaExpression(String mediaExpressionStr) {
        mediaExpressionStr = mediaExpressionStr.trim();
        if (!mediaExpressionStr.startsWith("(") || !mediaExpressionStr.endsWith(")")) {
            return null;
        }
        mediaExpressionStr = mediaExpressionStr.substring(1, mediaExpressionStr.length() - 1);
        if (mediaExpressionStr.length() == 0) {
            return null;
        }
        int colonPos = mediaExpressionStr.indexOf(':');
        String mediaFeature;
        String value = null;
        if (colonPos == -1) {
            mediaFeature = mediaExpressionStr;
        } else {
            mediaFeature = mediaExpressionStr.substring(0, colonPos).trim();
            value = mediaExpressionStr.substring(colonPos + 1).trim();
        }
        return new MediaExpression(mediaFeature, value);
    }

}
