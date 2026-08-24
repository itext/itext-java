/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.logs.LazyLogger;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.util.IntHashtable;

import java.io.IOException;

public class OtfClass {

    private static final LazyLogger LOGGER = new LazyLogger(OtfClass.class);

    public static final int GLYPH_BASE = 1;
    public static final int GLYPH_LIGATURE = 2;
    public static final int GLYPH_MARK = 3;

    // Key is glyph, value is class inside all 2
    private final IntHashtable mapClass = new IntHashtable();

    private OtfClass(RandomAccessFileOrArray rf, int classLocation) throws java.io.IOException {
        rf.seek(classLocation);
        int classFormat = rf.readUnsignedShort();
        if (classFormat == 1) {
            int startGlyph = rf.readUnsignedShort();
            int glyphCount = rf.readUnsignedShort();
            int endGlyph = startGlyph + glyphCount;
            for (int k = startGlyph; k < endGlyph; ++k) {
                int cl = rf.readUnsignedShort();
                mapClass.put(k, cl);
            }
        } else if (classFormat == 2) {
            int classRangeCount = rf.readUnsignedShort();
            for (int k = 0; k < classRangeCount; ++k) {
                int glyphStart = rf.readUnsignedShort();
                int glyphEnd = rf.readUnsignedShort();
                int cl = rf.readUnsignedShort();
                for (; glyphStart <= glyphEnd; ++glyphStart) {
                    mapClass.put(glyphStart, cl);
                }
            }
        } else {
            throw new java.io.IOException("Invalid class format " + classFormat);
        }
    }

    /**
     * Creates new {@link OtfClass} instance.
     *
     * @param rf {@link RandomAccessFileOrArray}
     * @param classLocation class location
     *
     * @return new {@link OtfClass} instance
     */
    public static OtfClass create(RandomAccessFileOrArray rf, int classLocation) {
        OtfClass otfClass;
        try {
            otfClass = new OtfClass(rf, classLocation);
        } catch (IOException e) {
            LOGGER.error(() -> MessageFormatUtil.format(
                    IoLogMessageConstant.OPENTYPE_GDEF_TABLE_ERROR, e.getMessage()));
            otfClass = null;
        }
        return otfClass;
    }

    /**
     * Returns the otf class for the passed glyph.
     *
     * @param glyph the glyph
     *
     * @return the requested result
     */
    public int getOtfClass(int glyph) {
        return mapClass.get(glyph);
    }

    /**
     * Determines whether passed glyph is Mark or not.
     *
     * @param glyph the glyph to check
     *
     * @return {@code true} if the passed glyph is Mark; otherwise {@code false}
     */
    public boolean isMarkOtfClass(int glyph) {
        return hasClass(glyph) && getOtfClass(glyph) == GLYPH_MARK;
    }

    /**
     * Determines whether passed glyph has class.
     *
     * @param glyph the glyph
     *
     * @return {@code true} if has; otherwise {@code false}.
     */
    public boolean hasClass(int glyph) {
        return mapClass.containsKey(glyph);
    }

    /**
     * Returns the otf class for the passed glyph.
     *
     * @param glyph the glyph
     * @param strict boolean value identifying whether the check if passed glyph has class should be done first
     * (-1 is returned if glyph doesn't have class and strict is true)
     *
     * @return the requested result
     */
    public int getOtfClass(int glyph, boolean strict) {
        if (strict) {
            if (mapClass.containsKey(glyph)) {
                return mapClass.get(glyph);
            } else {
                return -1;
            }
        } else {
            return mapClass.get(glyph);
        }
    }
}
