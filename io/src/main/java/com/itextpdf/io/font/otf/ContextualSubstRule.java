package com.itextpdf.io.font.otf;

public abstract class ContextualSubstRule {
    /**
     * @return length of the context glyph sequence defined by this rule.
     */
    public abstract int getContextLength();

    /**
     * @return an array of <code>SubstLookupRecord</code>. Each record specifies a position in the context glyph
     * sequence and a LookupListIndex to the substitution lookup that is applied at that position.
     */
    public abstract SubstLookupRecord[] getSubstLookupRecords();

    /**
     * Checks if glyph line element matches element from input sequence of the rule.
     * <br><br>
     * NOTE: rules do not contain the first element of the input sequence, the first element is defined by rule
     * position in substitution table. Therefore atIdx shall not be 0.
     * @param atIdx index in the rule sequence. Shall be: 0 < atIdx < ContextualSubstRule.getContextLength().
     */
    public abstract boolean isGlyphMatchesInput(int glyphId, int atIdx);


    /**
     * @return length of the lookahead context glyph sequence defined by this rule.
     */
    public int getLookaheadContextLength() {
        return 0;
    }
    /**
     * @return length of the backtrack context glyph sequence defined by this rule.
     */
    public int getBacktrackContextLength() {
        return 0;
    }
    /**
     * Checks if glyph line element matches element from lookahead sequence of the rule.
     * @param atIdx index in rule sequence. Shall be: 0 <= atIdx < ContextualSubstRule.getLookaheadContextLength().
     */
    public boolean isGlyphMatchesLookahead(int glyphId, int atIdx) {
        return false;
    }
    /**
     * Checks if glyph line element matches element from backtrack sequence of the rule.
     * @param atIdx index in rule sequence. Shall be: 0 <= atIdx < ContextualSubstRule.getBacktrackContextLength().
     */
    public boolean isGlyphMatchesBacktrack(int glyphId, int atIdx) {
        return false;
    }
}
