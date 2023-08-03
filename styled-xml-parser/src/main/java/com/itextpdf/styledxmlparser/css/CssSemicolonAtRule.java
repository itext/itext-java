/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;

/**
 * A {@link CssAtRule} implementation.
 */
public class CssSemicolonAtRule extends CssAtRule {

    /** The rule parameters. */
    private String ruleParams;

    /**
     * Creates a new {@link CssSemicolonAtRule} instance.
     *
     * @param ruleDeclaration the rule declaration
     */
    public CssSemicolonAtRule(String ruleDeclaration) {
        super(CssNestedAtRuleFactory.extractRuleNameFromDeclaration(ruleDeclaration.trim()));
        this.ruleParams = ruleDeclaration.trim().substring(ruleName.length()).trim();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MessageFormatUtil.format("@{0} {1};", ruleName, ruleParams);
    }
}
