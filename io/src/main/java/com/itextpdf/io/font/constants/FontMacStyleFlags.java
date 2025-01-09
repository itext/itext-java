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
package com.itextpdf.io.font.constants;

/**
 * Represents Open Type head.macStyle bits.
 * <p>
 * https://www.microsoft.com/typography/otspec/head.htm
 */
public final class FontMacStyleFlags {

    private FontMacStyleFlags() {
    }

    // Bit 0: Bold (if set to 1);
    public static final int BOLD = 1;
    
    // Bit 1: Italic (if set to 1)
    public static final int ITALIC = 2;
    
    // Bit 2: Underline (if set to 1)
    public static final int UNDERLINE = 4;
    
    // Bit 3: Outline (if set to 1)
    public static final int OUTLINE = 8;
    
    // Bit 4: Shadow (if set to 1)
    public static final int SHADOW = 16;
    
    // Bit 5: Condensed (if set to 1)
    public static final int CONDENSED = 32;
    
    // Bit 6: Extended (if set to 1)
    public static final int EXTENDED = 64;
}
