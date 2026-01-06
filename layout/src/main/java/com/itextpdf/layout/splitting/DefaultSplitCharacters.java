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
package com.itextpdf.layout.splitting;

import com.itextpdf.io.font.otf.GlyphLine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of {@link ISplitCharacters} interface.
 */
public class DefaultSplitCharacters implements ISplitCharacters {
    // https://www.w3.org/TR/jlreq/?lang=en#characters_not_starting_a_line
    private static final Set<Integer> CHARS_NOT_STARTING_LINE = new HashSet<>(Arrays.asList(
            // https://www.w3.org/TR/jlreq/?lang=en#cl-02
            0x2019, 0x201D, 0x0029, 0x3015, 0x005D, 0x007D, 0x3009, 0x300B,
            0x300D, 0x300F, 0x3011, 0x2986, 0x3019, 0x3017, 0x00BB, 0x301F,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-03
            0x2010, 0x301C, 0x30A0, 0x2013,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-04
            0x0021, 0x003F, 0x203C, 0x2047, 0x2048, 0x2049,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-05
            0x30FB, 0x003A, 0x003B,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-06
            0x3002, 0x002E,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-07
            0x3001, 0x002C,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-09
            0x30FD, 0x30FE, 0x309D, 0x309E, 0x3005, 0x303B,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-10
            0x30FC,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-11
            0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x30A1, 0x30A3, 0x30A5,
            0x30A7, 0x30A9, 0x3063, 0x3083, 0x3085, 0x3087, 0x308E, 0x3095,
            0x3096, 0x30C3, 0x30E3, 0x30E5, 0x30E7, 0x30EE, 0x30F5, 0x30F6,
            0x31F0, 0x31F1, 0x31F2, 0x31F3, 0x31F4, 0x31F5, 0x31F6, 0x31F7,
            0x31F8, 0x31F9, 0x31FA, 0x31FB, 0x31FC, 0x31FD, 0x31FE, 0x31FF,
            // https://www.w3.org/TR/jlreq/?lang=en#cl-29
            0x0029, 0x3015, 0x005D
    ));

    // https://www.w3.org/TR/jlreq/?lang=en#characters_not_ending_a_line
    private static final Set<Integer> CHARS_NOT_ENDING_LINE = new HashSet<>(Arrays.asList(
            // https://www.w3.org/TR/jlreq/?lang=en#cl-01
            // https://www.w3.org/TR/jlreq/?lang=en#cl-28
            0x2018, 0x201c, 0x0028, 0x3014, 0x005B, 0x007B, 0x3008, 0x300A,
            0x300C, 0x300E, 0x3010, 0x2985, 0x3018, 0x3016, 0x00AB, 0x301D
    ));

    // https://www.w3.org/TR/jlreq/?lang=en#unbreakable_character_sequences
    private static final Map<Integer, Integer> CHARS_INSEPARABLE;

    static {
        CHARS_INSEPARABLE = new HashMap<>();
        CHARS_INSEPARABLE.put(0x2013, 0x2013);
        CHARS_INSEPARABLE.put(0x2026, 0x2026);
        CHARS_INSEPARABLE.put(0x2025, 0x2025);
        CHARS_INSEPARABLE.put(0x3033, 0x3035);
        CHARS_INSEPARABLE.put(0x3034, 0x3035);
    }

    @Override
    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
        if (!text.get(glyphPos).hasValidUnicode()) {
            return false;
        }
        int charCode = text.get(glyphPos).getUnicode();

        if (text.size() - 1 > glyphPos) {
            // Check if a hyphen precedes a digit to denote negative value
            // TODO: DEVSIX-4863 why is glyphPos == 0? negative value could be preceded by a whitespace!
            if ((glyphPos == 0) && (charCode == '-') && (isDigitChar(text, glyphPos + 1))) {
                return false;
            }
            // Check if next char can't start the line
            int nextCharCode = text.get(glyphPos + 1).getUnicode();
            if (CHARS_NOT_STARTING_LINE.contains(nextCharCode)) {
                return false;
            }
            // Check if current and next char are inseparable sequence
            Integer secondInseparable = CHARS_INSEPARABLE.get(charCode);
            if (secondInseparable != null && secondInseparable == nextCharCode) {
                return false;
            }
        }
        // Check if the current char can't end the line
        if (CHARS_NOT_ENDING_LINE.contains(charCode)) {
            return false;
        }

        return (charCode <= ' '
                || charCode == '-'
                // U+2010 is hyphen
                || charCode == '\u2010'
                // Part of Unicode Block "General Punctuation": different spaces
                // See https://www.compart.com/en/unicode/block/U+2000
                || (charCode >= 0x2002 && charCode <= 0x200b)
                // CJK and other script characters block
                || (charCode >= 0x2e80 && charCode < 0xd7a0)
                // Unicode Block "CJK Compatibility Ideographs", see https://www.compart.com/en/unicode/block/U+F900
                || (charCode >= 0xf900 && charCode < 0xfb00)
                // Unicode Block "CJK Compatibility Forms", see https://www.compart.com/en/unicode/block/U+FE30
                || (charCode >= 0xfe30 && charCode < 0xfe50)
                // Part of Unicode Block "Halfwidth and Fullwidth Forms": CJK halfwitdh characters
                // See https://www.compart.com/en/unicode/block/U+FF00
                || (charCode >= 0xff61 && charCode < 0xffa0));
    }

    private static boolean isDigitChar(GlyphLine text, int glyphPos) {
       return Character.isDigit(text.get(glyphPos).getChars()[0]);
    }
}
