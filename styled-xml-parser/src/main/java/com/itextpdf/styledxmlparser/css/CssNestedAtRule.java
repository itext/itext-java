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
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to store a nested CSS at-rule
 * Nested at-rules are a subset of nested statements, which can be used
 * as a statement of a style sheet as well as inside of conditional group rules.
 */
public class CssNestedAtRule extends CssAtRule {

    /** The rule parameters. */
    private String ruleParameters;

    /** The body. */
    protected List<CssStatement> body;

    /**
     * Creates a {@link CssNestedAtRule} instance
     * with an empty body.
     * @param ruleName the rule name
     * @param ruleParameters the rule parameters
     */
    public CssNestedAtRule(String ruleName, String ruleParameters) {
        super(ruleName);
        this.ruleParameters = ruleParameters;
        this.body = new ArrayList<>();
    }

    /**
     * Adds a CSS statement to body.
     *
     * @param statement a CSS statement
     */
    public void addStatementToBody(CssStatement statement) {
        this.body.add(statement);
    }

    /**
     * Adds CSS statements to the body.
     *
     * @param statements a list of CSS statements
     */
    public void addStatementsToBody(Collection<CssStatement> statements) {
        this.body.addAll(statements);
    }

    /**
     * Adds the body CSS declarations.
     *
     * @param cssDeclarations a list of CSS declarations
     */
    public void addBodyCssDeclarations(List<CssDeclaration> cssDeclarations) {
        // ignore by default
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssStatement#getCssRuleSets(com.itextpdf.styledxmlparser.html.node.INode, com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription)
     */
    @Override
    public List<CssRuleSet> getCssRuleSets(INode node, MediaDeviceDescription deviceDescription) {
        List<CssRuleSet> result = new ArrayList<>();
        for (CssStatement childStatement : body) {
            result.addAll(childStatement.getCssRuleSets(node, deviceDescription));
        }
        return result;
    }

    /**
     * Gets the list of CSS statements.
     *
     * @return the list of CSS statements
     */
    public List<CssStatement> getStatements() {
        return body;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormatUtil.format("@{0} {1} ", ruleName, ruleParameters));
        sb.append("{");
        sb.append("\n");
        for (int i = 0; i < body.size(); i++) {
            sb.append("    ");
            sb.append(body.get(i).toString().replace("\n", "\n    "));
            if (i != body.size() - 1) {
                sb.append("\n");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    public String getRuleParameters() {
        return ruleParameters;
    }

}
