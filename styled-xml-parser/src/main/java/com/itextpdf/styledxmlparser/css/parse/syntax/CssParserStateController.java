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
package com.itextpdf.styledxmlparser.css.parse.syntax;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.CssAtRule;
import com.itextpdf.styledxmlparser.css.CssAtRuleFactory;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssImportAtRule;
import com.itextpdf.styledxmlparser.css.CssNestedAtRule;
import com.itextpdf.styledxmlparser.css.CssRuleName;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.CssSemicolonAtRule;
import com.itextpdf.styledxmlparser.css.CssStatement;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.TokenType;
import com.itextpdf.styledxmlparser.css.parse.CssRuleSetParser;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State machine that will parse content into a style sheet.
 */
public final class CssParserStateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CssParserStateController.class);

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


    /** The current state. */
    private IParserState currentState;

    /** Indicates if the current rule is supported. */
    private boolean isCurrentRuleSupported = true;

    /** The previous active state (excluding comments). */
    private IParserState previousActiveState;

    /** A buffer to store temporary results. */
    private final StringBuilder buffer = new StringBuilder();

    /** The current selector. */
    private String currentSelector;

    /** The style sheet. */
    private final CssStyleSheet styleSheet;

    /**
     * The style sheet from import CSS rules. It is used to store styles from import
     * separately to avoid {@link StyledXmlParserLogMessageConstant#IMPORT_MUST_COME_BEFORE}
     * on check whether were styles before import or not.
     */
    private final CssStyleSheet styleSheetFromImport;

    /** The nested At-rules. */
    private final Stack<CssNestedAtRule> nestedAtRules;

    /** The stored properties without selector. */
    private final Stack<List<CssDeclaration>> storedPropertiesWithoutSelector;

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

    /** The resource resolver. */
    private final ResourceResolver resourceResolver;

    /**
     * Creates a new {@link CssParserStateController} instance.
     * @deprecated use {@link #CssParserStateController(String)} constructor
     */
    @Deprecated
    public CssParserStateController() {
        this("");
    }

    /**
     * Creates a new {@link CssParserStateController} instance.
     *
     * @param baseUrl the base URL
     */
    public CssParserStateController(String baseUrl) {
        this((baseUrl == null || baseUrl.isEmpty()) ? null : new ResourceResolver(baseUrl, new NoDuplicatesResourceRetriever()));
    }

    private CssParserStateController(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        styleSheet = new CssStyleSheet();
        styleSheetFromImport = new CssStyleSheet();
        nestedAtRules = new Stack<>();
        storedPropertiesWithoutSelector = new Stack<>();

        commentStartState = new CommentStartState(this);
        commendEndState = new CommentEndState(this);
        commendInnerState = new CommentInnerState(this);
        unknownState = new UnknownState(this);
        ruleState = new RuleState(this);
        propertiesState = new BlockState(this);
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
        CssStyleSheet parsingResult = new CssStyleSheet();
        parsingResult.appendCssStyleSheet(styleSheet);
        parsingResult.appendCssStyleSheet(styleSheetFromImport);
        return parsingResult;
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
        if (isCurrentRuleSupported ) {
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
        nestedAtRules.push(CssAtRuleFactory.createNestedRule(buffer.toString()));
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
            if (nestedAtRules.isEmpty()) {
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
        if (!storedPropertiesWithoutSelector.isEmpty()) {
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
        if (this.resourceResolver == null) {
            return;
        }
        for (CssDeclaration declaration : declarations) {
            if (declaration.getExpression().contains("url(")) {
                normalizeSingleDeclarationURI(declaration);
            }
        }
    }

    private void normalizeSingleDeclarationURI(CssDeclaration declaration) {
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(declaration.getExpression());
        CssDeclarationValueTokenizer.Token token;
        StringBuilder normalizedDeclaration = new StringBuilder();
        while ((token = tokenizer.getNextValidToken()) != null) {
            String strToAppend;
            if (token.getType() == TokenType.FUNCTION && token.getValue().startsWith("url(")) {
                String url = token.getValue().trim();
                url = url.substring(4, url.length() - 1).trim();
                url = CssUtils.extractUnquotedString(url);
                if (CssTypesValidationUtils.isInlineData(url) || url.startsWith("#")) {
                    strToAppend = token.getValue().trim();
                } else {
                    String finalUrl = url;
                    try {
                        finalUrl = resourceResolver.resolveAgainstBaseUri(url).toExternalForm();
                    } catch (MalformedURLException ignored) {
                    }
                    strToAppend = MessageFormatUtil.format("url({0})", finalUrl);
                }
            } else if (token.getType() == TokenType.STRING && token.getStringQuote() != 0) {
                // If we parse string with quotes, save them
                strToAppend = token.getStringQuote() + token.getValue() + token.getStringQuote();
            } else {
                strToAppend = token.getValue();
            }

            if (normalizedDeclaration.length() > 0 && token.getType() != TokenType.COMMA) {
                // Don't add space at the start and before comma
                normalizedDeclaration.append(' ');
            }
            normalizedDeclaration.append(strToAppend);
        }
        declaration.setExpression(normalizedDeclaration.toString());
    }

    /**
     * Processes the semicolon At-rule.
     *
     * @param ruleStr the rule str
     */
    private void processSemicolonAtRule(String ruleStr) {
        CssSemicolonAtRule atRule = CssAtRuleFactory.createSemicolonAtRule(ruleStr);
        if (atRule instanceof CssImportAtRule) {
            boolean isPositionCorrect = true;
            for (CssStatement statement : styleSheet.getStatements()) {
                if (statement instanceof CssAtRule) {
                    String ruleName = ((CssAtRule) statement).getRuleName();
                    if (!CssImportAtRule.ALLOWED_RULES_BEFORE.contains(ruleName)) {
                        isPositionCorrect = false;
                        break;
                    }
                } else {
                    isPositionCorrect = false;
                    break;
                }
            }

            if (isPositionCorrect) {
                if (resourceResolver == null) {
                    LOGGER.error(StyledXmlParserLogMessageConstant.IMPORT_RULE_URL_CAN_NOT_BE_RESOLVED);
                    return;
                }

                String externalCss = CssUtils.extractUrl(atRule.getRuleParams());
                try (InputStream stream = resourceResolver.retrieveResourceAsInputStream(externalCss)) {
                    if (stream != null) {
                        final ResourceResolver newResourceResolver = new ResourceResolver(
                                resourceResolver.resolveAgainstBaseUri(externalCss).toExternalForm(),
                                resourceResolver.getRetriever());
                        CssParserStateController controller = new CssParserStateController(newResourceResolver);
                        CssStyleSheet externalStyleSheet = CssStyleSheetParser.parse(stream, controller);
                        styleSheetFromImport.appendCssStyleSheet(externalStyleSheet);
                    }
                } catch (IOException e) {
                    LOGGER.error(StyledXmlParserLogMessageConstant.UNABLE_TO_PROCESS_EXTERNAL_CSS_FILE, e);
                }
            } else {
                LOGGER.warn(StyledXmlParserLogMessageConstant.IMPORT_MUST_COME_BEFORE);
            }
        } else {
            styleSheet.addStatement(atRule);
        }
    }

    /**
     * Processes the finished At-rule block.
     *
     * @param atRule the at rule
     */
    private void processFinishedAtRuleBlock(CssNestedAtRule atRule) {
        if (nestedAtRules.isEmpty()) {
            styleSheet.addStatement(atRule);
        } else {
            nestedAtRules.peek().addStatementToBody(atRule);
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
            LOGGER.error(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.RULE_IS_NOT_SUPPORTED,
                    nestedAtRules.peek().getRuleName()));
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
