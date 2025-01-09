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

import com.itextpdf.layout.font.Range;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store a CSS font face At rule.
 */
public class CssFontFaceRule extends CssNestedAtRule {

    /**
     * Properties in the form of a list of CSS declarations.
     */
    private List<CssDeclaration> properties;

    /**
     * Instantiates a new CSS font face rule.
     */
    public CssFontFaceRule() {
        super(CssRuleName.FONT_FACE, "");
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public List<CssDeclaration> getProperties() {
        if (properties==null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(properties);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssNestedAtRule#addBodyCssDeclarations(java.util.List)
     */
    @Override
    public void addBodyCssDeclarations(List<CssDeclaration> cssDeclarations) {
        properties = new ArrayList<>(cssDeclarations);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssNestedAtRule#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(getRuleName()).append(" {").append("\n");
        for (CssDeclaration declaration : getProperties()) {
            sb.append("    ");
            sb.append(declaration);
            sb.append(";\n");
        }
        sb.append("}");
        return sb.toString();
    }

    public Range resolveUnicodeRange() {
        Range range = null;
        for (CssDeclaration descriptor : getProperties()) {
            if ("unicode-range".equals(descriptor.getProperty())) {
                range = CssUtils.parseUnicodeRange(descriptor.getExpression());
            }
        }
        return range;
    }
}
