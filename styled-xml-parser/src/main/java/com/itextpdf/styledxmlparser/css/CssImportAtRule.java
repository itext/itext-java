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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link CssSemicolonAtRule} for {@code import} rule.
 */
public class CssImportAtRule extends CssSemicolonAtRule {
    /**
     * The list of rules which are allowed to be before {@code import} rule declaration in CSS stylesheet.
     */
    public static final Set<String> ALLOWED_RULES_BEFORE = Collections.unmodifiableSet(new HashSet<String>(
            Arrays.asList(CssRuleName.CHARSET, CssRuleName.IMPORT, CssRuleName.LAYER)));

    /**
     * Creates a new {@link CssImportAtRule} instance.
     *
     * @param ruleParameters the rule parameters
     */
    public CssImportAtRule(String ruleParameters) {
        super(CssRuleName.IMPORT, ruleParameters);
    }
}
