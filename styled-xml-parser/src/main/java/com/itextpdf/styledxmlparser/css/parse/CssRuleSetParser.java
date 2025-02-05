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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities class to parse CSS rule sets.
 */
public final class CssRuleSetParser {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CssRuleSetParser.class);

    /**
     * Creates a new {@link CssRuleSetParser} instance.
     */
    private CssRuleSetParser() {
    }

    /**
     * Parses property declarations.
     *
     * @param propertiesStr the property declarations in the form of a {@link String}
     * @return the list of {@link CssDeclaration} instances
     */
    public static List<CssDeclaration> parsePropertyDeclarations(String propertiesStr) {
        List<CssDeclaration> declarations = new ArrayList<>();
        int openedCommentPos = propertiesStr.indexOf("/*", 0);
        if (openedCommentPos != -1) {
            declarations.addAll(parsePropertyDeclarations(propertiesStr.substring(0, openedCommentPos)));
            int closedCommentPos = propertiesStr.indexOf("*/", openedCommentPos);
            if (closedCommentPos != -1) {
                declarations.addAll(parsePropertyDeclarations(propertiesStr.substring(closedCommentPos + 2, propertiesStr.length())));
            }
        } else {
            int pos = getSemicolonPosition(propertiesStr, 0);
            while (pos != -1) {
                String[] propertySplit = splitCssProperty(propertiesStr.substring(0, pos));
                if (propertySplit != null) {
                    declarations.add(new CssDeclaration(propertySplit[0], propertySplit[1]));
                }
                propertiesStr = propertiesStr.substring(pos + 1);
                pos = getSemicolonPosition(propertiesStr, 0);
            }
            if (!propertiesStr.replaceAll("[\\n\\r\\t ]", "").isEmpty()) {
                String[] propertySplit = splitCssProperty(propertiesStr);
                if (propertySplit != null) {
                    declarations.add(new CssDeclaration(propertySplit[0], propertySplit[1]));
                }
                return declarations;
            }
        }
        return declarations;
    }


    /**
     * Parses a rule set into a list of {@link CssRuleSet} instances.
     * This method returns a {@link List} because a selector can
     * be compound, like "p, div, #navbar".
     *
     * @param selectorStr   the selector
     * @param propertiesStr the properties
     * @return the resulting list of {@link CssRuleSet} instances
     */
    public static List<CssRuleSet> parseRuleSet(String selectorStr, String propertiesStr) {
        List<CssDeclaration> declarations = parsePropertyDeclarations(propertiesStr);
        List<CssRuleSet> ruleSets = new ArrayList<>();

        //check for rules like p, {…}
        String[] selectors = selectorStr.split(",");
        for (int i = 0; i < selectors.length; i++) {
            selectors[i] = CssUtils.removeDoubleSpacesAndTrim(selectors[i]);
            if (selectors[i].length() == 0)
                return ruleSets;
        }
        for (String currentSelectorStr : selectors) {
            try {
                ruleSets.add(new CssRuleSet( new CssSelector(currentSelectorStr), declarations));
            } catch (Exception exc) {
                logger.error(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.ERROR_PARSING_CSS_SELECTOR,
                        currentSelectorStr), exc);
                //if any separated selector has errors, all others become invalid.
                //in this case we just clear map, it is the easies way to support this.
                declarations.clear();
                return ruleSets;
            }
        }

        return ruleSets;
    }

    /**
     * Splits CSS properties into an array of {@link String} values.
     *
     * @param property the properties
     * @return the array of property values
     */
    private static String[] splitCssProperty(String property) {
        if (property.trim().isEmpty()) {
            return null;
        }
        String[] result = new String[2];
        int position = property.indexOf(":");
        if (position < 0) {
            logger.error(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    property.trim()));
            return null;
        }
        result[0] = property.substring(0, position);
        result[1] = property.substring(position + 1);

        return result;
    }

    /**
     * Gets the semicolon position.
     *
     * @param propertiesStr the properties
     * @param fromIndex     the from index
     * @return the semicolon position
     */
    private static int getSemicolonPosition(String propertiesStr, int fromIndex) {
        int semiColonPos = propertiesStr.indexOf(";", fromIndex);
        int closedBracketPos = propertiesStr.indexOf(")", semiColonPos + 1);
        int openedBracketPos = propertiesStr.indexOf("(", fromIndex);
        if (semiColonPos != -1 && openedBracketPos < semiColonPos && closedBracketPos > 0) {
            int nextOpenedBracketPos = openedBracketPos;
            do {
                openedBracketPos = nextOpenedBracketPos;
                nextOpenedBracketPos = propertiesStr.indexOf("(", openedBracketPos + 1);
            } while (nextOpenedBracketPos < closedBracketPos && nextOpenedBracketPos > 0);
        }
        if (semiColonPos != -1 && semiColonPos > openedBracketPos && semiColonPos < closedBracketPos) {
            return getSemicolonPosition(propertiesStr, closedBracketPos + 1);
        }
        return semiColonPos;
    }
}
