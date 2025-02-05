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
package com.itextpdf.styledxmlparser.css.page;

import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssNestedAtRule;
import com.itextpdf.styledxmlparser.css.selector.CssPageMarginBoxSelector;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link CssNestedAtRule} implementation for margins.
 */
public class CssMarginRule extends CssNestedAtRule {
    
    /** The page selectors. */
    private List<ICssSelector> pageSelectors;

    /**
     * Creates a new {@link CssMarginRule} instance.
     *
     * @param ruleName the rule name
     */
    public CssMarginRule(String ruleName) {
        super(ruleName, "");
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssNestedAtRule#addBodyCssDeclarations(java.util.List)
     */
    @Override
    public void addBodyCssDeclarations(List<CssDeclaration> cssDeclarations) {
        // TODO DEVSIX-6364 Fix the body declarations duplication for each pageSelector part
        for (ICssSelector pageSelector : pageSelectors) {
            this.body.add(new CssNonStandardRuleSet(new CssPageMarginBoxSelector(getRuleName(), pageSelector), cssDeclarations));
        }
    }

    /**
     * Sets the page selectors.
     *
     * @param pageSelectors the new page selectors
     */
    void setPageSelectors(List<ICssSelector> pageSelectors) {
        this.pageSelectors = new ArrayList<>(pageSelectors);
    }
}
