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
 * Font descriptor flags
 */
public final class FontDescriptorFlags {
    private FontDescriptorFlags() {
    }

    public static final int FIXED_PITCH = 1;
    public static final int SERIF = 1 << 1;
    public static final int SYMBOLIC = 1 << 2;
    public static final int SCRIPT = 1 << 3;
    public static final int NONSYMBOLIC = 1 << 5;
    public static final int ITALIC = 1 << 6;
    public static final int ALL_CAP = 1 << 16;
    public static final int SMALL_CAP = 1 << 17;
    public static final int FORCE_BOLD = 1 << 18;
}
