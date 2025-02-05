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

import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class to store a CSS rule set.
 */
public class CssRuleSet extends CssStatement {

    /** Pattern to match "important" in a rule declaration. */
    private static final Pattern IMPORTANT_MATCHER = Pattern.compile(".*!\\s*important$");

    /** The CSS selector. */
    private ICssSelector selector;
    
    /** The normal CSS declarations. */
    private List<CssDeclaration> normalDeclarations;
    
    /** The important CSS declarations. */
    private List<CssDeclaration> importantDeclarations;

    /**
     * Creates a new {@link CssRuleSet} from selector and raw list of declarations.
     * The declarations are split into normal and important under the hood.
     * To construct the {@link CssRuleSet} instance from normal and important declarations, see
     * {@link #CssRuleSet(ICssSelector, List, List)}
     *
     * @param selector the CSS selector
     * @param declarations the CSS declarations
     */
    public CssRuleSet(ICssSelector selector, List<CssDeclaration> declarations) {
        this.selector = selector;
        this.normalDeclarations = new ArrayList<>();
        this.importantDeclarations = new ArrayList<>();
        splitDeclarationsIntoNormalAndImportant(declarations, normalDeclarations, importantDeclarations);
    }

    public CssRuleSet(ICssSelector selector, List<CssDeclaration> normalDeclarations, List<CssDeclaration> importantDeclarations) {
        this.selector = selector;
        this.normalDeclarations = normalDeclarations;
        this.importantDeclarations = importantDeclarations;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssStatement#getCssRuleSets(com.itextpdf.styledxmlparser.html.node.INode, com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription)
     */
    @Override
    public List<CssRuleSet> getCssRuleSets(INode element, MediaDeviceDescription deviceDescription) {
        if (selector.matches(element)) {
            return Collections.singletonList(this);
        } else {
            return super.getCssRuleSets(element, deviceDescription);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(selector.toString());
        sb.append(" {\n");
        for (int i = 0; i < normalDeclarations.size(); i++) {
            if (i > 0) {
                sb.append(";").append("\n");
            }
            CssDeclaration declaration = normalDeclarations.get(i);
            sb.append("    ").append(declaration.toString());
        }
        for (int i = 0; i < importantDeclarations.size(); i++) {
            if (i > 0 || normalDeclarations.size() > 0) {
                sb.append(";").append("\n");
            }
            CssDeclaration declaration = importantDeclarations.get(i);
            sb.append("    ").append(declaration.toString()).append(" !important");
        }
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Gets the CSS selector.
     *
     * @return the CSS selector
     */
    public ICssSelector getSelector() {
        return selector;
    }

    /**
     * Gets the normal CSS declarations.
     *
     * @return the normal declarations
     */
    public List<CssDeclaration> getNormalDeclarations() {
        return normalDeclarations;
    }

    /**
     * Gets the important CSS declarations.
     *
     * @return the important declarations
     */
    public List<CssDeclaration> getImportantDeclarations() {
        return importantDeclarations;
    }

    /**
     * Split CSS declarations into normal and important CSS declarations.
     *
     * @param declarations the declarations
     */
    private static void splitDeclarationsIntoNormalAndImportant(List<CssDeclaration> declarations, List<CssDeclaration> normalDeclarations, List<CssDeclaration> importantDeclarations) {
        for (CssDeclaration declaration : declarations) {
            int exclIndex = declaration.getExpression().indexOf('!');
            if (exclIndex > 0 && IMPORTANT_MATCHER.matcher(declaration.getExpression()).matches()) {
                importantDeclarations.add(new CssDeclaration(declaration.getProperty(), declaration.getExpression().substring(0, exclIndex).trim()));
            } else {
                normalDeclarations.add(declaration);
            }
        }
    }

}
