/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.font;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.util.FontFamilySplitterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that will examine the font as described in the CSS, and store it
 * in a form that the font provider will understand.
 */
public class CssFontFace {

    /** Name that will be used as the alias of the font. */
    private final String alias;
    /** A list of font face sources. */
    private final List<CssFontFaceSrc> sources;

    /**
     * Create a {@link CssFontFace} instance from a list of
     * CSS font attributes ("font-family" or "src").
     *
     * @param properties the font properties
     * @return the {@link CssFontFace} instance
     */
    public static CssFontFace create(List<CssDeclaration> properties) {
        String fontFamily = null;
        String srcs = null;
        for(CssDeclaration descriptor: properties) {
            if (CommonCssConstants.FONT_FAMILY.equals(descriptor.getProperty())) {
                // TODO DEVSIX-2534
                fontFamily = FontFamilySplitterUtil.removeQuotes(descriptor.getExpression());
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

        List<CssFontFaceSrc> sources = new ArrayList<>();
        // ttc collection are supported via url(Arial.ttc#1), url(Arial.ttc#2), etc.
        for (String src : splitSourcesSequence(srcs)) {
            //local|url("ideal-sans-serif.woff")( format("woff"))?
            CssFontFaceSrc source = CssFontFaceSrc.create(src.trim());
            if (source != null) {
                sources.add(source);
            }
        }

        if (sources.size() > 0) {
            return new CssFontFace(fontFamily, sources);
        } else {
            return null;
        }
    }

    // NOTE: If src property is written in incorrect format
    // (for example, contains token url(<url_content>)<some_nonsense>),
    // then browser ignores it altogether and doesn't load font at all, even if there are valid tokens.
    // iText will still process all split tokens and can possibly load this font in case it contains some correct urls.
    /**
     * Processes and splits a string sequence containing a url/uri.
     *
     * @param src a string representing css src attribute
     * @return an array of {@link String} urls for font loading
     */
    public static String[] splitSourcesSequence(String src) {
        List<String> list = new ArrayList<>();
        int indexToStart = 0;
        while (indexToStart < src.length()) {
            int indexToCut;
            int indexUnescapedOpeningQuoteMark = Math.min(CssUtils.findNextUnescapedChar(src, '\'', indexToStart) >= 0 ?
                            CssUtils.findNextUnescapedChar(src, '\'', indexToStart) : Integer.MAX_VALUE,
                    CssUtils.findNextUnescapedChar(src, '"', indexToStart) >= 0
                            ? CssUtils.findNextUnescapedChar(src, '"', indexToStart) : Integer.MAX_VALUE);
            int indexUnescapedBracket = CssUtils.findNextUnescapedChar(src, ')', indexToStart);
            if (indexUnescapedOpeningQuoteMark < indexUnescapedBracket) {
                indexToCut = CssUtils.findNextUnescapedChar(src, src.charAt(indexUnescapedOpeningQuoteMark),
                        indexUnescapedOpeningQuoteMark + 1);
                if (indexToCut == -1) {
                    indexToCut = src.length();
                }
            }
            else {
                indexToCut = indexUnescapedBracket;
            }
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
     * Checks whether in general we support requested font format.
     *
     * @param format {@link FontFormat}
     * @return true, if supported or unrecognized.
     */
    public static boolean isSupportedFontFormat(FontFormat format) {
        switch (format) {
            case None:
            case TrueType:
            case OpenType:
            case WOFF:
            case WOFF2:
                return true;
            default:
                return false;
        }
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
    public List<CssFontFaceSrc> getSources() {
        return new ArrayList<CssFontFaceSrc>(sources);
    }

    /**
     * Instantiates a new font face.
     *
     * @param alias the font-family (or alias)
     * @param sources the sources
     */
    private CssFontFace(String alias, List<CssFontFaceSrc> sources) {
        this.alias = alias;
        this.sources = new ArrayList<CssFontFaceSrc>(sources);
    }

    /**
     * The Enum FontFormat.
     */
    public enum FontFormat {
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

    /**
     * Class that defines a font face source.
     */
    public static class CssFontFaceSrc {

        /** The UrlPattern used to compose a source path. */
        public static final Pattern UrlPattern = Pattern.compile("^((local)|(url))\\(((\'[^\']*\')|(\"[^\"]*\")|([^\'\"\\)]*))\\)( format\\(((\'[^\']*\')|(\"[^\"]*\")|([^\'\"\\)]*))\\))?$");

        /** The Constant TypeGroup. */
        public static final int TypeGroup = 1;

        /** The Constant UrlGroup. */
        public static final int UrlGroup = 4;

        /** The Constant FormatGroup. */
        public static final int FormatGroup = 9;

        /** The font format. */
        final FontFormat format;

        /** The source path. */
        final String src;

        /** Indicates if the font is local. */
        final boolean isLocal;

        public FontFormat getFormat() {
            return format;
        }

        public String getSrc() {
            return src;
        }

        public boolean isLocal() {
            return isLocal;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return MessageFormatUtil.
                    format("{0}({1}){2}", isLocal ? "local" : "url", src, format != FontFormat.None ?
                                    MessageFormatUtil.format(" format({0})", format) : "");
        }

        /**
         * Creates a {@link CssFontFaceSrc} object by parsing a {@link String}
         * trying to match patterns that reveal the font name, whether that font is local,
         * and which format the font is in.
         *
         * @param src a string containing information about a font
         * @return the font in the form of a {@link CssFontFaceSrc} object
         */
        public static CssFontFaceSrc create(String src) {
            Matcher m = UrlPattern.matcher(src);
            if (!m.matches()) {
                return null;
            }
            return new CssFontFaceSrc(unquote(m.group(UrlGroup)),
                    "local".equals(m.group(TypeGroup)),
                    parseFormat(m.group(FormatGroup)));
        }

        /**
         * Parses a {@link String} to a font format.
         *
         * @param formatStr a string
         * @return a font format
         */
        public static FontFormat parseFormat(String formatStr) {
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
        public static String unquote(String quotedString) {
            if (quotedString.charAt(0) == '\'' || quotedString.charAt(0) == '\"') {
                return quotedString.substring(1, quotedString.length() - 1);
            }
            return quotedString;
        }

        /**
         * Instantiates a new {@link CssFontFaceSrc} instance.
         *
         * @param src a source path
         * @param isLocal indicates if the font is local
         * @param format the font format (true type, open type, woff,...)
         */
        private CssFontFaceSrc(String src, boolean isLocal, FontFormat format) {
            this.format = format;
            this.src = src;
            this.isLocal = isLocal;
        }
    }
}
