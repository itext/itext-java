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

import com.itextpdf.io.util.MessageFormatUtil;
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
