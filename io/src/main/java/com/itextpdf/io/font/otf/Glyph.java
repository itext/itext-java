/*
 *
 * This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.io.font.otf;

import com.itextpdf.io.util.TextUtil;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;

public class Glyph implements Serializable {

    private static final long serialVersionUID = 1627806639423114471L;

    // The <i>code</i> or <i>id</i> by which this is represented in the Font File.
    private final int code;
    // The normalized width of this Glyph.
    private final int width;
    // The normalized bbox of this Glyph.
    private int[] bbox = null;
    // utf-32 representation of glyph if appears. Correct value is > -1
    private int unicode;
    // The Unicode text represented by this Glyph
    private char[] chars;
    // ture, if this Glyph is Mark
    private final boolean isMark;

    // placement offset
    short xPlacement = 0;
    short yPlacement = 0;
    // advance offset
    short xAdvance = 0;
    short yAdvance = 0;

    // Index delta to base glyph. If after a glyph there are several anchored glyphs we should know we to find base glyph.
    short anchorDelta = 0;

    public Glyph(int code, int width, int unicode) {
        this(code, width, unicode, null, false);
    }

    public Glyph(int code, int width, char[] chars) {
        this(code, width, codePoint(chars), chars, false);
    }

    public Glyph(int code, int width, int unicode, int[] bbox) {
        this(code, width, unicode, null, false);
        this.bbox = bbox;
    }

    public Glyph(int width, int unicode) {
        this(-1, width, unicode, getChars(unicode), false);
    }

    public Glyph(int code, int width, int unicode, char[] chars, boolean IsMark) {
        this.code = code;
        this.width = width;
        this.unicode = unicode;
        this.isMark = IsMark;
        this.chars = chars != null ? chars : getChars(unicode);
    }

    public Glyph(Glyph glyph) {
        this.code = glyph.code;
        this.width = glyph.width;
        this.chars = glyph.chars;
        this.unicode = glyph.unicode;
        this.isMark = glyph.isMark;
        this.bbox = glyph.bbox;

        this.xPlacement = glyph.xPlacement;
        this.yPlacement = glyph.yPlacement;
        this.xAdvance = glyph.xAdvance;
        this.yAdvance = glyph.yAdvance;
        this.anchorDelta = glyph.anchorDelta;
    }

    public Glyph(Glyph glyph, int xPlacement, int yPlacement, int xAdvance, int yAdvance, int anchorDelta) {
        this(glyph);
        this.xPlacement = (short) xPlacement;
        this.yPlacement = (short) yPlacement;
        this.xAdvance = (short) xAdvance;
        this.yAdvance = (short) yAdvance;
        this.anchorDelta = (short) anchorDelta;
    }

    public Glyph(Glyph glyph, int unicode) {
        this(glyph.code, glyph.width, unicode, getChars(unicode), glyph.isMark());
    }

    public int getCode() {
        return code;
    }

    public int getWidth() {
        return width;
    }

    public int[] getBbox() {
        return bbox;
    }

    public boolean hasValidUnicode() {
        return unicode > -1;
    }

    public int getUnicode() {
        return unicode;
    }

    public void setUnicode(int unicode) {
        this.unicode = unicode;
        this.chars = getChars(unicode);
    }

    public char[] getChars() {
        return chars;
    }

    public void setChars(char[] chars) {
        this.chars = chars;
    }

    public boolean isMark() {
        return isMark;
    }

    public short getXPlacement() {
        return xPlacement;
    }

    public void setXPlacement(short xPlacement) {
        this.xPlacement = xPlacement;
    }

    public short getYPlacement() {
        return yPlacement;
    }

    public void setYPlacement(short yPlacement) {
        this.yPlacement = yPlacement;
    }

    public short getXAdvance() {
        return xAdvance;
    }

    public void setXAdvance(short xAdvance) {
        this.xAdvance = xAdvance;
    }

    public short getYAdvance() {
        return yAdvance;
    }

    public void setYAdvance(short yAdvance) {
        this.yAdvance = yAdvance;
    }

    public short getAnchorDelta() {
        return anchorDelta;
    }

    public void setAnchorDelta(short anchorDelta) {
        this.anchorDelta = anchorDelta;
    }

    public boolean hasOffsets() {
        return xPlacement != 0 || yPlacement != 0 || xAdvance != 0 || yAdvance != 0;
    }

    public boolean hasPlacement() {
        return xPlacement != 0 || yPlacement != 0;
    }

    public boolean hasAdvance() {
        return xAdvance != 0 || yAdvance != 0;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chars == null) ? 0 : Arrays.hashCode(chars));
        result = prime * result + code;
        result = prime * result + width;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Glyph)) {
            return false;
        }
        Glyph other = (Glyph) obj;
        return Arrays.equals(chars, other.chars) && code == other.code && width == other.width;
    }

    public String toString() {
        return MessageFormat.format("[id={0}, chars={1}, uni={2}, width={3}]",
                code, chars != null ? Arrays.toString(chars) : "null", unicode, width);
    }

    private static int codePoint(char[] a) {
        if (a != null) {
            if (a.length == 1 && Character.isValidCodePoint(a[0])) {
                return a[0];
            } else if (a.length == 2 && Character.isHighSurrogate(a[0]) && Character.isLowSurrogate(a[1])) {
                return Character.toCodePoint(a[0], a[1]);
            }
        }
        return -1;
    }

    private static char[] getChars(int unicode) {
        return unicode > -1 ? TextUtil.convertFromUtf32(unicode) : null;
    }
}
