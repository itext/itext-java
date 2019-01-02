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
package com.itextpdf.svg.processors.impl.font;

import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.font.FontFamilySplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that will examine the font as described in the CSS, and store it
 * in a form that the font provider will understand.
 */
class FontFace {

    /** Name that will be used as the alias of the font. */
    private final String alias;
    /** A list of font face sources. */
    private final List<FontFaceSrc> sources;

    /**
     * Create a {@link FontFace} instance from a list of
     * CSS font attributes ("font-family" or "src").
     *
     * @param properties the font properties
     * @return the {@link FontFace} instance
     */
    public static FontFace create(List<CssDeclaration> properties) {
        String fontFamily = null;
        String srcs = null;
        for(CssDeclaration descriptor: properties) {
            if ("font-family".equals(descriptor.getProperty())) {
                fontFamily = FontFamilySplitter.removeQuotes(descriptor.getExpression());
            } else if ("src".equals(descriptor.getProperty())) {
                srcs = descriptor.getExpression();
            }
        }
        if (fontFamily == null || srcs == null) {
            // 'font-family' and 'src' is required according to spec:
            // https://www.w3.org/TR/2013/CR-css-fonts-3-20131003/#descdef-font-family\
            // https://www.w3.org/TR/2013/CR-css-fonts-3-20131003/#descdef-src
            return null;
        }

        List<FontFaceSrc> sources = new ArrayList<>();
        // ttc collection are supported via url(Arial.ttc#1), url(Arial.ttc#2), etc.
        for (String src : splitSourcesSequence(srcs)) {
            //local|url("ideal-sans-serif.woff")( format("woff"))?
            FontFaceSrc source = FontFaceSrc.create(src.trim());
            if (source != null) {
                sources.add(source);
            }
        }

        if (sources.size() > 0) {
            return new FontFace(fontFamily, sources);
        } else {
            return null;
        }
    }

    // NOTE: If src property is written in incorrect format (for example, contains token url(<url_content>)<some_nonsense>),
    // then browser ignores it altogether and doesn't load font at all, even if there are valid tokens.
    // iText will still process all split tokens and can possibly load this font in case it contains some correct urls.
    /**
     * Processes and splits a string sequence containing a url/uri
     * @param src a string representing css src attribute
     *
     */
    public static String[] splitSourcesSequence(String src) {
        List<String> list = new ArrayList<>();
        int indexToStart = 0;
        while (indexToStart < src.length()) {
            int indexToCut;
            int indexUnescapedOpeningQuoteMark = Math.min(CssUtils.findNextUnescapedChar(src, '\'', indexToStart) >= 0 ?
                            CssUtils.findNextUnescapedChar(src, '\'', indexToStart) : Integer.MAX_VALUE,
                    CssUtils.findNextUnescapedChar(src, '"', indexToStart)  >= 0
                            ? CssUtils.findNextUnescapedChar(src, '"', indexToStart) : Integer.MAX_VALUE);
            int indexUnescapedBracket = CssUtils.findNextUnescapedChar(src, ')', indexToStart);
            if (indexUnescapedOpeningQuoteMark < indexUnescapedBracket) {
                indexToCut = CssUtils.findNextUnescapedChar(src, src.charAt(indexUnescapedOpeningQuoteMark), indexUnescapedOpeningQuoteMark + 1);
                if (indexToCut == -1)
                    indexToCut = src.length();
            }
            else
                indexToCut = indexUnescapedBracket;
            while (indexToCut < src.length() && src.charAt(indexToCut) != ',') {
                indexToCut++;
            }
            list.add(src.substring(indexToStart, indexToCut).trim());
            indexToStart = ++indexToCut;
        }
        String[] result = new String[list.size()];
        list.toArray(result);
        return result;
    }

    /**
     * Gets the font-family.
     * Actually font-family is an alias.
     *
     * @return the font family (or alias)
     */
    public String getFontFamily() {
        return alias;
    }

    /**
     * Gets the font face sources.
     *
     * @return the sources
     */
    public List<FontFaceSrc> getSources() {
        return new ArrayList<FontFaceSrc>(sources);
    }

    /**
     * Instantiates a new font face.
     *
     * @param alias the font-family (or alias)
     * @param sources the sources
     */
    private FontFace(String alias, List<FontFaceSrc> sources) {
        this.alias = alias;
        this.sources = new ArrayList<FontFaceSrc>(sources);
    }

    //region Nested types

    /**
     * Class that defines a font face source.
     */
    static class FontFaceSrc {

        /** The UrlPattern used to compose a source path. */
        static final Pattern UrlPattern = Pattern.compile("^((local)|(url))\\(((\'[^\']*\')|(\"[^\"]*\")|([^\'\"\\)]*))\\)( format\\(((\'[^\']*\')|(\"[^\"]*\")|([^\'\"\\)]*))\\))?$");

        /** The Constant TypeGroup. */
        static final int TypeGroup = 1;

        /** The Constant UrlGroup. */
        static final int UrlGroup = 4;

        /** The Constant FormatGroup. */
        static final int FormatGroup = 9;

        /** The font format. */
        final FontFormat format;

        /** The source path. */
        final String src;

        /** Indicates if the font is local. */
        final boolean isLocal;

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return MessageFormatUtil.format("{0}({1}){2}", isLocal ? "local" : "url", src, format != FontFormat.None ? MessageFormatUtil.format(" format({0})", format) : "");
        }

        /**
         * Creates a {@link FontFace} object by parsing a {@link String}
         * trying to match patterns that reveal the font name, whether that font is local,
         * and which format the font is in.
         *
         * @param src a string containing information about a font
         * @return the font in the form of a {@link FontFace} object
         */
        static FontFaceSrc create(String src) {
            Matcher m = UrlPattern.matcher(src);
            if (!m.matches()) {
                return null;
            }
            return new FontFaceSrc(unquote(m.group(UrlGroup)),
                    "local".equals(m.group(TypeGroup)),
                    parseFormat(m.group(FormatGroup)));
        }

        /**
         * Parses a {@link String} to a font format.
         *
         * @param formatStr a string
         * @return a font format
         */
        static FontFormat parseFormat(String formatStr) {
            if (formatStr != null && formatStr.length() > 0) {
                switch (unquote(formatStr).toLowerCase()) {
                    case "truetype":
                        return FontFormat.TrueType;
                    case "opentype":
                        return FontFormat.OpenType;
                    case "woff":
                        return FontFormat.WOFF;
                    case "woff2":
                        return FontFormat.WOFF2;
                    case "embedded-opentype":
                        return FontFormat.EOT;
                    case "svg":
                        return FontFormat.SVG;
                }
            }
            return FontFormat.None;
        }

        /**
         * Removes single and double quotes at the start and the end of a {@link String}.
         *
         * @param quotedString a {@link String} that might be between quotes
         * @return the {@link String} without the quotes
         */
        static String unquote(String quotedString) {
            if (quotedString.charAt(0) == '\'' || quotedString.charAt(0) == '\"') {
                return quotedString.substring(1, quotedString.length() - 1);
            }
            return quotedString;
        }

        /**
         * Instantiates a new {@link FontFaceSrc} instance.
         *
         * @param src a source path
         * @param isLocal indicates if the font is local
         * @param format the font format (true type, open type, woff,...)
         */
        private FontFaceSrc(String src, boolean isLocal, FontFormat format) {
            this.format = format;
            this.src = src;
            this.isLocal = isLocal;
        }
    }

    /**
     * The Enum FontFormat.
     */
    enum FontFormat {
        None,
        /** "truetype" */
        TrueType,
        /** "opentype" */
        OpenType,
        /** "woff" */
        WOFF,
        /** "woff2" */
        WOFF2,
        /** "embedded-opentype" */
        EOT,
        /** "svg" */
        SVG
    }

    //endregion
}
