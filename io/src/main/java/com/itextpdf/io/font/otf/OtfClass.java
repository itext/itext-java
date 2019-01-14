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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.util.MessageFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;

public class OtfClass implements Serializable {

    public static final int GLYPH_BASE = 1;
    public static final int GLYPH_LIGATURE = 2;
    public static final int GLYPH_MARK = 3;
    private static final long serialVersionUID = -7584495836452964728L;

    //key is glyph, value is class inside all 2
    private IntHashtable mapClass = new IntHashtable();

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

    public static OtfClass create(RandomAccessFileOrArray rf, int classLocation) {
        OtfClass otfClass;
        try {
            otfClass = new OtfClass(rf, classLocation);
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(OtfClass.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.OPENTYPE_GDEF_TABLE_ERROR, e.getMessage()));
            otfClass = null;
        }
        return otfClass;
    }

    public int getOtfClass(int glyph) {
        return mapClass.get(glyph);
    }

    public boolean isMarkOtfClass(int glyph) {
        return hasClass(glyph) && getOtfClass(glyph) == GLYPH_MARK;
    }

    public boolean hasClass(int glyph) {
        return mapClass.containsKey(glyph);
    }

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
