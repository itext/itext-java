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
package com.itextpdf.commons.utils;

import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class EncodingUtil {
    private static final String UTF8 = "UTF-8";

    private EncodingUtil() {
    }

    /**
     * Converts to byte array an array of chars, taking the provided encoding into account.
     *
     * @param chars an array of chars to be converted to bytes
     * @param encoding the encoding to be taken into account while converting the provided array of chars
     *
     * @return the resultant array of bytes
     *
     * @throws CharacterCodingException if anything goes wrong while encoding
     */
    public static byte[] convertToBytes(char[] chars, String encoding) throws CharacterCodingException {
        Charset cc = Charset.forName(encoding);
        CharsetEncoder ce = cc.newEncoder();
        ce.onUnmappableCharacter(CodingErrorAction.IGNORE);
        java.nio.ByteBuffer bb = ce.encode(CharBuffer.wrap(chars));
        ((Buffer) bb).rewind();
        int lim = ((Buffer) bb).limit();
        int offset = EncodingUtil.UTF8.equals(encoding) ? 3 : 0;
        byte[] br = new byte[lim + offset];
        if (EncodingUtil.UTF8.equals(encoding)) {
            br[0] = (byte) 0xEF;
            br[1] = (byte) 0xBB;
            br[2] = (byte) 0xBF;
        }
        bb.get(br, offset, lim);
        return br;
    }

    /**
     * Converts to String an array of bytes, taking the provided encoding into account.
     *
     * @param bytes an array of bytes to be converted to String
     * @param encoding the encoding to be taken into account while converting the provided bytes
     *
     * @return the resultant string
     *
     * @throws UnsupportedEncodingException if anything goes wrong while encoding
     */
    public static String convertToString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
        if (bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB
                && bytes[2] == (byte) 0xBF && encoding.equals(EncodingUtil.UTF8)) {
            return new String(bytes, 3, bytes.length - 3, EncodingUtil.UTF8);
        }
        return new String(bytes, encoding);
    }
}
