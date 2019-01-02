/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.util;

import com.itextpdf.io.font.PdfEncodings;

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

    private EncodingUtil() {
    }

    public static byte[] convertToBytes(char[] chars, String encoding) throws CharacterCodingException {
        Charset cc = Charset.forName(encoding);
        CharsetEncoder ce = cc.newEncoder();
        ce.onUnmappableCharacter(CodingErrorAction.IGNORE);
        java.nio.ByteBuffer bb = ce.encode(CharBuffer.wrap(chars));
        ((Buffer) bb).rewind();
        int lim = ((Buffer) bb).limit();
        int offset = PdfEncodings.UTF8.equals(encoding) ? 3 : 0;
        byte[] br = new byte[lim + offset];
        if (PdfEncodings.UTF8.equals(encoding)) {
            br[0] = (byte) 0xEF;
            br[1] = (byte) 0xBB;
            br[2] = (byte) 0xBF;
        }
        bb.get(br, offset, lim);
        return br;
    }

    public static String convertToString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
        if (encoding.equals(PdfEncodings.UTF8) &&
                bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF)
            return new String(bytes, 3, bytes.length - 3, PdfEncodings.UTF8);
        return new String(bytes, encoding);
    }
}
