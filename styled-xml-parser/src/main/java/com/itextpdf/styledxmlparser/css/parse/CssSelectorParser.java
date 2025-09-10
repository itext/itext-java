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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.selector.item.CssAttributeSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssIdSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPseudoClassSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssPseudoElementSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssSeparatorSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.CssTagSelectorItem;
import com.itextpdf.styledxmlparser.css.selector.item.ICssSelectorItem;
import com.itextpdf.styledxmlparser.exceptions.StyledXmlParserExceptionMessage;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for parsing CSS selectors.
 */
public final class CssSelectorParser {

    /**
     * legacy pseudo-elements (first-line, first-letter, before, after).
     */
    private static final List<String> LEGACY_PSEUDO_ELEMENTS
            = Arrays.asList("first-line", "first-letter", "before", "after");

    /**
     * Private constructor for the {@code CssSelectorParser} utility class.
     * This constructor prevents instantiation of the class since it only provides
     * static methods and is not meant to be instantiated.
     */
    private CssSelectorParser() {
        // Utility class, no instances allowed.
    }

    /**
     * Parses the given CSS selector string into a list of {@link ICssSelectorItem} objects.
     * This method processes the selector string character by character, handling state transitions
     * and escape sequences to generate a structured representation of the selector components.
     *
     * @param selector the CSS selector string to be parsed
     *
     * @return a list of {@link ICssSelectorItem} objects representing the components of the parsed selector
     */
    public static List<ICssSelectorItem> parseSelectorItems(String selector) {
        List<ICssSelectorItem> selectorItems = new ArrayList<>();
        State state = new NoneState();
        for (int i = 0; i < selector.length(); ++i) {
            char c = selector.charAt(i);
            State nextState;
            //process escape sequence if necessary
            boolean isEscaped = false;
            if (c == '\\') {
                Tuple2<Integer, Character> escapedSeq = processEscape(i, selector);
                i = (int) escapedSeq.getFirst();
                c = (char) escapedSeq.getSecond();
                isEscaped = true;
                //Only tags can start with an escaped character
                nextState = trySwitchToTagState(state);
            } else {
                nextState = trySwitchState(state, c);
            }

            if (state != nextState) {
                state.process(selectorItems);
                state = nextState;
            }
            state.addChar(c, isEscaped);
        }
        state.process(selectorItems);
        return selectorItems;
    }

    /**
     * Processes a possible escape sequence in the given source string, starting from the specified index.
     * This method extracts and decodes an escape sequence based on its hexadecimal representation
     * or returns the character directly if no valid escape sequence is found.
     *
     * @param start the starting index in the source string to begin processing the escape sequence
     * @param source the source string containing the potential escape sequence
     *
     * @return a {@link Tuple2} object where the first element is the index after the processed escape sequence,
     *         and the second element is the decoded character or a replacement character ('\uFFFD') if the escape
     *         sequence is invalid
     */
    private static Tuple2<Integer, Character> processEscape(int start, String source) {
        if (start + 1 >= source.length()) {
            return new Tuple2<>(start, '\uFFFD');
        }
        StringBuilder pendingUnicodeSequence = new StringBuilder();
        int i = start + 1;
        for (; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (isHexDigit(c) && pendingUnicodeSequence.length() < 6) {
                pendingUnicodeSequence.append(c);
            } else {
                break;
            }
        }

        if (pendingUnicodeSequence.length() == 0) {
            // Not a hex sequence, just an escaped character e.g. `\.`
            return new Tuple2<>(i, source.charAt(start + 1));
        }

        int lastConsumedIndex = i - 1;
        int codePoint = Integer.parseInt(pendingUnicodeSequence.toString(), 16);
        // Consume one whitespace character after the sequence if present
        // See CSS Syntax Module Level 3 4.3.7
        if (i < source.length() && Character.isWhitespace(source.charAt(i))) {
            lastConsumedIndex = i;
        }
        if (Character.isValidCodePoint(codePoint) && codePoint != 0) {
            return new Tuple2<>(lastConsumedIndex, (char) codePoint);
        } else {
            return new Tuple2<>(lastConsumedIndex, '\uFFFD');
        }
    }

    /**
     * Determines if the provided character is a valid hexadecimal digit.
     *
     * @param c the character to check
     *
     * @return true if the character is a valid hexadecimal digit, otherwise false
     */
    private static boolean isHexDigit(char c) {
        return (47 < c && c < 58) || (64 < c && c < 71) || (96 < c && c < 103);
    }

    /**
     * Attempts to switch the current parser state to {@link TagState} based on the provided current state.
     * If the current state is an instance of {@link NoneState} or {@link SeparatorState}, a new {@link TagState}
     * instance is created and returned. Otherwise, the original state is returned unchanged.
     *
     * @param state the current state of the parser
     *
     * @return the new state, which may either remain the same or switch to {@link TagState}
     */
    private static State trySwitchToTagState(State state) {
        if (state instanceof NoneState || state instanceof SeparatorState) {
            return new TagState();
        }
        return state;
    }

    /**
     * Attempts to switch the current state based on the provided character.
     * If the current state is not ready for a switch, the original state is returned.
     * Determines connections between states.
     *
     * @param state the current state of the parser
     * @param c the character that determines whether and to which state to switch
     *
     * @return the new state after evaluating the character; if no switch is performed,
     *         the original state is returned
     */
    private static State trySwitchState(State state, char c) {
        if (!state.isReadyForSwitch(c)) {
            return state;
        }
        switch (c) {
            case '.':
                return new ClassState();
            case '#':
                return new IdState();
            case ':':
                return new PseudoState();
            case '[':
                return new AttributeState();
            case ' ':
            case '+':
            case '>':
            case '~':
            case ',':
            case '|':
                return new SeparatorState();
            case '*':
                return new TagState();
            default:
                return state instanceof SeparatorState || state instanceof NoneState ? new TagState() : state;
        }
    }

    /**
     * Represents the state of a CSS selector parser. Each state defines a specific behavior for
     * processing characters, transitioning between states, and modifying the selector items list.
     * This interface is intended to be implemented by various state types, each accounting for
     * different selector parsing behaviors such as handling tags, separators, or escape sequences.
     */
    private interface State {

        /**
         * Determines whether the current state is ready to transition to another state
         * based on the provided character.
         *
         * @param c the character that is being evaluated to decide state transition
         * @return true if the state is ready to switch to a new state, false otherwise
         */
        boolean isReadyForSwitch(char c);

        /**
         * Adds a character to the current state's processing logic.
         *
         * @param c the character to be added
         * @param isEscaped true if the character is escaped, false otherwise
         */
        void addChar(char c, boolean isEscaped);

        /**
         * Processes a list of CSS selector items. The implementation of this method
         * performs operations specific to the current state of the CSS selector parser,
         * which can include modifying, validating, or analyzing the provided selector items.
         *
         * @param selectorItems the list of CSS selector items to be processed or modified
         */
        void process(List<ICssSelectorItem> selectorItems);
    }

    /**
     * Represents a "None" state in the {@code CssSelectorParser}. This state acts as a default or
     * placeholder state where no specific processing or state changes occur. It adheres to the
     * {@code State} interface but does not modify any selector items or perform any transitions.
     */
    private static final class NoneState implements State {
        @Override
        public boolean isReadyForSwitch(char c) {
            return true;
        }

        @Override
        public void addChar(char c, boolean isEscaped) {
            //Nothing to do here
        }

        @Override
        public void process(List<ICssSelectorItem> selectorItems) {
            //Nothing to do here
        }
    }

    /**
     * Represents a state in the CSS selector parser responsible for processing tag selectors.
     * This state accumulates characters to define a tag name and, upon processing, creates
     * a {@link CssTagSelectorItem} to add to the selector items list.
     * <p>
     * This is a private static inner class of the CSS selector parser and implements the {@link State}
     * interface to define specific behaviors for handling tag selector components in a CSS string.
     */
    private static final class TagState implements State {
        private final StringBuilder data = new StringBuilder();
        @Override
        public boolean isReadyForSwitch(char c) {
            return true;
        }

        @Override
        public void addChar(char c, boolean isEscaped) {
            data.append(c);
        }

        @Override
        public void process(List<ICssSelectorItem> selectorItems) {
            if (!selectorItems.isEmpty() && !(selectorItems.get(selectorItems.size() - 1) instanceof CssSeparatorSelectorItem)) {
                throw new IllegalArgumentException(MessageFormatUtil.format(
                        StyledXmlParserExceptionMessage.INVALID_SELECTOR_STRING, data.toString()));
            }
            selectorItems.add(new CssTagSelectorItem(data.toString()));
        }
    }

    /**
     * Represents a state in the CSS selector parser responsible for processing combinators and separators.
     * It is designed to handle the state where a separator character (e.g., space, comma) is encountered
     * during the parsing process. This state aids in managing validation, transitioning, and the placement
     * of separators within the parsed CSS selectors.
     * <p>
     * An instance of SeparatorState implements the {@code State} interface and therefore defines the behavior
     * for processing characters, validating transitions, and modifying the list of selector items.
     */
    private static final class SeparatorState implements State {
        private char data = '\0';
        @Override
        public boolean isReadyForSwitch(char c) {
            return true;
        }

        @Override
        public void addChar(char c, boolean isEscaped) {
            if (data != '\0') {
                throw new IllegalArgumentException(MessageFormatUtil
                    .format(StyledXmlParserExceptionMessage.INVALID_SELECTOR_STRING, "" + data + c));
            }
            data = c;
        }

        /**
         * Processes a list of CSS selector items by validating and updating the state
         * of separator characters (e.g., spaces, commas) within the selector list.
         * <p>
         * If the list is empty, an exception is thrown indicating an invalid initial state.
         * Handles proper appending or replacement of separator items and ensures no
         * invalid consecutive separators are added.
         *
         * @param selectorItems the list of {@code ICssSelectorItem} objects representing
         *                      the current state of CSS selectors being parsed and processed.
         *                      Must not be empty.
         *
         * @throws IllegalArgumentException if the list is empty
         *                                  or if invalid consecutive separators are encountered.
         */
        @Override
        public void process(List<ICssSelectorItem> selectorItems) {
            if (selectorItems.isEmpty()) {
                throw new IllegalArgumentException(MessageFormatUtil.format(
                        StyledXmlParserExceptionMessage.INVALID_SELECTOR_STRING, data));
            }
            ICssSelectorItem lastItem = selectorItems.get(selectorItems.size() - 1);
            CssSeparatorSelectorItem curItem = new CssSeparatorSelectorItem(data);
            if (lastItem instanceof CssSeparatorSelectorItem) {
                if (((CssSeparatorSelectorItem) lastItem).getSeparator() == ' ') {
                    selectorItems.set(selectorItems.size() - 1, curItem);
                } else if (((CssSeparatorSelectorItem) lastItem).getSeparator() != ' ' && data != ' ') {
                    throw new IllegalArgumentException(MessageFormatUtil
                            .format(StyledXmlParserExceptionMessage.INVALID_SELECTOR_STRING,
                                    "" + ((CssSeparatorSelectorItem) lastItem).getSeparator() + curItem.getSeparator()));
                }
            } else {
                selectorItems.add(curItem);
            }
        }
    }

    /**
     * Represents a state in the CSS selector parser responsible for processing class selectors.
     * This state accumulates characters to define a class name and, upon processing, creates
     * a {@link CssClassSelectorItem} to add to the selector items list.
     * <p>
     * This is a private static inner class of the CSS selector parser and implements the {@link State}
     * interface to define specific behaviors for handling class selector components in a CSS string.
     */
    private static final class ClassState implements State {
        private final StringBuilder data = new StringBuilder();
        @Override
        public boolean isReadyForSwitch(char c) {
            return true;
        }

        @Override
        public void addChar(char c, boolean isEscaped) {
            data.append(c);
        }

        @Override
        public void process(List<ICssSelectorItem> selectorItems) {
            selectorItems.add(new CssClassSelectorItem(data.toString().substring(1)));
        }
    }

    /**
     * Represents the state of the CSS selector parser for processing ID selectors.
     * This state manages the collection of characters for an ID-based selector
     * and processes it into a {@link CssIdSelectorItem} when the parsing is completed.
     */
    private static final class IdState implements State {
        private final StringBuilder data = new StringBuilder();

        @Override
        public boolean isReadyForSwitch(char c) {
            return true;
        }

        @Override
        public void addChar(char c, boolean isEscaped) {
            data.append(c);
        }

        @Override
        public void process(List<ICssSelectorItem> selectorItems) {
            selectorItems.add(new CssIdSelectorItem(data.toString().substring(1)));
        }
    }


    /**
     * Represents an abstract state for managing the parsing and processing of CSS selector
     * elements that involve function-like syntax. It provides common functionality for
     * handling character-based transitions such as detecting closures and string literals.
     * This class is intended to be extended by other concrete state implementations that
     * require specific behavior for selectors involving functions or brackets.
     */
    private static abstract class FunctionState implements State {
        protected char closure;
        protected boolean inString = false;
        protected boolean isReadyForSwitch = false;

        /**
         * Constructs a new FunctionState object with the specified closure character.
         * The closure character determines the character that signifies the termination
         * of the function-like construct being processed by this state.
         *
         * @param closure the character that represents the closure for this state
         */
        public FunctionState(char closure) {
            this.closure = closure;
        }

        @Override
        public boolean isReadyForSwitch(char c) {
            return isReadyForSwitch;
        }

        /**
         * Updates the internal flags based on the provided character and its escape status.
         * This method detects if the character is part of a string literal or
         * if it marks the closure of the current function-like construct.
         *
         * @param c          the character to be added
         * @param isEscaped  a boolean indicating whether the character is escaped
         */
        @Override
        public void addChar(char c, boolean isEscaped) {
            if ((c == '"' || c == '\'') && !isEscaped) {
                inString = !inString;
            }
            if (c == closure && !isEscaped && !inString) {
                isReadyForSwitch = true;
            }
        }
    }

    /**
     * Represents a state in the {@link CssSelectorParser} responsible for parsing attribute selectors.
     * This class extends {@link FunctionState} and processes attribute selector data, constructing
     * an {@link CssAttributeSelectorItem} instance to be added to the list of selector items.
     * The state operates until a closing bracket (']') is encountered.
     */
    private static final class AttributeState extends FunctionState {
        private final StringBuilder data = new StringBuilder();

        public AttributeState() {
            super(']');
        }

        @Override
        public void process(List<ICssSelectorItem> selectorItems) {
            selectorItems.add(new CssAttributeSelectorItem(data.toString()));
        }

        @Override
        public void addChar(char c, boolean isEscaped) {
            super.addChar(c, isEscaped);
            data.append(c);
        }
    }

    /**
     * Represents a state used in the CSS Selector parsing process for handling pseudo-classes
     * and pseudo-elements. This state monitors character input to determine whether to transition
     * to another state and processes pseudo-selectors, differentiating between legacy pseudo-elements,
     * modern pseudo-elements, and pseudo-classes.
     */
    private static final class PseudoState extends FunctionState {
        private final StringBuilder data = new StringBuilder();
        private boolean isFunction = false;

        public PseudoState() {
            super(')');
        }

        @Override
        public boolean isReadyForSwitch(char c) {
            return (!isFunction && data.length() > 1) || super.isReadyForSwitch(c);
        }

        @Override
        public void addChar(char c, boolean isEscaped) {
            super.addChar(c, isEscaped);
            c = isFunction ? c : Character.toLowerCase(c);
            if (c == '(' && !isEscaped) {
                isFunction = true;
            }
            data.append(c);
        }

        /**
         * Processes a list of CSS selector items to handle pseudo-elements and pseudo-classes.
         * Differentiates between modern pseudo-elements (indicated by "::"), legacy pseudo-elements,
         * and pseudo-classes. Adds the appropriate selector item to the provided list or throws
         * an exception if the pseudo-class is unsupported.
         *
         * @param selectorItems the list of {@link ICssSelectorItem} to which processed pseudo-element
         *                      or pseudo-class selectors will be added
         * @throws IllegalArgumentException if the pseudo-class is unsupported
         */
        @Override
        public void process(List<ICssSelectorItem> selectorItems) {
            String pseudoElement = data.toString();
            if (pseudoElement.startsWith("::")) {
                selectorItems.add(new CssPseudoElementSelectorItem(pseudoElement.substring(2)));
            } else if (pseudoElement.startsWith(":")
                    && LEGACY_PSEUDO_ELEMENTS.contains(pseudoElement.substring(1))) {
                selectorItems.add(new CssPseudoElementSelectorItem(pseudoElement.substring(1)));
            } else {
                ICssSelectorItem pseudoClassSelectorItem
                        = CssPseudoClassSelectorItem.create(pseudoElement.substring(1));
                if (pseudoClassSelectorItem == null) {
                    throw new IllegalArgumentException(
                            MessageFormatUtil.format(StyledXmlParserLogMessageConstant.UNSUPPORTED_PSEUDO_CSS_SELECTOR,
                                    pseudoElement));
                }
                selectorItems.add(pseudoClassSelectorItem);
            }
        }
    }
}
