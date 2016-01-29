package com.itextpdf.io.font;

import com.itextpdf.io.IOException;

import java.util.Arrays;
import java.util.Set;

/**
 * Provides methods for creating various types of fonts.
 */
public class FontFactory {

    private static FontRegisterProvider fontRegisterProvider = new FontRegisterProvider();

    /**
     * Creates a new font. This will always be the default Helvetica font (not embedded).
     * This method is introduced because Helvetica is used in many examples.
     *
     * @return a BaseFont object (Helvetica, Winansi, not embedded)
     */
    public static FontProgram createFont() throws java.io.IOException {
        return createFont(FontConstants.HELVETICA);
    }

    /**
     * Creates a new font. This font can be one of the 14 built in types,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font (simple or collection) or a CJK font from the
     * Adobe Asian Font Pack. TrueType fonts and CJK fonts can have an optional style modifier
     * appended to the name. These modifiers are: Bold, Italic and BoldItalic. An
     * example would be "STSong-Light,Bold". Note that this modifiers do not work if
     * the font is embedded. Fonts in TrueType collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p/>
     * The fonts are cached and if they already exist they are extracted from the cache,
     * not parsed again.
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     * <p/>
     * This method calls:<br>
     * <PRE>
     * createFont(name, encoding, embedded, true, null, null);
     * </PRE>
     *
     * @param name     the name of the font or its location on file
     * @return returns a new font. This font may come from the cache
     */
    public static FontProgram createFont(String name) throws java.io.IOException {
        return createFont(name, true, null, null, false);
    }

    /**
     * Creates a new font. This font can be one of the 14 built in types,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font (simple or collection) or a CJK font from the
     * Adobe Asian Font Pack. TrueType fonts and CJK fonts can have an optional style modifier
     * appended to the name. These modifiers are: Bold, Italic and BoldItalic. An
     * example would be "STSong-Light,Bold". Note that this modifiers do not work if
     * the font is embedded. Fonts in TrueType collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p/>
     * The fonts are cached and if they already exist they are extracted from the cache,
     * not parsed again.
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     * <p/>
     * This method calls:<br>
     * <PRE>
     * createFont(name, encoding, embedded, true, null, null);
     * </PRE>
     *
     * @param name     the name of the font or its location on file
     * @param cached   ttrue if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @return returns a new font. This font may come from the cache
     */
    public static FontProgram createFont(String name, boolean cached) throws java.io.IOException {
        return createFont(name, cached, null, null, false);
    }

    /**
     * Creates a new font. This font can be one of the 14 built in types,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font (simple or collection) or a CJK font from the
     * Adobe Asian Font Pack. TrueType fonts and CJK fonts can have an optional style modifier
     * appended to the name. These modifiers are: Bold, Italic and BoldItalic. An
     * example would be "STSong-Light,Bold". Note that this modifiers do not work if
     * the font is embedded. Fonts in TrueType collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     *
     * @param ttfAfm   the true type font or the afm in a byte array
     *                 an exception if the font is not recognized. Note that even if true an exception may be thrown in some circumstances.
     *                 This parameter is useful for FontFactory that may have to check many invalid font names before finding the right one
     * @return returns a new font. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     */
    public static FontProgram createFont(byte[] ttfAfm) throws java.io.IOException {
        return createFont(null, false, ttfAfm, null);
    }


    /**
     * Creates a new font. This font can be one of the 14 built in types,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font (simple or collection) or a CJK font from the
     * Adobe Asian Font Pack. TrueType fonts and CJK fonts can have an optional style modifier
     * appended to the name. These modifiers are: Bold, Italic and BoldItalic. An
     * example would be "STSong-Light,Bold". Note that this modifiers do not work if
     * the font is embedded. Fonts in TrueType collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     *
     * @param ttfAfm   the true type font or the afm in a byte array
     * @param pfb      the pfb in a byte array
     *                 an exception if the font is not recognized. Note that even if true an exception may be thrown in some circumstances.
     *                 This parameter is useful for FontFactory that may have to check many invalid font names before finding the right one
     * @return returns a new font. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     */
    public static FontProgram createFont(byte[] ttfAfm, byte[] pfb) throws java.io.IOException {
        return createFont(null, false, ttfAfm, pfb);
    }

    /**
     * Creates a new font. This font can be one of the 14 built in types,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font (simple or collection) or a CJK font from the
     * Adobe Asian Font Pack. TrueType fonts and CJK fonts can have an optional style modifier
     * appended to the name. These modifiers are: Bold, Italic and BoldItalic. An
     * example would be "STSong-Light,Bold". Note that this modifiers do not work if
     * the font is embedded. Fonts in TrueType collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p/>
     * The fonts may or may not be cached depending on the flag <CODE>cached</CODE>.
     * If the <CODE>byte</CODE> arrays are present the font will be
     * read from them instead of the name. A name is still required to identify
     * the font type.
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     *
     * @param name     the name of the font or its location on file
     * @param cached   true if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @param ttfAfm   the true type font or the afm in a byte array
     * @param pfb      the pfb in a byte array
     * @return returns a new font. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     */
    public static FontProgram createFont(String name, boolean cached, byte[] ttfAfm, byte[] pfb) throws java.io.IOException {
        return createFont(name, cached, ttfAfm, pfb, false);
    }

    /**
     * Creates a new font. This font can be one of the 14 built in types,
     * a Type1 font referred to by an AFM or PFM file, a TrueType font (simple or collection) or a CJK font from the
     * Adobe Asian Font Pack. TrueType fonts and CJK fonts can have an optional style modifier
     * appended to the name. These modifiers are: Bold, Italic and BoldItalic. An
     * example would be "STSong-Light,Bold". Note that this modifiers do not work if
     * the font is embedded. Fonts in TrueType collections are addressed by index such as "msgothic.ttc,1".
     * This would get the second font (indexes start at 0), in this case "MS PGothic".
     * <p/>
     * The fonts may or may not be cached depending on the flag <CODE>cached</CODE>.
     * If the <CODE>byte</CODE> arrays are present the font will be
     * read from them instead of the name. A name is still required to identify
     * the font type.
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     *
     * @param name     the name of the font or its location on file
     * @param cached   true if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @param ttfAfm   the true type font or the afm in a byte array
     * @param pfb      the pfb in a byte array
     * @param noThrow  if true will not throw an exception if the font is not recognized and will return null, if false will throw
     *                 an exception if the font is not recognized. Note that even if true an exception may be thrown in some circumstances.
     *                 This parameter is useful for FontFactory that may have to check many invalid font names before finding the right one
     * @return returns a new font. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     */
    public static FontProgram createFont(String name, boolean cached, byte[] ttfAfm, byte[] pfb, boolean noThrow) throws java.io.IOException {
        String baseName = FontProgram.getBaseName(name);

        //yes, we trying to find built-in standard font with original name, not baseName.
        boolean isBuiltinFonts14 = FontConstants.BUILTIN_FONTS_14.contains(name);
        boolean isCidFont = !isBuiltinFonts14 && FontCache.isPredefinedCidFont(baseName);

        FontProgram fontFound;

        if (cached && name != null) {
            fontFound = FontCache.getFont(name);
            if (fontFound != null) {
                return fontFound;
            }
        }

        if (name == null) {
            if (pfb != null) {
                try {
                    return Type1Font.createFont(ttfAfm, pfb);
                } catch (Exception ignored) {
                }
            }
            if (ttfAfm != null) {
                try {
                    return new TrueTypeFont(ttfAfm);
                } catch (Exception ignored) {
                }

                try {
                    return Type1Font.createFont(ttfAfm);
                } catch (Exception ignored) {
                }
            }
            if (noThrow) {
                return null;
            } else {
                throw new IOException(IOException.FontIsNotRecognized);
            }
        }

        FontProgram fontBuilt;

        if (isBuiltinFonts14 || name.toLowerCase().endsWith(".afm") || name.toLowerCase().endsWith(".pfm")) {
            fontBuilt = new Type1Font(name, null, ttfAfm, pfb);
        } else if (baseName.toLowerCase().endsWith(".ttf") || baseName.toLowerCase().endsWith(".otf") || baseName.toLowerCase().indexOf(".ttc,") > 0) {
            if (ttfAfm != null) {
                fontBuilt = new TrueTypeFont(ttfAfm);
            } else {
                fontBuilt = new TrueTypeFont(name);
            }
        } else if (isCidFont) {
            fontBuilt = new CidFont(name, FontCache.getCompatibleCmaps(baseName));
        } else if (noThrow) {
            return null;
        } else {
            throw new IOException(IOException.Font1IsNotRecognized).setMessageParams(name);
        }

        if (cached) {
            fontFound = FontCache.getFont(name);
            if (fontFound != null) {
                return fontFound;
            }
            FontCache.saveFont(fontBuilt, name);
        }

        return fontBuilt;
    }


    /**
     * Creates a new True Type font from ttc file,
     * <p/>
     * The fonts may or may not be cached depending on the flag <CODE>cached</CODE>.
     * If the <CODE>byte</CODE> arrays are present the font will be
     * read from them instead of the name. A name is still required to identify
     * the font type.
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     *
     * @param ttcPath  location  of true type collection file (*.ttc)
     * @param ttcIndex the encoding to be applied to this font
     * @param cached   true if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @return returns a new font. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     */
    public static FontProgram createFont(String ttcPath, int ttcIndex, boolean cached) throws java.io.IOException {
        if (cached) {
            FontProgram fontFound = FontCache.getFont(ttcPath + ttcIndex);
            if (fontFound != null) {
                return fontFound;
            }
        }
        FontProgram fontBuilt = new TrueTypeFont(ttcPath, ttcIndex);
        if (cached) {
            FontCache.saveFont(fontBuilt, ttcPath + ttcIndex);
        }

        return fontBuilt;
    }

    public static FontProgram createFont(String ttcPath, int ttcIndex) throws java.io.IOException {
        return createFont(ttcPath, ttcIndex, false);
    }

    /**
     * Creates a new True Type font from ttc bytes array,
     * <p/>
     * The fonts may or may not be cached depending on the flag <CODE>cached</CODE>.
     * If the <CODE>byte</CODE> arrays are present the font will be
     * read from them instead of the name. A name is still required to identify
     * the font type.
     * <p/>
     * Besides the common encodings described by name, custom encodings
     * can also be made. These encodings will only work for the single byte fonts
     * Type1 and TrueType. The encoding string starts with a '#'
     * followed by "simple" or "full". If "simple" there is a decimal for the first character position and then a list
     * of hex values representing the Unicode codes that compose that encoding.<br>
     * The "simple" encoding is recommended for TrueType fonts
     * as the "full" encoding risks not matching the character with the right glyph
     * if not done with care.<br>
     * The "full" encoding is specially aimed at Type1 fonts where the glyphs have to be
     * described by non standard names like the Tex math fonts. Each group of three elements
     * compose a code position: the one byte code order in decimal or as 'x' (x cannot be the space), the name and the Unicode character
     * used to access the glyph. The space must be assigned to character position 32 otherwise
     * text justification will not work.
     * <p/>
     * Example for a "simple" encoding that includes the Unicode
     * character space, A, B and ecyrillic:
     * <PRE>
     * "# simple 32 0020 0041 0042 0454"
     * </PRE>
     * <p/>
     * Example for a "full" encoding for a Type1 Tex font:
     * <PRE>
     * "# full 'A' nottriangeqlleft 0041 'B' dividemultiply 0042 32 space 0020"
     * </PRE>
     *
     * @param ttc      bytes array of ttc font
     * @param ttcIndex the encoding to be applied to this font
     * @param cached   true if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @return returns a new font. This font may come from the cache but only if cached
     * is true, otherwise it will always be created new
     */
    public static FontProgram createFont(byte[] ttc, int ttcIndex, boolean cached) throws java.io.IOException {

        if (cached) {
            String ttcNameKey = String.valueOf(Arrays.deepHashCode(new Object[]{ttc})) + ttcIndex;
            FontProgram fontFound = FontCache.getFont(ttcNameKey);
            if (fontFound != null) {
                return fontFound;
            }
        }
        FontProgram fontBuilt = new TrueTypeFont(ttc, ttcIndex);
        String ttcNameKey = String.valueOf(Arrays.deepHashCode(new Object[]{ttc})) + ttcIndex;
        if (cached) {
            FontCache.saveFont(fontBuilt, ttcNameKey);
        }

        return fontBuilt;
    }

    public static FontProgram createFont(byte[] ttc, int ttcIndex) throws java.io.IOException {
        return createFont(ttc, ttcIndex, false);
    }


    public static FontProgram createRegisteredFont(String fontName, int style, boolean cached) throws java.io.IOException {
        return fontRegisterProvider.getFont(fontName, style, cached);
    }

    public static FontProgram createRegisteredFont(String fontName, int style) throws java.io.IOException {
        return fontRegisterProvider.getFont(fontName, style);
    }

    public static FontProgram createRegisteredFont(String fontName) throws java.io.IOException {
        return fontRegisterProvider.getFont(fontName, FontConstants.UNDEFINED);
    }

    /**
     * Register a font by giving explicitly the font family and name.
     *
     * @param familyName the font family
     * @param fullName   the font name
     * @param path       the font path
     */
    public static void registerFamily(final String familyName, final String fullName, final String path) {
        fontRegisterProvider.registerFamily(familyName, fullName, path);
    }

    /**
     * Register a ttf- or a ttc-file.
     *
     * @param path the path to a ttf- or ttc-file
     */

    public static void register(final String path) {
        register(path, null);
    }

    /**
     * Register a font file and use an alias for the font contained in it.
     *
     * @param path  the path to a font file
     * @param alias the alias you want to use for the font
     */

    public static void register(final String path, final String alias) {
        fontRegisterProvider.register(path, alias);
    }

    /**
     * Register all the fonts in a directory.
     *
     * @param dir the directory
     * @return the number of fonts registered
     */
    public static int registerDirectory(final String dir) {
        return fontRegisterProvider.registerDirectory(dir);
    }

    /**
     * Register fonts in some probable directories. It usually works in Windows,
     * Linux and Solaris.
     *
     * @return the number of fonts registered
     */
    public static int registerSystemDirectories() {
        return fontRegisterProvider.registerSystemDirectories();
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

    public static Set<String> getRegisteredFamilies() {
        return fontRegisterProvider.getRegisteredFamilies();
    }

    /**
     * Gets a set of registered font names.
     *
     * @param fontname of a font that may or may not be registered
     * @return true if a given font is registered
     */

    public static boolean contains(final String fontname) {
        return fontRegisterProvider.isRegistered(fontname);
    }

    /**
     * Checks if a certain font is registered.
     *
     * @param fontname the name of the font that has to be checked.
     * @return true if the font is found
     */

    public static boolean isRegistered(final String fontname) {
        return fontRegisterProvider.isRegistered(fontname);
    }
}
