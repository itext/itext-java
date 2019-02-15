/*
 *
 * This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.io.util.MessageFormatUtil;
import java.util.Arrays;

public class Glyph implements Serializable {

    private static final long serialVersionUID = 1627806639423114471L;

    private static final char REPLACEMENT_CHARACTER = '\ufffd';
    private static final char[] REPLACEMENT_CHARACTERS = new char[] {REPLACEMENT_CHARACTER};
    private static final String REPLACEMENT_CHARACTER_STRING = String.valueOf(REPLACEMENT_CHARACTER);

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
    // true, if this Glyph is Mark
    private final boolean isMark;

    // placement offset
    short xPlacement = 0;
    short yPlacement = 0;
    // advance offset
    short xAdvance = 0;
    short yAdvance = 0;

    // Index delta to base glyph. If after a glyph there are several anchored glyphs we should know we to find base glyph.
    short anchorDelta = 0;

    /**
     * Construct a non-mark Glyph, retrieving characters from unicode.
     *
     * @param code code representation of the glyph in the font file
     * @param width normalized width of the glyph
     * @param unicode utf-32 representation of glyph if appears. Correct value is &gt; -1
     */
    public Glyph(int code, int width, int unicode) {
        this(code, width, unicode, null, false);
    }

    /**
     * Construct a non-mark Glyph, using the codepoint of the characters as unicode point.
     *
     * @param code code representation of the glyph in the font file
     * @param width normalized width of the glyph
     * @param chars The Unicode text represented by this Glyph.
     */
    public Glyph(int code, int width, char[] chars) {
        this(code, width, codePoint(chars), chars, false);
    }

    /**
     * Construct a non-mark Glyph, retrieving characters from unicode.
     *
     * @param code code representation of the glyph in the font file
     * @param width normalized width of the glyph
     * @param unicode utf-32 representation of glyph if appears. Correct value is &gt; -1
     * @param bbox The normalized bounding box of this Glyph.
     */
    public Glyph(int code, int width, int unicode, int[] bbox) {
        this(code, width, unicode, null, false);
        this.bbox = bbox;
    }

    /**
     * Construct a non-mark Glyph object with id -1 and characters retrieved from unicode.
     *
     * @param width normalized width of the glyph
     * @param unicode utf-32 representation of glyph if appears. Correct value is &gt; -1
     */
    public Glyph(int width, int unicode) {
        this(-1, width, unicode, getChars(unicode), false);
    }

    /**
     * Construct a glyph object form the passed arguments.
     *
     * @param code code representation of the glyph in the font file
     * @param width normalized width of the glyph
     * @param unicode utf-32 representation of glyph if appears. Correct value is &gt; -1
     * @param chars The Unicode text represented by this Glyph.
     *              if null is passed, the unicode value is used to retrieve the chars.
     * @param IsMark True if the glyph is a Mark
     */
    public Glyph(int code, int width, int unicode, char[] chars, boolean IsMark) {
        this.code = code;
        this.width = width;
        this.unicode = unicode;
        this.isMark = IsMark;
        this.chars = chars != null ? chars : getChars(unicode);
    }

    /**
     * Copy a Glyph.
     *
     * @param glyph Glyph to copy
     */
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

    /**
     * Copy a Glyph and assign new placement and advance offsets and a new index delta to base glyph
     *
     * @param glyph Glyph to copy
     * @param xPlacement x - placement offset
     * @param yPlacement y - placement offset
     * @param xAdvance x - advance offset
     * @param yAdvance y - advance offset
     * @param anchorDelta Index delta to base glyph. If after a glyph there are several anchored glyphs we should know we to find base glyph.
     */
    public Glyph(Glyph glyph, int xPlacement, int yPlacement, int xAdvance, int yAdvance, int anchorDelta) {
        this(glyph);
        this.xPlacement = (short) xPlacement;
        this.yPlacement = (short) yPlacement;
        this.xAdvance = (short) xAdvance;
        this.yAdvance = (short) yAdvance;
        this.anchorDelta = (short) anchorDelta;
    }

    /**
     * Copy a glyph and assign the copied glyph a new unicode point and characters
     *
     * @param glyph glyph to copy
     * @param unicode new unicode point
     */
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

    /**
     * Two Glyphs are equal if their unicode characters, code and normalized width are equal.
     *
     * @param obj The object
     * @return True if this equals obj cast to Glyph, false otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Glyph other = (Glyph) obj;
        return Arrays.equals(chars, other.chars) && code == other.code && width == other.width;
    }

    /**
     * Gets a Unicode string corresponding to this glyph. In general case it might consist of many characters.
     * If this glyph does not have a valid unicode ({@link #hasValidUnicode()}), then a string consisting of a special
     * Unicode '\ufffd' character is returned.
     * @return the Unicode string that corresponds to this glyph
     */
    public String getUnicodeString() {
        if (chars != null) {
            return String.valueOf(chars);
        } else {
            return REPLACEMENT_CHARACTER_STRING;
        }
    }

    /**
     * Gets Unicode char sequence corresponding to this glyph. In general case it might consist of many characters.
     * If this glyph does not have a valid unicode ({@link #hasValidUnicode()}), then a special
     * Unicode '\ufffd' character is returned.
     * @return the Unicode char sequence that corresponds to this glyph
     */
    public char[] getUnicodeChars() {
        if (chars != null) {
            return chars;
        } else {
            return REPLACEMENT_CHARACTERS;
        }
    }

    public String toString() {
        return MessageFormatUtil.format("[id={0}, chars={1}, uni={2}, width={3}]",
                toHex(code), chars != null ? Arrays.toString(chars) : "null", toHex(unicode), width);
    }

    private static String toHex(int ch) {
        String s = "0000" + Integer.toHexString(ch);
        return s.substring(Math.min(4, s.length() - 4));
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
