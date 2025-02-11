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
