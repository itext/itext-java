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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.SerializationException;
import com.itextpdf.styledxmlparser.jsoup.helper.StringUtil;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * HTML entities, and escape routines.
 * Source: <a href="http://www.w3.org/TR/html5/named-character-references.html#named-character-references">W3C HTML
 * named character references</a>.
 */
public class Entities {
    public static class EscapeMode {
        /**
         * Restricted entities suitable for XHTML output: lt, gt, amp, and quot only.
         */
        public static final EscapeMode xhtml = new EscapeMode(xhtmlByVal, "xhtml");
        /**
         * Default HTML output entities.
         */
        public static final EscapeMode base = new EscapeMode(baseByVal, "base");
        /**
         * Complete HTML entities.
         */
        public static final EscapeMode extended = new EscapeMode(fullByVal, "extended");

        private static Map<String, EscapeMode> nameValueMap = new HashMap<String, EscapeMode>();

        public static EscapeMode valueOf(String name) {
            return nameValueMap.get(name);
        }

        static {
            nameValueMap.put(xhtml.name, xhtml);
            nameValueMap.put(base.name, base);
            nameValueMap.put(extended.name, extended);
        }

        private Map<Character, String> map;
        private String name;

        private EscapeMode(Map<Character, String> map, String name) {
            this.map = map;
            this.name = name;
        }

        public Map<Character, String> getMap() {
            return map;
        }

        public String name() {
            return name;
        }
    }

    private static final Map<String, Character> full;
    private static final Map<Character, String> xhtmlByVal;
    private static final Map<String, Character> base;
    private static final Map<Character, String> baseByVal;
    private static final Map<Character, String> fullByVal;

    private Entities() {
    }

    /**
     * Check if the input is a known named entity
     *
     * @param name the possible entity name (e.g. "lt" or "amp")
     * @return true if a known named entity
     */
    public static boolean isNamedEntity(String name) {
        return full.containsKey(name);
    }

    /**
     * Check if the input is a known named entity in the base entity set.
     *
     * @param name the possible entity name (e.g. "lt" or "amp")
     * @return true if a known named entity in the base set
     * @see #isNamedEntity(String)
     */
    public static boolean isBaseNamedEntity(String name) {
        return base.containsKey(name);
    }

    /**
     * Get the Character value of the named entity
     *
     * @param name named entity (e.g. "lt" or "amp")
     * @return the Character value of the named entity (e.g. '{@literal <}' or '{@literal &}')
     */
    public static Character getCharacterByName(String name) {
        return full.get(name);
    }

    static String escape(String string, Document.OutputSettings out) {
        StringBuilder accum = new StringBuilder(string.length() * 2);
        try {
            escape(accum, string, out, false, false, false);
        } catch (IOException e) {
            throw new SerializationException(e); // doesn't happen
        }
        return accum.toString();
    }

    // this method is ugly, and does a lot. but other breakups cause rescanning and stringbuilder generations
    static void escape(Appendable accum, String str, Document.OutputSettings outputSettings,
                       boolean inAttribute, boolean normaliseWhite, boolean stripLeadingWhite) throws IOException {

        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        final EscapeMode escapeMode = outputSettings.escapeMode();
        final CharsetEncoder encoder = outputSettings.encoder();
        final CoreCharset coreCharset = getCoreCharsetByName(outputSettings.charset().name());
        final Map<Character, String> map = escapeMode.getMap();
        final int length = str.length();

        int codePoint;
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = str.codePointAt(offset);

            if (normaliseWhite) {
                if (StringUtil.isWhitespace(codePoint)) {
                    if ((stripLeadingWhite && !reachedNonWhite) || lastWasWhite)
                        continue;
                    accum.append(' ');
                    lastWasWhite = true;
                    continue;
                } else {
                    lastWasWhite = false;
                    reachedNonWhite = true;
                }
            }
            // surrogate pairs, split implementation for efficiency on single char common case (saves creating strings, char[]):
            if (codePoint < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
                final char c = (char) codePoint;
                // html specific and required escapes:
                switch (c) {
                    case '&':
                        accum.append("&amp;");
                        break;
                    case (char) 0xA0:
                        if (escapeMode != EscapeMode.xhtml)
                            accum.append("&nbsp;");
                        else
                            accum.append("&#xa0;");
                        break;
                    case '<':
                        // escape when in character data or when in a xml attribue val; not needed in html attr val
                        if (!inAttribute || escapeMode == EscapeMode.xhtml)
                            accum.append("&lt;");
                        else
                            accum.append(c);
                        break;
                    case '>':
                        if (!inAttribute)
                            accum.append("&gt;");
                        else
                            accum.append(c);
                        break;
                    case '"':
                        if (inAttribute)
                            accum.append("&quot;");
                        else
                            accum.append(c);
                        break;
                    default:
                        if (canEncode(coreCharset, c, encoder))
                            accum.append(c);
                        else if (map.containsKey(c))
                            accum.append('&').append(map.get(c)).append(';');
                        else
                            accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
                }
            } else {
                final String c = new String(Character.toChars(codePoint));
                if (encoder.canEncode(c)) // uses fallback encoder for simplicity
                    accum.append(c);
                else
                    accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
            }
        }
    }

    static String unescape(String string) {
        return unescape(string, false);
    }

    /**
     * Unescape the input string.
     *
     * @param string to un-HTML-escape
     * @param strict if "strict" (that is, requires trailing ';' char, otherwise that's optional)
     * @return unescaped string
     */
    static String unescape(String string, boolean strict) {
        return Parser.unescapeEntities(string, strict);
    }

    /*
     * Provides a fast-path for Encoder.canEncode, which drastically improves performance on Android post JellyBean.
     * After KitKat, the implementation of canEncode degrades to the point of being useless. For non ASCII or UTF,
     * performance may be bad. We can add more encoders for common character sets that are impacted by performance
     * issues on Android if required.
     *
     * Benchmarks:     *
     * OLD toHtml() impl v New (fastpath) in millis
     * Wiki: 1895, 16
     * CNN: 6378, 55
     * Alterslash: 3013, 28
     * Jsoup: 167, 2
     */

    private static boolean canEncode(final CoreCharset charset, final char c, final CharsetEncoder fallback) {
        // todo add more charset tests if impacted by Android's bad perf in canEncode
        switch (charset) {
            case ascii:
                return c < 0x80;
            case utf:
                // real is:!(Character.isLowSurrogate(c) || Character.isHighSurrogate(c)); - but already check above
                return true;
            default:
                return fallback.canEncode(c);
        }
    }

    private enum CoreCharset {
        ascii, utf, fallback;
    }

    private static CoreCharset getCoreCharsetByName(String name) {
        if (name.equals("US-ASCII"))
            return CoreCharset.ascii;
        if (name.startsWith("UTF-")) // covers UTF-8, UTF-16, et al
            return CoreCharset.utf;
        return CoreCharset.fallback;
    }

    // xhtml has restricted entities
    private static final Object[][] xhtmlArray = {
            {"quot", 0x00022},
            {"amp", 0x00026},
            {"lt", 0x0003C},
            {"gt", 0x0003E}
    };

    static {
        xhtmlByVal = new HashMap<Character, String>();
        base = loadEntities("entities-base.properties");  // most common / default
        baseByVal = toCharacterKey(base);
        full = loadEntities("entities-full.properties"); // extended and overblown.
        fullByVal = toCharacterKey(full);

        for (Object[] entity : xhtmlArray) {
            char c = (char) ((Integer) entity[1]).intValue();
            xhtmlByVal.put(c, ((String) entity[0]));
        }
    }

    private static Map<String, Character> loadEntities(String filename) {
        Properties properties = new Properties();
        Map<String, Character> entities = new HashMap<String, Character>();
        try {
            InputStream in = Entities.class.getResourceAsStream(filename);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            throw new MissingResourceException("Error loading entities resource: " + e.getMessage(), "Entities", filename);
        }

        for (Object name : properties.keySet()) {
            Character val = (char) Integer.parseInt(properties.getProperty((String) name), 16);
            entities.put((String) name, val);
        }
        return entities;
    }

    private static Map<Character, String> toCharacterKey(Map<String, Character> inMap) {
        Map<Character, String> outMap = new HashMap<Character, String>();
        for (Map.Entry<String, Character> entry : inMap.entrySet()) {
            char character = (char) entry.getValue();
            String name = entry.getKey();

            if (outMap.containsKey(character)) {
                // dupe, prefer the lower case version
                if (name.toLowerCase().equals(name))
                    outMap.put(character, name);
            } else {
                outMap.put(character, name);
            }
        }
        return outMap;
    }
}
