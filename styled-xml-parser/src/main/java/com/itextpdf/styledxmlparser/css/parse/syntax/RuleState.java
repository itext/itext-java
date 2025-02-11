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

/**
 * {@link IParserState} implementation for the rule state.
 */
class RuleState implements IParserState {

    /**
     * The state machine that parses the CSS.
     */
    private CssParserStateController controller;

    /**
     * Creates a new {@link RuleState} instance.
     *
     * @param controller the state machine that parses the CSS
     */
    RuleState(CssParserStateController controller) {
        this.controller = controller;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.parse.syntax.IParserState#process(char)
     */
    @Override
    public void process(char ch) {
        if (ch == '{') {
            controller.pushBlockPrecedingAtRule();
            controller.enterRuleStateBasedOnItsType();
        } else if (ch == ';') {
            controller.storeSemicolonAtRule();
            controller.enterUnknownState();
        } else {
            controller.appendToBuffer(ch);
        }
    }

}
