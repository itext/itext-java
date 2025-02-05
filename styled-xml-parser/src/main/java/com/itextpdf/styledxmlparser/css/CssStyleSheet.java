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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.CssRuleSetComparator;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.ShorthandResolverFactory;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;
import com.itextpdf.styledxmlparser.node.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that stores all the CSS statements, and thus acts as a CSS style sheet.
 */
public class CssStyleSheet {

    /** The list of CSS statements. */
    private List<CssStatement> statements;

    /**
     * Creates a new {@link CssStyleSheet} instance.
     */
    public CssStyleSheet() {
        statements = new ArrayList<>();
    }

    /**
     * Adds a CSS statement to the style sheet.
     *
     * @param statement the CSS statement
     */
    public void addStatement(CssStatement statement) {
        statements.add(statement);
    }

    /**
     * Append another CSS style sheet to this one.
     *
     * @param anotherCssStyleSheet the other CSS style sheet
     */
    public void appendCssStyleSheet(CssStyleSheet anotherCssStyleSheet) {
        statements.addAll(anotherCssStyleSheet.statements);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CssStatement statement : statements) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(statement.toString());
        }
        return sb.toString();
    }

    /**
     * Gets the CSS statements of this style sheet.
     *
     * @return the CSS statements
     */
    public List<CssStatement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    /**
     * Gets the CSS declarations.
     *
     * @param node the node
     * @param deviceDescription the media device description
     * @return the CSS declarations
     */
    public List<CssDeclaration> getCssDeclarations(INode node, MediaDeviceDescription deviceDescription) {
        List<CssRuleSet> ruleSets = getCssRuleSets(node, deviceDescription);
        Map<String, CssDeclaration> declarations = new LinkedHashMap<>();
        for (CssRuleSet ruleSet : ruleSets) {
            populateDeclarationsMap(ruleSet.getNormalDeclarations(), declarations);
        }
        for (CssRuleSet ruleSet : ruleSets) {
            populateDeclarationsMap(ruleSet.getImportantDeclarations(), declarations);
        }
        return new ArrayList<>(declarations.values());
    }

    /**
     * Gets the CSS declarations.
     *
     * @param ruleSets list of css rule sets
     * @return the CSS declarations
     */
    public static Map<String, String> extractStylesFromRuleSets(List<CssRuleSet> ruleSets) {
        Map<String, CssDeclaration> declarations = new LinkedHashMap<>();
        for (CssRuleSet ruleSet : ruleSets) {
            populateDeclarationsMap(ruleSet.getNormalDeclarations(), declarations);
        }
        for (CssRuleSet ruleSet : ruleSets) {
            populateDeclarationsMap(ruleSet.getImportantDeclarations(), declarations);
        }
        Map<String, String> stringMap = new LinkedHashMap<>();
        for (Map.Entry<String, CssDeclaration> entry : declarations.entrySet()) {
            stringMap.put(entry.getKey(), entry.getValue().getExpression());
        }
        return stringMap;
    }

    /**
     * Populates the CSS declarations map.
     *
     * @param declarations the declarations
     * @param map the map
     */
    private static void populateDeclarationsMap(List<CssDeclaration> declarations, Map<String, CssDeclaration> map) {
        for (CssDeclaration declaration : declarations) {
            IShorthandResolver shorthandResolver = ShorthandResolverFactory.getShorthandResolver(declaration.getProperty());
            if (shorthandResolver == null) {
                putDeclarationInMapIfValid(map, declaration);
            } else {
                List<CssDeclaration> resolvedShorthandProps = shorthandResolver.resolveShorthand(declaration.getExpression());
                populateDeclarationsMap(resolvedShorthandProps, map);
            }
        }
    }

    /**
     * Gets the CSS rule sets.
     *
     * @param node the node
     * @param deviceDescription the device description
     * @return the css rule sets
     */
    public List<CssRuleSet> getCssRuleSets(INode node, MediaDeviceDescription deviceDescription) {
        List<CssRuleSet> ruleSets = new ArrayList<>();
        for (CssStatement statement : statements) {
            ruleSets.addAll(statement.getCssRuleSets(node, deviceDescription));
        }
        Collections.sort(ruleSets, new CssRuleSetComparator());
        return ruleSets;
    }

    /**
     * Puts a declaration in a styles map if the declaration is valid.
     *
     * @param stylesMap the styles map
     * @param cssDeclaration the css declaration
     */
    private static void putDeclarationInMapIfValid(Map<String, CssDeclaration> stylesMap, CssDeclaration cssDeclaration) {
        if (CssDeclarationValidationMaster.checkDeclaration(cssDeclaration)) {
                stylesMap.put(cssDeclaration.getProperty(), cssDeclaration);
        } else {
            Logger logger = LoggerFactory.getLogger(ICssResolver.class);
            logger.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    cssDeclaration));
        }
    }

}
