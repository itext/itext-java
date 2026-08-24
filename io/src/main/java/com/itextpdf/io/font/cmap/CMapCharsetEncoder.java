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
package com.itextpdf.io.font.cmap;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Encodes Unicode code points for a standard CMap charset.
 */
public final class CMapCharsetEncoder {
    private final CharsetEncoder encoder;
    private final CharBuffer charBuf = CharBuffer.allocate(2);
    private final boolean bmpOnly;
    private final Charset targetCharset;

    /**
     * Creates an encoder that accepts all Unicode code points supported by the charset.
     *
     * @param targetCharset the charset used to encode code points
     */
    public CMapCharsetEncoder(Charset targetCharset) {
        this(targetCharset, false);
    }

    /**
     * Creates an encoder with an optional Basic Multilingual Plane restriction.
     *
     * @param targetCharset the charset used to encode code points
     * @param bmpOnly       {@code true} to reject supplementary code points
     */
    public CMapCharsetEncoder(Charset targetCharset, boolean bmpOnly) {
        this.bmpOnly = bmpOnly;
        this.targetCharset = targetCharset;
        this.encoder = targetCharset.newEncoder();
    }

    /**
     * Encodes one Unicode code point.
     *
     * @param cp the Unicode code point to encode
     *
     * @return a newly allocated encoded byte sequence
     *
     * @throws ITextException if the code point cannot be encoded or is supplementary when BMP-only encoding is used
     */
    public byte[] encodeUnicodeCodePoint(int cp) {
        if (!Character.isBmpCodePoint(cp) && bmpOnly) {
            throw new ITextException(IoExceptionMessageConstant.ONLY_BMP_ENCODING);
        }
        charBuf.clear();
        charBuf.put(Character.toChars(cp));
        charBuf.flip();

        encoder.reset();
        final ByteBuffer destBuf;
        try {
            destBuf = encoder.encode(charBuf);
        } catch (CharacterCodingException e) {
            throw new ITextException(MessageFormatUtil.format(IoExceptionMessageConstant.ENCODING_ERROR, cp,
                    targetCharset.name()), e);
        }
        byte[] result = new byte[destBuf.limit()];

        destBuf.get(result);
        return result;
    }
}
