/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.otf.lookuptype7.PosTableLookup7Format2;
import com.itextpdf.io.util.MessageFormatUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lookup Type 7:
 * Contextual Positioning Subtables
 */
public class GposLookupType7 extends OpenTableLookup {

    private static final Logger LOGGER = LoggerFactory.getLogger(GposLookupType7.class);

    private static final long serialVersionUID = 4596977183462695970L;

    private List<ContextualPositionTable> subTables;

    public GposLookupType7(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations)
            throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        subTables = new ArrayList<>();
        readSubTables();
    }

    @Override
    public boolean transformOne(GlyphLine line) {
        boolean changed = false;
        int oldLineStart = line.start;
        int oldLineEnd = line.end;
        int initialLineIndex = line.idx;

        for (ContextualPositionTable subTable : subTables) {
            ContextualPositionRule contextRule = subTable.getMatchingContextRule(line);
            if (contextRule == null) {
                continue;
            }

            int lineEndBeforeTransformations = line.end;
            PosLookupRecord[] posLookupRecords = contextRule.getPosLookupRecords();
            GlyphIndexer gidx = new GlyphIndexer();
            gidx.line = line;
            for (PosLookupRecord posRecord : posLookupRecords) {
                // There could be some skipped glyphs inside the context sequence, therefore currently GlyphIndexer and
                // nextGlyph method are used to get to the glyph at "substRecord.sequenceIndex" index
                gidx.idx = initialLineIndex;
                for (int i = 0; i < posRecord.sequenceIndex; ++i) {
                    gidx.nextGlyph(openReader, lookupFlag);
                }

                line.idx = gidx.idx;
                OpenTableLookup lookupTable = openReader.getLookupTable(posRecord.lookupListIndex);
                changed = lookupTable.transformOne(line) || changed;
            }

            line.idx = line.end;
            line.start = oldLineStart;
            int lenDelta = lineEndBeforeTransformations - line.end;
            line.end = oldLineEnd - lenDelta;
            return changed;
        }

        line.idx++;
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        int substFormat = openReader.rf.readShort();
        switch (substFormat) {
            case 2:
                readSubTableFormat2(subTableLocation);
                break;
            case 1:
            case 3:
                LOGGER.warn(MessageFormatUtil.format(LogMessageConstant.GPOS_LOOKUP_SUBTABLE_FORMAT_NOT_SUPPORTED,
                        substFormat, 7));
                break;
            default:
                throw new IllegalArgumentException("Bad subtable format identifier: " + substFormat);
        }
    }

    protected void readSubTableFormat2(int subTableLocation) throws java.io.IOException {
        int coverageOffset = openReader.rf.readUnsignedShort();
        int classDefOffset = openReader.rf.readUnsignedShort();
        int posClassSetCount = openReader.rf.readUnsignedShort();
        int[] posClassSetOffsets = openReader.readUShortArray(posClassSetCount, subTableLocation);

        Set<Integer> coverageGlyphIds = new HashSet<>(openReader.readCoverageFormat(subTableLocation + coverageOffset));
        OtfClass classDefinition = openReader.readClassDefinition(subTableLocation + classDefOffset);

        PosTableLookup7Format2 t = new PosTableLookup7Format2(openReader, lookupFlag,
                coverageGlyphIds, classDefinition);

        List<List<ContextualPositionRule>> subClassSets = new ArrayList<>(posClassSetCount);
        for (int i = 0; i < posClassSetCount; ++i) {
            List<ContextualPositionRule> subClassSet = null;
            if (posClassSetOffsets[i] != 0) {
                openReader.rf.seek(posClassSetOffsets[i]);
                int posClassRuleCount = openReader.rf.readUnsignedShort();
                int[] posClassRuleOffsets = openReader.readUShortArray(posClassRuleCount, posClassSetOffsets[i]);

                subClassSet = new ArrayList<>(posClassRuleCount);
                for (int j = 0; j < posClassRuleCount; ++j) {
                    ContextualPositionRule rule;
                    openReader.rf.seek(posClassRuleOffsets[j]);

                    int glyphCount = openReader.rf.readUnsignedShort();
                    int posCount = openReader.rf.readUnsignedShort();
                    int[] inputClassIds = openReader.readUShortArray(glyphCount - 1);
                    PosLookupRecord[] posLookupRecords = openReader.readPosLookupRecords(posCount);

                    rule = new PosTableLookup7Format2.PosRuleFormat2(t, inputClassIds, posLookupRecords);
                    subClassSet.add(rule);
                }
            }
            subClassSets.add(subClassSet);
        }

        t.setPosClassSets(subClassSets);
        subTables.add(t);
    }

}
