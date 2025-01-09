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
package com.itextpdf.styledxmlparser.css;

import com.itextpdf.styledxmlparser.css.media.CssMediaRule;
import com.itextpdf.styledxmlparser.css.page.CssMarginRule;
import com.itextpdf.styledxmlparser.css.page.CssPageRule;

/**
 * A factory for creating {@link CssAtRule} objects.
 */
public final class CssAtRuleFactory {
    private CssAtRuleFactory() {
        // do nothing
    }

    /**
     * Create a new {@link CssSemicolonAtRule} object.
     *
     * @param ruleDeclaration the rule declaration
     *
     * @return a {@link CssSemicolonAtRule} instance
     */
    public static CssSemicolonAtRule createSemicolonAtRule(String ruleDeclaration) {
        ruleDeclaration = ruleDeclaration.trim();
        String ruleName = extractRuleNameFromDeclaration(ruleDeclaration);
        String ruleParameters = ruleDeclaration.substring(ruleName.length()).trim();
        if (CssRuleName.IMPORT.equals(ruleName)) {
            return new CssImportAtRule(ruleParameters);
        }
        return new CssSemicolonAtRule(ruleName, ruleParameters);
    }

    /**
     * Creates a new {@link CssNestedAtRule} object.
     *
     * @param ruleDeclaration the rule declaration
     *
     * @return a {@link CssNestedAtRule} instance
     */
    public static CssNestedAtRule createNestedRule(String ruleDeclaration) {
        ruleDeclaration = ruleDeclaration.trim();
        String ruleName = extractRuleNameFromDeclaration(ruleDeclaration);
        String ruleParameters = ruleDeclaration.substring(ruleName.length()).trim();
        //TODO: DEVSIX-2263 consider media rules in SVG
        switch (ruleName) {
            case CssRuleName.MEDIA:
                return new CssMediaRule(ruleParameters);
            case CssRuleName.PAGE:
                return new CssPageRule(ruleParameters);
            case CssRuleName.TOP_LEFT_CORNER:
            case CssRuleName.TOP_LEFT:
            case CssRuleName.TOP_CENTER:
            case CssRuleName.TOP_RIGHT:
            case CssRuleName.TOP_RIGHT_CORNER:
            case CssRuleName.LEFT_TOP:
            case CssRuleName.LEFT_MIDDLE:
            case CssRuleName.LEFT_BOTTOM:
            case CssRuleName.RIGHT_TOP:
            case CssRuleName.RIGHT_MIDDLE:
            case CssRuleName.RIGHT_BOTTOM:
            case CssRuleName.BOTTOM_LEFT_CORNER:
            case CssRuleName.BOTTOM_LEFT:
            case CssRuleName.BOTTOM_CENTER:
            case CssRuleName.BOTTOM_RIGHT:
            case CssRuleName.BOTTOM_RIGHT_CORNER:
                return new CssMarginRule(ruleName);
            case CssRuleName.FONT_FACE:
                return new CssFontFaceRule();
            default:
                return new CssNestedAtRule(ruleName, ruleParameters);
        }
    }

    /**
     * Extracts the rule name from the CSS rule declaration.
     *
     * @param ruleDeclaration the rule declaration
     * @return the rule name
     */
    static String extractRuleNameFromDeclaration(String ruleDeclaration) {
        int spaceIndex = ruleDeclaration.indexOf(' ');
        int colonIndex = ruleDeclaration.indexOf(':');
        int separatorIndex;
        if (spaceIndex == -1) {
            separatorIndex = colonIndex;
        } else if (colonIndex == -1) {
            separatorIndex = spaceIndex;
        } else {
            separatorIndex = Math.min(spaceIndex, colonIndex);
        }
        return separatorIndex == -1 ? ruleDeclaration : ruleDeclaration.substring(0, separatorIndex);
    }
}
