/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;

import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.BitSet;

class UriEncodeUtil {

    /**
     * Set of 256 characters with the bits that don't need encoding set to on.
     */
    private static BitSet unreservedAndReserved;

    /**
     * The difference between the value a character in lower cases and the upper case character value.
     */
    private static final int caseDiff = ('a' - 'A');

    /**
     * The default encoding ("UTF-8").
     */
    private static String dfltEncName = "UTF-8";

    static {
        unreservedAndReserved = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            unreservedAndReserved.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            unreservedAndReserved.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            unreservedAndReserved.set(i);
        }

        unreservedAndReserved.set('-');
        unreservedAndReserved.set('_');
        unreservedAndReserved.set('.');
        unreservedAndReserved.set('~');

        unreservedAndReserved.set(':');
        unreservedAndReserved.set('/');
        unreservedAndReserved.set('?');
        unreservedAndReserved.set('#');
        unreservedAndReserved.set('[');
        unreservedAndReserved.set(']');
        unreservedAndReserved.set('@');
        unreservedAndReserved.set('!');
        unreservedAndReserved.set('$');
        unreservedAndReserved.set('&');
        unreservedAndReserved.set('\'');
        unreservedAndReserved.set('\\');
        unreservedAndReserved.set('(');
        unreservedAndReserved.set(')');
        unreservedAndReserved.set('*');
        unreservedAndReserved.set('+');
        unreservedAndReserved.set(',');
        unreservedAndReserved.set(';');
        unreservedAndReserved.set('=');
    }

    /**
     * Encodes a {@code String} in the default encoding and default uri scheme to an HTML-encoded {@code String}.
     *
     * @param s the original string
     * @return the encoded string
     */
    public static String encode(String s) {
        return encode(s, dfltEncName);
    }

    /**
     * Encodes a {@code String} in a specific encoding and uri scheme to an HTML-encoded {@code String}.
     *
     * @param s      the original string
     * @param enc    the encoding
     * @return the encoded string
     */
    public static String encode(String s, String enc) {
        boolean needToChange = false;
        StringBuffer out = new StringBuffer(s.length());
        Charset charset;
        CharArrayWriter charArrayWriter = new CharArrayWriter();

        if (enc == null) {
            throw new StyledXMLParserException(StyledXMLParserException.UnsupportedEncodingException);
        }

        try {
            charset = Charset.forName(enc);
        } catch (IllegalCharsetNameException e) {
            throw new StyledXMLParserException(StyledXMLParserException.UnsupportedEncodingException);
        }
        int i = 0;
        boolean firstHash = true;
        while (i < s.length()) {
            int c = (int) s.charAt(i);
            if ('\\' == c) {
                out.append('/');
                needToChange = true;
                i++;
            } else if ('%' == c) {
                int v = -1;
                if (i + 2 < s.length()) {
                    try {
                        v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
                    } catch (NumberFormatException e) {
                        v = -1;
                    }
                    if (v >= 0)
                        out.append((char) c);
                }
                if (v < 0) {
                    // here we assume percent sign to be used not for encoding of other characters, i.e. not for its reserved purpose
                    // which means percent sign should be encoded itself. %25 code stands for percent sign.
                    needToChange = true;
                    out.append("%25");
                }
                i++;
            } else if ('#' == c) {
                // we want only the first hash to be left without percent encoding because C# encodes this way
                if (firstHash) {
                    out.append((char) c);
                    firstHash = false;
                }
                else {
                    out.append("%23");
                    needToChange = true;
                }
                i++;
            } else if (unreservedAndReserved.get(c)) {
                out.append((char) c);
                i++;
            } else {
                // convert to external encoding before hex conversion
                do {
                    charArrayWriter.write(c);
                    /*
                     * If this character represents the start of a Unicode
                     * surrogate pair, then pass in two characters. It's not
                     * clear what should be done if a bytes reserved in the
                     * surrogate pairs range occurs outside of a legal
                     * surrogate pair. For now, just treat it as if it were
                     * any other character.
                     */
                    if (c >= 0xD800 && c <= 0xDBFF) {
                        /*
                          System.out.println(Integer.toHexString(c)
                          + " is high surrogate");
                        */
                        if ((i + 1) < s.length()) {
                            int d = (int) s.charAt(i + 1);
                            /*
                              System.out.println("\tExamining "
                              + Integer.toHexString(d));
                            */
                            if (d >= 0xDC00 && d <= 0xDFFF) {
                                /*
                                  System.out.println("\t"
                                  + Integer.toHexString(d)
                                  + " is low surrogate");
                                */
                                charArrayWriter.write(d);
                                i++;
                            }
                        }
                    }
                    i++;
                } while (i < s.length() && !unreservedAndReserved.get((c = (int) s.charAt(i))));

                charArrayWriter.flush();
                String str = new String(charArrayWriter.toCharArray());
                byte[] ba = str.getBytes(charset);
                for (int j = 0; j < ba.length; j++) {
                    out.append('%');
                    char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
                    // converting to use uppercase letter as part of
                    // the hex value if ch is a letter.
                    if (Character.isLetter(ch)) {
                        ch -= caseDiff;
                    }
                    out.append(ch);
                    ch = Character.forDigit(ba[j] & 0xF, 16);
                    if (Character.isLetter(ch)) {
                        ch -= caseDiff;
                    }
                    out.append(ch);
                }
                charArrayWriter.reset();
                needToChange = true;
            }
        }
        return (needToChange ? out.toString() : s);
    }
}
