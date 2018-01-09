/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.io.font.otf.lookuptype6;

import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.ContextualSubTable;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.font.otf.OpenTableLookup;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;

import java.util.List;

public abstract class SubTableLookup6 extends ContextualSubTable {

    private static final long serialVersionUID = -7471613803606544198L;

    protected SubTableLookup6(OpenTypeFontTableReader openReader, int lookupFlag) {
        super(openReader, lookupFlag);
    }

    @Override
    public ContextualSubstRule getMatchingContextRule(GlyphLine line) {
        if (line.idx >= line.end)
            return null;

        Glyph g = line.get(line.idx);
        List<ContextualSubstRule> rules = getSetOfRulesForStartGlyph(g.getCode());
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
            if (gidx.glyph == null || !rule.isGlyphMatchesLookahead(gidx.glyph.getCode(), j)) {
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
            if (gidx.glyph == null || !rule.isGlyphMatchesBacktrack(gidx.glyph.getCode(), j)) {
                break;
            }
        }
        return j == rule.getBacktrackContextLength();
    }

}
