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
package com.itextpdf.styledxmlparser.css.parse.syntax;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssNestedAtRule;
import com.itextpdf.styledxmlparser.css.CssNestedAtRuleFactory;
import com.itextpdf.styledxmlparser.css.CssRuleName;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.CssSemicolonAtRule;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.parse.CssRuleSetParser;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.resolver.resource.UriResolver;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * State machine that will parse content into a style sheet.
 */
public final class CssParserStateController {

    /** The current state. */
    private IParserState currentState;

    /** Indicates if the current rule is supported. */
    //Hashed value
    private boolean isCurrentRuleSupported = true;

    /** The previous active state (excluding comments). */
    private IParserState previousActiveState;

    /** A buffer to store temporary results. */
    private StringBuilder buffer = new StringBuilder();

    /** The current selector. */
    private String currentSelector;

    /** The style sheet. */
    private CssStyleSheet styleSheet;

    /** The nested At-rules. */
    private Stack<CssNestedAtRule> nestedAtRules;

    /** The stored properties without selector. */
    private Stack<List<CssDeclaration>> storedPropertiesWithoutSelector;

    /** Set of the supported rules. */
    private static final Set<String> SUPPORTED_RULES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            CssRuleName.MEDIA, CssRuleName.PAGE,
            CssRuleName.TOP_LEFT_CORNER, CssRuleName.TOP_LEFT, CssRuleName.TOP_CENTER, CssRuleName.TOP_RIGHT, CssRuleName.TOP_RIGHT_CORNER,
            CssRuleName.BOTTOM_LEFT_CORNER, CssRuleName.BOTTOM_LEFT, CssRuleName.BOTTOM_CENTER, CssRuleName.BOTTOM_RIGHT, CssRuleName.BOTTOM_RIGHT_CORNER,
            CssRuleName.LEFT_TOP, CssRuleName.LEFT_MIDDLE, CssRuleName.LEFT_BOTTOM,
            CssRuleName.RIGHT_TOP, CssRuleName.RIGHT_MIDDLE, CssRuleName.RIGHT_BOTTOM,
            CssRuleName.FONT_FACE
    )));

    /** Set of conditional group rules. */
    private static final Set<String> CONDITIONAL_GROUP_RULES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            CssRuleName.MEDIA
    )));

    /** The comment start state. */
    private final IParserState commentStartState;

    /** The commend end state. */
    private final IParserState commendEndState;

    /** The commend inner state. */
    private final IParserState commendInnerState;

    /** The unknown state. */
    private final IParserState unknownState;

    /** The rule state. */
    private final IParserState ruleState;

    /** The properties state. */
    private final IParserState propertiesState;

    /** The conditional group at rule block state. */
    private final IParserState conditionalGroupAtRuleBlockState;

    /** The At-rule block state. */
    private final IParserState atRuleBlockState;

    /** The URI resolver. */
    private UriResolver uriResolver;

    /**
     * Creates a new {@link CssParserStateController} instance.
     */
    public CssParserStateController() {
        this("");
    }

    /**
     * Creates a new {@link CssParserStateController} instance.
     *
     * @param baseUrl the base URL
     */
    public CssParserStateController(String baseUrl) {
        if (baseUrl != null && baseUrl.length() > 0) {
            this.uriResolver = new UriResolver(baseUrl);
        }
        styleSheet = new CssStyleSheet();
        nestedAtRules = new Stack<>();
        storedPropertiesWithoutSelector = new Stack<>();

        commentStartState = new CommentStartState(this);
        commendEndState = new CommentEndState(this);
        commendInnerState = new CommentInnerState(this);
        unknownState = new UnknownState(this);
        ruleState = new RuleState(this);
        propertiesState = new PropertiesState(this);
        atRuleBlockState = new AtRuleBlockState(this);
        conditionalGroupAtRuleBlockState = new ConditionalGroupAtRuleBlockState(this);

        currentState = unknownState;
    }

    /**
     * Process a character using the current state.
     *
     * @param ch the character
     */
    public void process(char ch) {
        currentState.process(ch);
    }

    /**
     * Gets the resulting style sheet.
     *
     * @return the resulting style sheet
     */
    public CssStyleSheet getParsingResult() {
        return styleSheet;
    }

    /**
     * Appends a character to the buffer.
     *
     * @param ch the character
     */
    void appendToBuffer(char ch) {
        buffer.append(ch);
    }

    /**
     * Gets the contents of the buffer.
     *
     * @return the buffer contents
     */
    String getBufferContents() {
        return buffer.toString();
    }

    /**
     * Resets the buffer.
     */
    void resetBuffer() {
        buffer.setLength(0);
    }

    /**
     * Enter the previous active state.
     */
    void enterPreviousActiveState() {
        setState(previousActiveState);
    }

    /**
     * Enter the comment start state.
     */
    void enterCommentStartState() {
        saveActiveState();
        setState(commentStartState);
    }

    /**
     * Enter the comment end state.
     */
    void enterCommentEndState() {
        setState(commendEndState);
    }

    /**
     * Enter the comment inner state.
     */
    void enterCommentInnerState() {
        setState(commendInnerState);
    }

    /**
     * Enter the rule state.
     */
    void enterRuleState() {
        setState(ruleState);
    }

    /**
     * Enter the unknown state if nested blocks are finished.
     */
    void enterUnknownStateIfNestedBlocksFinished() {
        if (nestedAtRules.size() == 0) {
            setState(unknownState);
        } else {
            enterRuleStateBasedOnItsType();
        }
    }

    /**
     * Enter the rule state, based on whether the current state is unsupported or conditional.
     */
    void enterRuleStateBasedOnItsType() {
        if (currentAtRuleIsConditionalGroupRule()) {
            enterConditionalGroupAtRuleBlockState();
        } else {
            enterAtRuleBlockState();
        }
    }

    /**
     * Enter the unknown state.
     */
    void enterUnknownState() {
        setState(unknownState);
    }

    /**
     * Enter the At-rule block state.
     */
    void enterAtRuleBlockState() {
        setState(atRuleBlockState);
    }

    /**
     * Enter the conditional group At-rule block state.
     */
    void enterConditionalGroupAtRuleBlockState() {
        setState(conditionalGroupAtRuleBlockState);
    }

    /**
     * Enter the properties state.
     */
    void enterPropertiesState() {
        setState(propertiesState);
    }

    /**
     * Store the current selector.
     */
    void storeCurrentSelector() {
        currentSelector = buffer.toString();
        buffer.setLength(0);
    }

    /**
     * Store the current properties.
     */
    void storeCurrentProperties() {
        if (isCurrentRuleSupported) {
            processProperties(currentSelector, buffer.toString());
        }
        currentSelector = null;
        buffer.setLength(0);
    }

    /**
     * Store the current properties without selector.
     */
    void storeCurrentPropertiesWithoutSelector() {
        if (isCurrentRuleSupported) {
            processProperties(buffer.toString());
        }
        buffer.setLength(0);
    }

    /**
     * Store the semicolon At-rule.
     */
    void storeSemicolonAtRule() {
        if (isCurrentRuleSupported) {
            processSemicolonAtRule(buffer.toString());
        }
        buffer.setLength(0);
    }

    /**
     * Finish the At-rule block.
     */
    void finishAtRuleBlock() {
        List<CssDeclaration> storedProps = storedPropertiesWithoutSelector.pop();
        CssNestedAtRule atRule = nestedAtRules.pop();
        if (isCurrentRuleSupported) {
            processFinishedAtRuleBlock(atRule);
            if (!storedProps.isEmpty()) {
                atRule.addBodyCssDeclarations(storedProps);
            }
        }
        isCurrentRuleSupported = isCurrentRuleSupported();
        buffer.setLength(0);
    }

    /**
     * Push the block preceding At-rule.
     */
    void pushBlockPrecedingAtRule() {
        nestedAtRules.push(CssNestedAtRuleFactory.createNestedRule(buffer.toString()));
        storedPropertiesWithoutSelector.push(new ArrayList<CssDeclaration>());
        isCurrentRuleSupported = isCurrentRuleSupported();
        buffer.setLength(0);
    }

    /**
     * Save the active state.
     */
    private void saveActiveState() {
        previousActiveState = currentState;
    }

    /**
     * Sets the current state.
     *
     * @param state the new state
     */
    private void setState(IParserState state) {
        currentState = state;
    }

    /**
     * Processes the properties.
     *
     * @param selector the selector
     * @param properties the properties
     */
    private void processProperties(String selector, String properties) {
        List<CssRuleSet> ruleSets = CssRuleSetParser.parseRuleSet(selector, properties);
        for (CssRuleSet ruleSet : ruleSets) {
            normalizeDeclarationURIs(ruleSet.getNormalDeclarations());
            normalizeDeclarationURIs(ruleSet.getImportantDeclarations());
        }
        for (CssRuleSet ruleSet : ruleSets) {
            if (nestedAtRules.size() == 0) {
                styleSheet.addStatement(ruleSet);
            } else {
                nestedAtRules.peek().addStatementToBody(ruleSet);
            }
        }
    }

    /**
     * Processes the properties.
     *
     * @param properties the properties
     */
    private void processProperties(String properties) {
        if (storedPropertiesWithoutSelector.size() > 0) {
            List<CssDeclaration> cssDeclarations = CssRuleSetParser.parsePropertyDeclarations(properties);
            normalizeDeclarationURIs(cssDeclarations);
            storedPropertiesWithoutSelector.peek().addAll(cssDeclarations);
        }
    }

    /**
     * Normalizes the declaration URIs.
     *
     * @param declarations the declarations
     */
    private void normalizeDeclarationURIs(List<CssDeclaration> declarations) {
        // This is the case when css has no location and thus urls should not be resolved against base css location
        if (this.uriResolver == null) {
            return;
        }
        for (CssDeclaration declaration : declarations) {
            if (declaration.getExpression().contains("url(")) {
                CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(declaration.getExpression());
                CssDeclarationValueTokenizer.Token token;
                StringBuilder normalizedDeclaration = new StringBuilder();
                while ((token = tokenizer.getNextValidToken()) != null) {
                    String strToAppend;
                    if (token.getType() == CssDeclarationValueTokenizer.TokenType.FUNCTION && token.getValue().startsWith("url(")) {
                        String url = token.getValue().trim();
                        url = url.substring(4, url.length() - 1).trim();
                        if (CssUtils.isBase64Data(url)) {
                            strToAppend = token.getValue().trim();
                        } else {
                            if (url.startsWith("'") && url.endsWith("'") || url.startsWith("\"") && url.endsWith("\"")) {
                                url = url.substring(1, url.length() - 1);
                            }
                            url = url.trim();
                            String finalUrl = url;
                            try {
                                finalUrl = uriResolver.resolveAgainstBaseUri(url).toExternalForm();
                            } catch (MalformedURLException ignored) {
                            }
                            strToAppend = MessageFormatUtil.format("url({0})", finalUrl);
                        }
                    } else {
                        strToAppend = token.getValue();
                    }
                    if (normalizedDeclaration.length() > 0) {
                        normalizedDeclaration.append(' ');
                    }
                    normalizedDeclaration.append(strToAppend);
                }
                declaration.setExpression(normalizedDeclaration.toString());
            }
        }
    }

    /**
     * Processes the semicolon At-rule.
     *
     * @param ruleStr the rule str
     */
    private void processSemicolonAtRule(String ruleStr) {
        CssSemicolonAtRule atRule = new CssSemicolonAtRule(ruleStr);
        styleSheet.addStatement(atRule);
    }

    /**
     * Processes the finished At-rule block.
     *
     * @param atRule the at rule
     */
    private void processFinishedAtRuleBlock(CssNestedAtRule atRule) {
        if (nestedAtRules.size() != 0) {
            nestedAtRules.peek().addStatementToBody(atRule);
        } else {
            styleSheet.addStatement(atRule);
        }
    }

    /**
     * Checks if is current rule is supported.
     *
     * @return true, if the current rule is supported
     */
    private boolean isCurrentRuleSupported() {
        boolean isSupported = nestedAtRules.isEmpty() || SUPPORTED_RULES.contains(nestedAtRules.peek().getRuleName());
        if (!isSupported) {
            LoggerFactory.getLogger(getClass()).error(MessageFormatUtil.format(LogMessageConstant.RULE_IS_NOT_SUPPORTED, nestedAtRules.peek().getRuleName()));
        }
        return isSupported;
    }

    /**
     * Checks if the current At-rule is a conditional group rule (or if it's unsupported).
     *
     * @return true, if the current At-rule is unsupported or conditional
     */
    private boolean currentAtRuleIsConditionalGroupRule() {
        return !isCurrentRuleSupported || (nestedAtRules.size() > 0 && CONDITIONAL_GROUP_RULES.contains(nestedAtRules.peek().getRuleName()));
    }
}
