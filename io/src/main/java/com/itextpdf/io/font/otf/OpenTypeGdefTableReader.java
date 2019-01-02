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

import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.io.Serializable;

public class OpenTypeGdefTableReader implements Serializable{

    private static final long serialVersionUID = 1564505797329158035L;
    private final int FLAG_IGNORE_BASE = 2;
    private final int FLAG_IGNORE_LIGATURE = 4;
    private final int FLAG_IGNORE_MARK = 8;
    
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
            rf.readUnsignedInt(); //version, we only support 0x00010000
            int glyphClassDefOffset = rf.readUnsignedShort();
            rf.readUnsignedShort(); //skip Attachment Point List Table
            rf.readUnsignedShort(); //skip Ligature Caret List Table
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
        if (markAttachmentClass != null && markAttachmentClass.getOtfClass(glyph) > 0 && (flag >> 8) > 0) {
            return markAttachmentClass.getOtfClass(glyph) != (flag >> 8);
        }
        return false;
    }

    public OtfClass getGlyphClassTable() {
        return glyphClass;
    }
}
