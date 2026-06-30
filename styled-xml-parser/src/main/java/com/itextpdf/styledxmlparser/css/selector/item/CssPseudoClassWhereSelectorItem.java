/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;

import java.util.List;

class CssPseudoClassWhereSelectorItem extends CssPseudoClassForgivingSelectorListSelectorItem {

    CssPseudoClassWhereSelectorItem(List<ICssSelector> selectorList, String argumentsString) {
        super(CommonCssConstants.WHERE, selectorList, argumentsString);
    }

    @Override
    public int getSpecificity() {
        // Per Selectors Level 4: :where() always contributes 0 specificity.
        return 0;
    }

    public static CssPseudoClassWhereSelectorItem createWhereSelectorItem(String arguments) {
        List<ICssSelector> selectors = parseSelectorListWithoutPseudoElements(arguments, true);
        if (selectors == null) {
            return null;
        }
        return new CssPseudoClassWhereSelectorItem(selectors, arguments);
    }
}

