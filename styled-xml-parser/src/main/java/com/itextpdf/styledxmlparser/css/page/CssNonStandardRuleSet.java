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
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;

import java.util.List;

/**
 * Class for a non standard {@link CssRuleSet}.
 */
class CssNonStandardRuleSet extends CssRuleSet {
    
    /**
     * Creates a new {@link CssNonStandardRuleSet} instance.
     *
     * @param selector the selector
     * @param declarations the declarations
     */
    public CssNonStandardRuleSet(ICssSelector selector, List<CssDeclaration> declarations) {
        super(selector, declarations);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssRuleSet#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNormalDeclarations().size(); i++) {
            if (i > 0) {
                sb.append(";").append("\n");
            }
            CssDeclaration declaration = getNormalDeclarations().get(i);
            sb.append(declaration.toString());
        }
        for (int i = 0; i < getImportantDeclarations().size(); i++) {
            if (i > 0 || getNormalDeclarations().size() > 0) {
                sb.append(";").append("\n");
            }
            CssDeclaration declaration = getImportantDeclarations().get(i);
            sb.append(declaration.toString()).append(" !important");
        }
        return sb.toString();
    }
}
