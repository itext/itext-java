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
package com.itextpdf.io.font.otf;

public abstract class ContextualRule {

    /**
     * Gets the length of the context glyph sequence defined by this rule
     * @return length of the context
     */
    public abstract int getContextLength();

    /**
     * Checks if glyph line element matches element from input sequence of the rule.
     * <br><br>
     * NOTE: rules do not contain the first element of the input sequence, the first element is defined by rule
     * position in substitution table. Therefore atIdx shall not be 0.
     *
     * @param glyphId glyph code id
     * @param atIdx   index in the rule sequence. Shall be: 0 &lt; atIdx &lt; {@link ContextualSubstRule#getContextLength}
     * @return {@code true} if glyph matches element
     */
    public abstract boolean isGlyphMatchesInput(int glyphId, int atIdx);

    /**
     * Gets the length of the lookahead context glyph sequence defined by this rule
     * @return length of the lookahead context
     */
    public int getLookaheadContextLength() {
        return 0;
    }

    /**
     * Gets the length of the backtrack context glyph sequence defined by this rule
     * @return length of the backtrack context
     */
    public int getBacktrackContextLength() {
        return 0;
    }

    /**
     * Checks if glyph line element matches element from lookahead sequence of the rule.
     *
     * @param glyphId glyph code id
     * @param atIdx   index in rule sequence. Shall be: 0 &lt;= atIdx &lt; {@link ContextualSubstRule#getLookaheadContextLength()}
     * @return {@code true} if glyph matches element from lookahead sequence
     */
    public boolean isGlyphMatchesLookahead(int glyphId, int atIdx) {
        return false;
    }

    /**
     * Checks if glyph line element matches element from backtrack sequence of the rule.
     *
     * @param glyphId glyph code id
     * @param atIdx   index in rule sequence. Shall be: 0 &lt;= atIdx &lt; {@link ContextualSubstRule#getBacktrackContextLength()}
     * @return {@code true} if glyph matches element from backtrack sequence
     */
    public boolean isGlyphMatchesBacktrack(int glyphId, int atIdx) {
        return false;
    }
}
