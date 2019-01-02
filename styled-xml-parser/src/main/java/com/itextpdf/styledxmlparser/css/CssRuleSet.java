/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.
    
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS
    
    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/
    
    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.
    
    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.
    
    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.
    
    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
    private static final Pattern importantMatcher = Pattern.compile(".*!\\s*important$");

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
            if (exclIndex > 0 && importantMatcher.matcher(declaration.getExpression()).matches()) {
                importantDeclarations.add(new CssDeclaration(declaration.getProperty(), declaration.getExpression().substring(0, exclIndex).trim()));
            } else {
                normalDeclarations.add(declaration);
            }
        }
    }

}
