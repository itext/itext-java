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
package com.itextpdf.styledxmlparser.css.page;

import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssNestedAtRule;
import com.itextpdf.styledxmlparser.css.CssRuleName;
import com.itextpdf.styledxmlparser.css.CssStatement;
import com.itextpdf.styledxmlparser.css.selector.CssPageSelector;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link CssNestedAtRule} implementation for page rules.
 */
public class CssPageRule extends CssNestedAtRule {

    /** The page selectors. */
    private List<ICssSelector> pageSelectors;

    /**
     * Creates a new {@link CssPageRule} instance.
     *
     * @param ruleParameters the rule parameters
     */
    public CssPageRule(String ruleParameters) {
        super(CssRuleName.PAGE, ruleParameters);
        pageSelectors = new ArrayList<>();

        String[] selectors = ruleParameters.split(",");
        for (int i = 0; i < selectors.length; i++) {
            selectors[i] = CssUtils.removeDoubleSpacesAndTrim(selectors[i]);
        }
        for (String currentSelectorStr : selectors) {
            pageSelectors.add(new CssPageSelector(currentSelectorStr));
        }
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssNestedAtRule#addBodyCssDeclarations(java.util.List)
     */
    @Override
    public void addBodyCssDeclarations(List<CssDeclaration> cssDeclarations) {
        // TODO Due to this for-loop, on toString method call for the CssPageRule instance
        //      all the body declarations will be duplicated for each pageSelector part.
        //      This potentially could lead to a nasty behaviour when declarations will double
        //      for each read-write iteration of the same css-file (however, this use case seems 
        //      to be unlikely to happen). 
        //      Possible solution would be to split single page rule with compound selector into 
        //      several page rules with simple selectors on addition of the page rule to it's parent.
        //
        //      Also, the same concerns this method implementation in CssMarginRule class.
        //      
        //      See CssStyleSheetParserTest#test11 test.
        for (ICssSelector pageSelector : pageSelectors) {
            this.body.add(new CssNonStandardRuleSet(pageSelector, cssDeclarations));
        }
    }  
    
    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssNestedAtRule#addStatementToBody(com.itextpdf.styledxmlparser.css.CssStatement)
     */
    @Override
    public void addStatementToBody(CssStatement statement) {
        if (statement instanceof CssMarginRule) {
            ((CssMarginRule) statement).setPageSelectors(pageSelectors);
        }
        this.body.add(statement);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssNestedAtRule#addStatementsToBody(java.util.Collection)
     */
    @Override
    public void addStatementsToBody(Collection<CssStatement> statements) {
        for (CssStatement statement : statements) {
            addStatementToBody(statement);
        }
    }

}
