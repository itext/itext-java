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
package com.itextpdf.kernel.pdf;

/**
 * Compression constants for {@link PdfStream}.
 */
public class CompressionConstants {
    /**
     * A possible compression level.
     */
    public static final int UNDEFINED_COMPRESSION = Integer.MIN_VALUE;
    /**
     * A possible compression level.
     */
    public static final int DEFAULT_COMPRESSION = java.util.zip.Deflater.DEFAULT_COMPRESSION;
    /**
     * A possible compression level.
     */
    public static final int NO_COMPRESSION = java.util.zip.Deflater.NO_COMPRESSION;
    /**
     * A possible compression level.
     */
    public static final int BEST_SPEED = java.util.zip.Deflater.BEST_SPEED;
    /**
     * A possible compression level.
     */
    public static final int BEST_COMPRESSION = java.util.zip.Deflater.BEST_COMPRESSION;
}
