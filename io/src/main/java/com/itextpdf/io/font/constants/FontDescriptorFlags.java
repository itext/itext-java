/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
 * Font descriptor flags
 */
public final class FontDescriptorFlags {
    private FontDescriptorFlags() {
    }

    public static int FixedPitch = 1;
    public static int Serif = 1 << 1;
    public static int Symbolic = 1 << 2;
    public static int Script = 1 << 3;
    public static int Nonsymbolic = 1 << 5;
    public static int Italic = 1 << 6;
    public static int AllCap = 1 << 16;
    public static int SmallCap = 1 << 17;
    public static int ForceBold = 1 << 18;
}
