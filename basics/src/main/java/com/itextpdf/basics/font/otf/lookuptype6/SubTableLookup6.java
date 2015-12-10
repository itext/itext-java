package com.itextpdf.basics.font.otf.lookuptype6;

import com.itextpdf.basics.font.otf.ContextualSubstRule;
import com.itextpdf.basics.font.otf.ContextualSubTable;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;
import com.itextpdf.basics.font.otf.OpenTableLookup;
import com.itextpdf.basics.font.otf.OpenTypeFontTableReader;
import java.util.List;

public abstract class SubTableLookup6 extends ContextualSubTable {
    protected SubTableLookup6(OpenTypeFontTableReader openReader, int lookupFlag) {
        super(openReader, lookupFlag);
    }

    @Override
    public ContextualSubstRule getMatchingContextRule(GlyphLine line) {
        if (line.idx >= line.end)
            return null;

        Glyph g = line.glyphs.get(line.idx);
        List<ContextualSubstRule> rules = getSetOfRulesForStartGlyph(g.index);
        for (ContextualSubstRule rule : rules) {
            int lastGlyphIndex = checkIfContextMatch(line, rule);

            if (lastGlyphIndex != -1
                    && checkIfLookaheadContextMatch(line, rule, lastGlyphIndex)
                    && checkIfBacktrackContextMatch(line, rule)) {

                line.start = line.idx;
                line.end = lastGlyphIndex + 1;
                return rule;
            }
        }

        return null;
    }


    /**
     * Checks if given glyph line at the given position matches given rule.
     */
    protected boolean checkIfLookaheadContextMatch(GlyphLine line, ContextualSubstRule rule, int startIdx) {
        int j;
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.line = line;
        gidx.idx = startIdx;
        for (j = 0; j < rule.getLookaheadContextLength(); ++j) {
            gidx.nextGlyph(openReader, lookupFlag);
            if (gidx.glyph == null || !rule.isGlyphMatchesLookahead(gidx.glyph.index, j)) {
                break;
            }
        }
        return j == rule.getLookaheadContextLength();
    }

    /**
     * Checks if given glyph line at the given position matches given rule.
     */
    protected boolean checkIfBacktrackContextMatch(GlyphLine line, ContextualSubstRule rule) {
        int j;
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.line = line;
        gidx.idx = line.idx;
        for (j = 0; j < rule.getBacktrackContextLength(); ++j) {
            gidx.previousGlyph(openReader, lookupFlag);
            if (gidx.glyph == null || !rule.isGlyphMatchesBacktrack(gidx.glyph.index, j)) {
                break;
            }
        }
        return j == rule.getBacktrackContextLength();
    }

}
