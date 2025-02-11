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
package com.itextpdf.pdfa;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;

/**
 * Utilities to construct an XMP for a PDF/A file.
 */
public class PdfAXMPUtil {
    /**
     * Check whether the given byte array is an UTF-8 encoded character sequence.
     *
     * @param array array to check
     *
     * @return true if array is UTF-8 encoded data, false otherwise
     */
    public static boolean isUtf8(byte[] array) {
        try {
            StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(array));
        }
        catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }
}
