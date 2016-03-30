package com.itextpdf.io.font.otf;

import java.util.List;

public abstract class ContextualSubTable {
    protected OpenTypeFontTableReader openReader;
    protected int lookupFlag;

    protected ContextualSubTable(OpenTypeFontTableReader openReader, int lookupFlag) {
        this.openReader = openReader;
        this.lookupFlag = lookupFlag;
    }

    /**
     * Gets a most preferable context rule that matches the line at current position. If no matching context rule is found,
     * it returns null.
     * <br><br>
     * NOTE: if matching context rule is found, the <code>GlyphLine.start</code> and <code>GlyphLine.end</code> will be
     * changed in such way, that they will point at start and end of the matching context glyph sequence inside the glyph line.
     * @param line a line, which is to be checked if it matches some context.
     * @return matching context rule or null, if none was found.
     */
    public ContextualSubstRule getMatchingContextRule(GlyphLine line) {
        if (line.idx >= line.end)
            return null;

        Glyph g = line.get(line.idx);
        List<ContextualSubstRule> rules = getSetOfRulesForStartGlyph(g.getCode());
        for (ContextualSubstRule rule : rules) {
            int lastGlyphIndex = checkIfContextMatch(line, rule);
            if (lastGlyphIndex != -1) {
                line.start = line.idx;
                line.end = lastGlyphIndex + 1;
                return rule;
            }
        }

        return null;
    }

    /**
     * Gets a set of rules, which start with given glyph id.
     * @param startId id of the first glyph in the sequence.
     */
    protected abstract List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId);

    /**
     * Checks if given glyph line at the given position matches given rule.
     * @return either index which corresponds to the last glyph of the matching context inside the glyph line if context matches,
     * or -1 if context doesn't match.
     */
    protected int checkIfContextMatch(GlyphLine line, ContextualSubstRule rule) {
        int j;
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.line = line;
        gidx.idx = line.idx;

        //Note, that starting index shall be 1
        for (j = 1; j < rule.getContextLength(); ++j) {
            gidx.nextGlyph(openReader, lookupFlag);
            if (gidx.glyph == null || !rule.isGlyphMatchesInput(gidx.glyph.getCode(), j)) {
                break;
            }
        }

        boolean isMatch = j == rule.getContextLength();
        if (isMatch) {
            return gidx.idx;
        } else {
            return -1;
        }
    }
}
