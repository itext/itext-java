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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LookupType 4: Ligature Substitution Subtable
 * @author psoares
 */
public class GsubLookupType4 extends OpenTableLookup {
    private static final long serialVersionUID = -8106254947137506056L;
    /**
     * The key is the first character. The first element in the int array is the
     * output ligature
     */
    private Map<Integer,List<int[]>> ligatures;
    
    public GsubLookupType4(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) throws java.io.IOException {
        super(openReader, lookupFlag, subTableLocations);
        ligatures = new HashMap<>();
        readSubTables();
    }
    
    @Override
    public boolean transformOne(GlyphLine line) {
        //TODO >
        if (line.idx >= line.end)
            return false;
        boolean changed = false;
        Glyph g = line.get(line.idx);
        boolean match = false;
        if (ligatures.containsKey(g.getCode()) && !openReader.isSkip(g.getCode(), lookupFlag)) {
            GlyphIndexer gidx = new GlyphIndexer();
            gidx.line = line;
            List<int[]> ligs = ligatures.get(g.getCode());
            for (int[] lig : ligs) {
                match = true;
                gidx.idx = line.idx;
                for (int j = 1; j < lig.length; ++j) {
                    gidx.nextGlyph(openReader, lookupFlag);
                    if (gidx.glyph == null || gidx.glyph.getCode() != lig[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    line.substituteManyToOne(openReader, lookupFlag, lig.length - 1, lig[0]);
                    break;
                }
            }
        }
        if (match) {
            changed = true;
        }
        line.idx++;
        return changed;
    }

    @Override
    protected void readSubTable(int subTableLocation) throws java.io.IOException {
        openReader.rf.seek(subTableLocation);
        openReader.rf.readShort(); //subformat - always 1
        int coverage = openReader.rf.readUnsignedShort() + subTableLocation;
        int ligSetCount = openReader.rf.readUnsignedShort();
        int[] ligatureSet = new int[ligSetCount];
        for (int k = 0; k < ligSetCount; ++k) {
            ligatureSet[k] = openReader.rf.readUnsignedShort() + subTableLocation;
        }
        List<Integer> coverageGlyphIds = openReader.readCoverageFormat(coverage);
        for (int k = 0; k < ligSetCount; ++k) {
            openReader.rf.seek(ligatureSet[k]);
            int ligatureCount = openReader.rf.readUnsignedShort();
            int[] ligature = new int[ligatureCount];
            for (int j = 0; j < ligatureCount; ++j) {
                ligature[j] = openReader.rf.readUnsignedShort() + ligatureSet[k];
            }
            List<int[]> components = new ArrayList<>(ligatureCount);
            for (int j = 0; j < ligatureCount; ++j) {
                openReader.rf.seek(ligature[j]);
                int ligGlyph = openReader.rf.readUnsignedShort();
                int compCount = openReader.rf.readUnsignedShort();
                int[] component = new int[compCount];
                component[0] = ligGlyph;
                for (int i = 1; i < compCount; ++i) {
                    component[i] = openReader.rf.readUnsignedShort();
                }
                components.add(component);
            }
            ligatures.put(coverageGlyphIds.get(k), components);
        }
    }    
}
