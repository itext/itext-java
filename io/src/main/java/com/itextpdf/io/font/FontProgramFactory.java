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
package com.itextpdf.io.font;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.constants.FontStyles;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.exceptions.FontCompressionException;
import com.itextpdf.io.font.woff2.Woff2Converter;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.Set;

/**
 * Provides methods for creating various types of fonts.
 */
public final class FontProgramFactory {

    /**
     * This is the default value of the <VAR>cached</VAR> variable.
     */
    private static boolean DEFAULT_CACHED = true;

    private static FontRegisterProvider fontRegisterProvider = new FontRegisterProvider();

    private FontProgramFactory() {
    }

    /**
     * Creates a new standard Helvetica font program file.
     *
     * @return a {@link FontProgram} object with Helvetica font description
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createFont() throws java.io.IOException {
        return createFont(StandardFonts.HELVETICA);
    }

    /**
     * Creates a new font program. This font program can be one of the 14 built in fonts,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font or
     * a CJK font from the Adobe Asian Font Pack.
     * Fonts in TrueType Collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p>
     * The fonts are cached and if they already exist they are extracted from the cache,
     * not parsed again.
     * <p>
     *
     * @param fontProgram the name of the font or its location on file
     * @return returns a new {@link FontProgram}. This font program may come from the cache
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createFont(String fontProgram) throws java.io.IOException {
        return createFont(fontProgram, null, DEFAULT_CACHED);
    }

    /**
     * Creates a new font program. This font program can be one of the 14 built in fonts,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font or
     * a CJK font from the Adobe Asian Font Pack.
     * Fonts in TrueType Collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p>
     * The fonts are cached and if they already exist they are extracted from the cache,
     * not parsed again.
     * <p>
     *
     * @param fontProgram the name of the font or its location on file
     * @param cached whether to to cache this font program after it has been loaded
     * @return returns a new {@link FontProgram}. This font program may come from the cache
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createFont(String fontProgram, boolean cached) throws java.io.IOException {
        return createFont(fontProgram, null, cached);
    }

    /**
     * Creates a new font program. This font program can be one of the 14 built in fonts,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font or
     * a CJK font from the Adobe Asian Font Pack.
     * Fonts in TrueType Collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p>
     * The fonts are cached and if they already exist they are extracted from the cache,
     * not parsed again.
     * <p>
     *
     * @param fontProgram the byte contents of the font program
     * @return returns a new {@link FontProgram}. This font program may come from the cache
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createFont(byte[] fontProgram) throws java.io.IOException {
        return createFont(null, fontProgram, DEFAULT_CACHED);
    }

    /**
     * Creates a new font program. This font program can be one of the 14 built in fonts,
     * a Type 1 font referred to by an AFM or PFM file, a TrueType font or
     * a CJK font from the Adobe Asian Font Pack.
     * Fonts in TrueType Collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p>
     * The fonts are cached and if they already exist they are extracted from the cache,
     * not parsed again.
     * <p>
     *
     * @param fontProgram the byte contents of the font program
     * @param cached whether to to cache this font program
     * @return returns a new {@link FontProgram}. This font program may come from the cache
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createFont(byte[] fontProgram, boolean cached) throws java.io.IOException {
        return createFont(null, fontProgram, cached);
    }

    private static FontProgram createFont(String name, byte[] fontProgram, boolean cached) throws java.io.IOException {
        String baseName = FontProgram.trimFontStyle(name);

        //yes, we trying to find built-in standard font with original name, not baseName.
        boolean isBuiltinFonts14 = StandardFonts.isStandardFont(name);
        boolean isCidFont = !isBuiltinFonts14 && FontCache.isPredefinedCidFont(baseName);

        FontProgram fontFound;
        FontCacheKey fontKey = null;
        if (cached) {
            fontKey = createFontCacheKey(name, fontProgram);
            fontFound = FontCache.getFont(fontKey);
            if (fontFound != null) {
                return fontFound;
            }
        }

        FontProgram fontBuilt = null;
        if (name == null) {
            if (fontProgram != null) {
                try {
                    if (WoffConverter.isWoffFont(fontProgram)) {
                        fontProgram = WoffConverter.convert(fontProgram);
                    } else if (Woff2Converter.isWoff2Font(fontProgram)) {
                        fontProgram = Woff2Converter.convert(fontProgram);
                    }
                    fontBuilt = new TrueTypeFont(fontProgram);
                } catch (Exception ignored) {
                }
                if (fontBuilt == null) {
                    try {
                        fontBuilt = new Type1Font(null, null, fontProgram, null);
                    } catch (Exception ignored) {
                    }
                }
            }
        } else {
            String fontFileExtension = null;
            int extensionBeginIndex = baseName.lastIndexOf('.');
            if (extensionBeginIndex > 0) {
                fontFileExtension = baseName.substring(extensionBeginIndex).toLowerCase();
            }
            if (isBuiltinFonts14 || ".afm".equals(fontFileExtension) || ".pfm".equals(fontFileExtension)) {
                fontBuilt = new Type1Font(name, null, null, null);
            } else if (isCidFont) {
                fontBuilt = new CidFont(name, FontCache.getCompatibleCmaps(baseName));
            } else if (".ttf".equals(fontFileExtension) || ".otf".equals(fontFileExtension)) {
                if (fontProgram != null) {
                    fontBuilt = new TrueTypeFont(fontProgram);
                } else {
                    fontBuilt = new TrueTypeFont(name);
                }
            } else if (".woff".equals(fontFileExtension) || ".woff2".equals(fontFileExtension)) {
                if (fontProgram == null) {
                    fontProgram = readFontBytesFromPath(baseName);
                }
                if (".woff".equals(fontFileExtension)) {
                    try {
                        fontProgram = WoffConverter.convert(fontProgram);
                    } catch (IllegalArgumentException woffException) {
                        throw new IOException(IoExceptionMessageConstant.INVALID_WOFF_FILE, woffException);
                    }
                } else {
                    // ".woff2".equals(fontFileExtension)

                    try {
                        fontProgram = Woff2Converter.convert(fontProgram);
                    } catch (FontCompressionException woff2Exception) {
                        throw new IOException(IoExceptionMessageConstant.INVALID_WOFF2_FONT_FILE, woff2Exception);
                    }
                }
                fontBuilt = new TrueTypeFont(fontProgram);
            } else {
                int ttcSplit = baseName.toLowerCase().indexOf(".ttc,");
                if (ttcSplit > 0) {
                    try {

                        // count(.ttc) = 4
                        String ttcName = baseName.substring(0, ttcSplit + 4);

                        // count(.ttc,) = 5)
                        int ttcIndex = Integer.parseInt(baseName.substring(ttcSplit + 5));
                        fontBuilt = new TrueTypeFont(ttcName, ttcIndex);
                    } catch (NumberFormatException nfe) {
                        throw new IOException(nfe.getMessage(), nfe);
                    }
                }
            }
        }
        if (fontBuilt == null) {
            if (name != null) {
                throw new IOException(IoExceptionMessageConstant.TYPE_OF_FONT_IS_NOT_RECOGNIZED_PARAMETERIZED).setMessageParams(name);
            } else {
                throw new IOException(IoExceptionMessageConstant.TYPE_OF_FONT_IS_NOT_RECOGNIZED);
            }
        }
        return cached ? FontCache.saveFont(fontBuilt, fontKey) : fontBuilt;
    }

    /**
     * Creates a new Type 1 font by the byte contents of the corresponding AFM/PFM and PFB files
     *
     * @param afm the contents of the AFM or PFM metrics file
     * @param pfb the contents of the PFB file
     * @return created {@link FontProgram} instance
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createType1Font(byte[] afm, byte[] pfb) throws java.io.IOException {
        return createType1Font(afm, pfb, DEFAULT_CACHED);
    }

    /**
     * Creates a new Type 1 font by the byte contents of the corresponding AFM/PFM and PFB files
     *
     * @param afm the contents of the AFM or PFM metrics file
     * @param pfb the contents of the PFB file
     * @param cached specifies whether to cache the created {@link FontProgram} or not
     * @return created {@link FontProgram} instance
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createType1Font(byte[] afm, byte[] pfb, boolean cached) throws java.io.IOException {
        return createType1Font(null, null, afm, pfb, cached);
    }

    /**
     * Creates a new Type 1 font by the corresponding AFM/PFM and PFB files
     *
     * @param metricsPath path to the AFM or PFM metrics file
     * @param binaryPath path to the contents of the PFB file
     * @return created {@link FontProgram} instance
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createType1Font(String metricsPath, String binaryPath) throws java.io.IOException {
        return createType1Font(metricsPath, binaryPath, DEFAULT_CACHED);
    }

    /**
     * Creates a new Type 1 font by the corresponding AFM/PFM and PFB files
     *
     * @param metricsPath path to the AFM or PFM metrics file
     * @param binaryPath path to the contents of the PFB file
     * @param cached specifies whether to cache the created {@link FontProgram} or not
     * @return created {@link FontProgram} instance
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createType1Font(String metricsPath, String binaryPath, boolean cached) throws java.io.IOException {
        return createType1Font(metricsPath, binaryPath, null, null, cached);
    }

    /**
     * Creates a new TrueType font program from ttc (TrueType Collection) file.
     *
     * @param ttc      location  of TrueType Collection file (*.ttc)
     * @param ttcIndex the index of the font file from the collection to be read
     * @param cached   true if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @return returns a new {@link FontProgram} instance. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createFont(String ttc, int ttcIndex, boolean cached) throws java.io.IOException {
        FontCacheKey fontCacheKey = FontCacheKey.create(ttc, ttcIndex);
        if (cached) {
            FontProgram fontFound = FontCache.getFont(fontCacheKey);
            if (fontFound != null) {
                return fontFound;
            }
        }
        FontProgram fontBuilt = new TrueTypeFont(ttc, ttcIndex);
        return cached ? FontCache.saveFont(fontBuilt, fontCacheKey) : fontBuilt;
    }

    /**
     * Creates a new TrueType font program from ttc (TrueType Collection) file bytes.
     *
     * @param ttc      the content of a TrueType Collection file (*.ttc)
     * @param ttcIndex the index of the font file from the collection to be read
     * @param cached   true if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @return returns a new {@link FontProgram} instance. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createFont(byte[] ttc, int ttcIndex, boolean cached) throws java.io.IOException {
        FontCacheKey fontKey = FontCacheKey.create(ttc, ttcIndex);
        if (cached) {
            FontProgram fontFound = FontCache.getFont(fontKey);
            if (fontFound != null) {
                return fontFound;
            }
        }
        FontProgram fontBuilt = new TrueTypeFont(ttc, ttcIndex);
        return cached ? FontCache.saveFont(fontBuilt, fontKey) : fontBuilt;
    }

    /**
     * Creates a FontProgram from the font file that has been previously registered.
     *
     * @param fontName either a font alias, if the font file has been registered with an alias,
     *                 or just a font name otherwise
     * @param style the style of the font to look for. Possible values are listed in {@link FontStyles}.
     *              See {@link FontStyles#BOLD}, {@link FontStyles#ITALIC}, {@link FontStyles#NORMAL},
     *              {@link FontStyles#BOLDITALIC}, {@link FontStyles#UNDEFINED}
     * @param cached whether to try to get the font program from cache
     * @return created {@link FontProgram}
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createRegisteredFont(String fontName, int style, boolean cached) throws java.io.IOException {
        return fontRegisterProvider.getFont(fontName, style, cached);
    }

    /**
     * Creates a FontProgram from the font file that has been previously registered.
     *
     * @param fontName either a font alias, if the font file has been registered with an alias,
     *                 or just a font name otherwise
     * @param style the style of the font to look for. Possible values are listed in {@link FontStyles}.
     *              See {@link FontStyles#BOLD}, {@link FontStyles#ITALIC}, {@link FontStyles#NORMAL},
     *              {@link FontStyles#BOLDITALIC}, {@link FontStyles#UNDEFINED}
     * @return created {@link FontProgram}
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static FontProgram createRegisteredFont(String fontName, int style) throws java.io.IOException {
        return fontRegisterProvider.getFont(fontName, style);
    }

    /**
     * Creates a FontProgram from the font file that has been previously registered.
     *
     * @param fontName either a font alias, if the font file has been registered with an alias,
     *                 or just a font name otherwise
     * @return created {@link FontProgram}
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading the file2
     */
    public static FontProgram createRegisteredFont(String fontName) throws java.io.IOException {
        return fontRegisterProvider.getFont(fontName, FontStyles.UNDEFINED);
    }

    /**
     * Register a font by giving explicitly the font family and name.
     *
     * @param familyName the font family
     * @param fullName   the font name
     * @param path       the font path
     */
    public static void registerFontFamily(String familyName, String fullName, String path) {
        fontRegisterProvider.registerFontFamily(familyName, fullName, path);
    }

    /**
     * Registers a .ttf, .otf, .afm, .pfm, or a .ttc font file.
     * In case if TrueType Collection (.ttc), an additional parameter may be specified defining the index of the font
     * to be registered, e.g. "path/to/font/collection.ttc,0". The index is zero-based.
     *
     * @param path the path to a font file
     */
    public static void registerFont(String path) {
        registerFont(path, null);
    }

    /**
     * Register a font file and use an alias for the font contained in it.
     *
     * @param path  the path to a font file
     * @param alias the alias you want to use for the font
     */
    public static void registerFont(String path, String alias) {
        fontRegisterProvider.registerFont(path, alias);
    }

    /**
     * Register all the fonts in a directory.
     *
     * @param dir the directory
     * @return the number of fonts registered
     */
    public static int registerFontDirectory(String dir) {
        return fontRegisterProvider.registerFontDirectory(dir);
    }

    /**
     * Register fonts in some probable directories. It usually works in Windows,
     * Linux and Solaris.
     *
     * @return the number of fonts registered
     */
    public static int registerSystemFontDirectories() {
        return fontRegisterProvider.registerSystemFontDirectories();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered fonts
     */
    public static Set<String> getRegisteredFonts() {
        return fontRegisterProvider.getRegisteredFonts();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered font families
     */
    public static Set<String> getRegisteredFontFamilies() {
        return fontRegisterProvider.getRegisteredFontFamilies();
    }

    /**
     * Checks if a certain font is registered.
     *
     * @param fontName the name of the font that has to be checked.
     * @return true if the font is found
     */
    public static boolean isRegisteredFont(String fontName) {
        return fontRegisterProvider.isRegisteredFont(fontName);
    }

    private static FontProgram createType1Font(String metricsPath, String binaryPath, byte[] afm, byte[] pfb, boolean cached) throws java.io.IOException {
        FontProgram fontProgram;
        FontCacheKey fontKey = null;
        if (cached) {
            fontKey = createFontCacheKey(metricsPath, afm);
            fontProgram = FontCache.getFont(fontKey);
            if (fontProgram != null) {
                return fontProgram;
            }
        }

        fontProgram = new Type1Font(metricsPath, binaryPath, afm, pfb);
        return cached ? FontCache.saveFont(fontProgram, fontKey) : fontProgram;
    }

    private static FontCacheKey createFontCacheKey(String name, byte[] fontProgram) {
        FontCacheKey key;
        if (name != null) {
            key = FontCacheKey.create(name);
        } else {
            key = FontCacheKey.create(fontProgram);
        }
        return key;
    }

    public static void clearRegisteredFonts() { fontRegisterProvider.clearRegisteredFonts(); }

    /**
     * Clears registered font cache
     */
    public static void clearRegisteredFontFamilies() { fontRegisterProvider.clearRegisteredFontFamilies(); }

    static byte[] readFontBytesFromPath(String path) throws java.io.IOException {
        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(path));
        int bufLen = (int) raf.length();
        if (bufLen < raf.length()) {
            throw new IOException(MessageFormatUtil.format("Source data from \"{0}\" is bigger than byte array can hold.", path));
        }
        byte[] buf = new byte[bufLen];
        raf.readFully(buf);
        return buf;
    }
}
