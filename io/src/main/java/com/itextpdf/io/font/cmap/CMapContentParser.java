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
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.PdfTokenizer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMapContentParser {

    /**
     * Commands have this type.
     */
    public static final int COMMAND_TYPE = 200;

    /**
     * Holds value of property tokeniser.
     */
    private PdfTokenizer tokeniser;

    /**
     * Creates a new instance of PdfContentParser
     * @param tokeniser the tokeniser with the content
     */
    public CMapContentParser(PdfTokenizer tokeniser) {
        this.tokeniser = tokeniser;
    }

    /**
     * Parses a single command from the content. Each command is output as an array of arguments
     * having the command itself as the last element. The returned array will be empty if the
     * end of content was reached.
     * @param ls an {@code ArrayList} to use. It will be cleared before using.
     * @throws java.io.IOException on error
     */
    public void parse(List<CMapObject> ls) throws java.io.IOException {
        ls.clear();
        CMapObject ob;
        while ((ob = readObject()) != null) {
            ls.add(ob);
            // TokenType.Other or CMapObject.Literal means a command
            if (ob.isLiteral())
                break;
        }
    }

    /**
     * Reads a dictionary. The tokeniser must be positioned past the {@code "<<"} token.
     * @return the dictionary
     * @throws java.io.IOException on error
     */
    public CMapObject readDictionary() throws java.io.IOException {
        Map<String, CMapObject> dic = new HashMap<>();
        while (true) {
            if (!nextValidToken())
                throw new IOException("Unexpected end of file.");
            if (tokeniser.getTokenType() == TokenType.EndDic)
                break;
            if (tokeniser.getTokenType() == TokenType.Other && "def".equals(tokeniser.getStringValue()))
                continue;
            if (tokeniser.getTokenType() != TokenType.Name)
                throw new IOException("Dictionary key {0} is not a name.").setMessageParams(tokeniser.getStringValue());
            String name = tokeniser.getStringValue();
            CMapObject obj = readObject();
            if (obj.isToken()) {
                if (obj.toString().equals(">>")) {
                    tokeniser.throwError(IOException.UnexpectedGtGt);
                }
                if (obj.toString().equals("]")) {
                    tokeniser.throwError(IOException.UnexpectedCloseBracket);
                }
            }
            dic.put(name, obj);
        }
        return new CMapObject(CMapObject.DICTIONARY, dic);
    }

    /**
     * Reads an array. The tokeniser must be positioned past the "[" token.
     * @return an array
     * @throws java.io.IOException on error
     */
    public CMapObject readArray() throws java.io.IOException {
        List<CMapObject> array = new ArrayList<CMapObject>();
        while (true) {
            CMapObject obj = readObject();
            if (obj.isToken()) {
                if (obj.toString().equals("]")) {
                    break;
                }
                if (obj.toString().equals(">>")) {
                    tokeniser.throwError(IOException.UnexpectedGtGt);
                }
            }
            array.add(obj);
        }
        return new CMapObject(CMapObject.ARRAY, array);
    }

    /**
     * Reads a pdf object.
     * @return the pdf object
     * @throws java.io.IOException on error
     */
    public CMapObject readObject() throws java.io.IOException {
        if (!nextValidToken())
            return null;
        TokenType type = tokeniser.getTokenType();
        switch (type) {
            case StartDic:
                return readDictionary();
            case StartArray:
                return readArray();
            case String:
                CMapObject obj;
                if (tokeniser.isHexString()) {
                    obj = new CMapObject(CMapObject.HEX_STRING, PdfTokenizer.decodeStringContent(tokeniser.getByteContent(), true));
                } else {
                    obj = new CMapObject(CMapObject.STRING, PdfTokenizer.decodeStringContent(tokeniser.getByteContent(), false));
                }
                return obj;
            case Name:
                return new CMapObject(CMapObject.NAME, decodeName(tokeniser.getByteContent()));
            case Number:
                CMapObject numObject = new CMapObject(CMapObject.NUMBER, null);
                try {
                    numObject.setValue((int)java.lang.Double.parseDouble(tokeniser.getStringValue()));
                } catch (NumberFormatException e) {
                    numObject.setValue(Integer.MIN_VALUE);
                }
                return numObject;
            case Other:
                return new CMapObject(CMapObject.LITERAL, tokeniser.getStringValue());
            case EndArray:
                return new CMapObject(CMapObject.TOKEN, "]");
            case EndDic:
                return new CMapObject(CMapObject.TOKEN, ">>");
            default:
                return new CMapObject(0, "");
        }
    }

    /**
     * Reads the next token skipping over the comments.
     * @return {@code true} if a token was read, {@code false} if the end of content was reached.
     * @throws java.io.IOException on error.
     */
    public boolean nextValidToken() throws java.io.IOException {
        while (tokeniser.nextToken()) {
            if (tokeniser.getTokenType() == TokenType.Comment)
                continue;
            return true;
        }
        return false;
    }

    // TODO: Duplicates PdfName.generateValue (REFACTOR)
    protected static String decodeName(byte[] content) {
        StringBuilder buf = new StringBuilder();
        try {
            for (int k = 0; k < content.length; ++k) {
                char c = (char) content[k];
                if (c == '#') {
                    byte c1 = content[k + 1];
                    byte c2 = content[k + 2];
                    c = (char) ((ByteBuffer.getHex(c1) << 4) + ByteBuffer.getHex(c2));
                    k += 2;
                }
                buf.append(c);
            }
        } catch (IndexOutOfBoundsException e) {
            // empty on purpose
        }
        return buf.toString();
    }

    private static String toHex4(int n) {
        String s = "0000" + Integer.toHexString(n);
        return s.substring(s.length() - 4);
    }

    /**
     * Gets an hex string in the format "&lt;HHHH&gt;".
     *
     * @param n the number
     * @return the hex string
     */
    public static String toHex(int n) {
        if (n < 0x10000)
            return "<" + toHex4(n) + ">";
        n -= 0x10000;
        int high = n / 0x400 + 0xd800;
        int low = n % 0x400 + 0xdc00;
        return "[<" + toHex4(high) + toHex4(low) + ">]";
    }

    public static String decodeCMapObject(CMapObject cMapObject) {
        if (cMapObject.isHexString()) {
            return PdfEncodings.convertToString(((String) cMapObject.getValue()).getBytes(), PdfEncodings.UNICODE_BIG_UNMARKED);
        } else {
            return (String) cMapObject.getValue();
        }
    }
}
