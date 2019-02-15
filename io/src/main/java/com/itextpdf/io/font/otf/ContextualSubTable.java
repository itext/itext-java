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
package com.itextpdf.io.font.otf;

import java.io.Serializable;
import java.util.List;

public abstract class ContextualSubTable implements Serializable {

    private static final long serialVersionUID = 1802216575331243298L;
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
     *
     * @param line
     * @param rule
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
