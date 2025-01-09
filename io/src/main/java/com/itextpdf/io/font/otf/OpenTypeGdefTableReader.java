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

import com.itextpdf.io.source.RandomAccessFileOrArray;


public class OpenTypeGdefTableReader {
    static final int FLAG_IGNORE_BASE = 2;
    static final int FLAG_IGNORE_LIGATURE = 4;
    static final int FLAG_IGNORE_MARK = 8;

    private final int tableLocation;
    private final RandomAccessFileOrArray rf;
    private OtfClass glyphClass;
    private OtfClass markAttachmentClass;
    
    public OpenTypeGdefTableReader(RandomAccessFileOrArray rf, int tableLocation) {
        this.rf = rf;
        this.tableLocation = tableLocation;
    }
    
    public void readTable() throws java.io.IOException {
        if (tableLocation > 0) {
            rf.seek(tableLocation);
            // version, we only support 0x00010000
            rf.readUnsignedInt();
            int glyphClassDefOffset = rf.readUnsignedShort();
            // skip Attachment Point List Table
            rf.readUnsignedShort();
            // skip Ligature Caret List Table
            rf.readUnsignedShort();
            int markAttachClassDefOffset = rf.readUnsignedShort();
            if (glyphClassDefOffset > 0) {
                glyphClass = OtfClass.create(rf, glyphClassDefOffset + tableLocation);
            }
            if (markAttachClassDefOffset > 0) {
                markAttachmentClass = OtfClass.create(rf, markAttachClassDefOffset + tableLocation);
            }
        }
    }
    
    public boolean isSkip(int glyph, int flag) {
        if (glyphClass != null && (flag & (FLAG_IGNORE_BASE | FLAG_IGNORE_LIGATURE | FLAG_IGNORE_MARK)) != 0) {
            int cla = glyphClass.getOtfClass(glyph);
            if (cla == OtfClass.GLYPH_BASE && (flag & FLAG_IGNORE_BASE) != 0) {
                return true;
            }
            if (cla == OtfClass.GLYPH_MARK && (flag & FLAG_IGNORE_MARK) != 0) {
                return true;
            }
            if (cla == OtfClass.GLYPH_LIGATURE && (flag & FLAG_IGNORE_LIGATURE) != 0) {
                return true;
            }
        }
        int markAttachmentType = (flag >> 8);
        // If MarkAttachmentType is non-zero, then mark attachment classes must be defined in the
        // Mark Attachment Class Definition Table in the GDEF table. When processing glyph sequences,
        // a lookup must ignore any mark glyphs that are not in the specified mark attachment class;
        // only marks of the specified type are processed.
        if (markAttachmentType != 0 && glyphClass != null) {
            int currentGlyphClass = glyphClass.getOtfClass(glyph);
            // Will be 0 in case the class is not defined for this particular glyph
            int glyphMarkAttachmentClass = markAttachmentClass != null ? markAttachmentClass.getOtfClass(glyph) : 0;
            return currentGlyphClass == OtfClass.GLYPH_MARK && glyphMarkAttachmentClass != markAttachmentType;
        }
        return false;
    }

    public OtfClass getGlyphClassTable() {
        return glyphClass;
    }
}
