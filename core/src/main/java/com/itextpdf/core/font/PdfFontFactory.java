package com.itextpdf.core.font;

import com.itextpdf.core.PdfException;
import com.itextpdf.basics.font.*;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

import java.io.IOException;
import java.util.Set;

public final class PdfFontFactory {

    public static PdfFont createFont() throws IOException {
        return createStandardFont(FontConstants.HELVETICA, PdfEncodings.WINANSI);
    }

    public static PdfFont createFont(PdfDictionary fontDictionary) {
        if (checkFontDictionary(fontDictionary, PdfName.Type1, false)) {
            return new PdfType1Font(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.Type0, false)) {
            return new PdfType0Font(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.TrueType, false)) {
            return new PdfTrueTypeFont(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.Type3, false)) {
            return new PdfType3Font(fontDictionary);
        } else {
            throw new PdfException(PdfException.DictionaryNotContainFontData);
        }
    }

    public static PdfFont createFont(String path, String encoding) throws IOException {
        return createFont(path, encoding, false);
    }

    public static PdfFont createFont(byte[] ttc, int ttcIndex, String encoding) throws IOException {
        return createFont(ttc, ttcIndex, encoding, false);
    }

    public static PdfFont createFont(byte[] ttc, int ttcIndex, String encoding, boolean embedded) throws IOException {
        TrueTypeCollection collection = new TrueTypeCollection(ttc, encoding);
        FontProgram program = collection.getFontByTccIndex(ttcIndex);
        return new PdfTrueTypeFont((TrueTypeFont) program, encoding, embedded);
    }

    public static PdfFont createFont(String ttcPath, int ttcIndex, String encoding) throws IOException {
        return createFont(ttcPath, ttcIndex, encoding, false);
    }

    public static PdfFont createFont(String ttcPath, int ttcIndex, String encoding, boolean embedded) throws IOException {
        TrueTypeCollection collection = new TrueTypeCollection(ttcPath, encoding);
        FontProgram fontProgram = collection.getFontByTccIndex(ttcIndex);
        return createFont(fontProgram, encoding, embedded);
    }

    public static PdfFont createFont(String path, boolean embedded) throws IOException {
        return createFont(path, null, embedded);
    }

    public static PdfFont createFont(String path, String encoding, boolean embedded) throws IOException {
        FontProgram fontProgram = FontFactory.createFont(path);
        return createFont(fontProgram, encoding, embedded);
    }

    public static PdfFont createFont(FontProgram fontProgram, String encoding, boolean embedded) throws IOException {
        if (fontProgram == null) {
            return null;
        } else if (fontProgram instanceof Type1Font) {
            return new PdfType1Font((Type1Font) fontProgram, encoding, embedded);
        } else if (fontProgram instanceof TrueTypeFont) {
            if (PdfEncodings.IDENTITY_H.equals(encoding) || PdfEncodings.IDENTITY_V.equals(encoding)) {
                return new PdfType0Font((TrueTypeFont) fontProgram, encoding);
            } else {
                return new PdfTrueTypeFont((TrueTypeFont) fontProgram, encoding, embedded);
            }
        } else if (fontProgram instanceof CidFont) {
            if (((CidFont) fontProgram).compatibleWith(encoding)) {
                return new PdfType0Font((CidFont) fontProgram, encoding);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static PdfFont createFont(FontProgram fontProgram, String encoding) throws IOException {
        return createFont(fontProgram, encoding, false);
    }

    public static PdfFont createFont(FontProgram fontProgram) throws IOException {
        return createFont(fontProgram, PdfEncodings.WINANSI);
    }

    public static PdfFont createFont(byte[] font, String encoding) throws IOException {
        return createFont(font, encoding, false);
    }

    public static PdfFont createFont(byte[] font, boolean embedded) throws IOException {
        return createFont(font, null, embedded);
    }

    public static PdfFont createFont(byte[] font, String encoding, boolean embedded) throws IOException {
        FontProgram fontProgram = FontFactory.createFont(null, false, font, null, true);
        if (fontProgram == null) {
            return null;
        } else if (fontProgram instanceof Type1Font) {
            return new PdfType1Font((Type1Font) fontProgram, encoding, embedded);
        } else if (fontProgram instanceof TrueTypeFont) {
            if (PdfEncodings.IDENTITY_H.equals(encoding) || PdfEncodings.IDENTITY_V.equals(encoding)) {
                return new PdfType0Font((TrueTypeFont) fontProgram, encoding);
            } else {
                return new PdfTrueTypeFont((TrueTypeFont) fontProgram, encoding, embedded);
            }
        } else {
            return null;
        }
    }

    public static PdfFont createStandardFont(String name) throws IOException {
        return createStandardFont(name, null);
    }

    public static PdfFont createStandardFont(String name, String encoding) throws IOException {
        return new PdfType1Font(Type1Font.createStandardFont(name), encoding);
    }

    public static PdfFont createType1Font(String metrics) throws IOException {
        return createType1Font(metrics, null, null, false);
    }

    public static PdfFont createType1Font(String metrics, String encoding) throws IOException {
        return createType1Font(metrics, null, encoding, false);
    }

    public static PdfFont createType1Font(String metrics, String binary, String encoding) throws IOException {
        return createType1Font(metrics, binary, encoding, false);
    }

    public static PdfType3Font createType3Font(PdfDocument document, boolean colorized) throws IOException {
        return new PdfType3Font(document, colorized);
    }

    public static PdfFont createRegisteredFont(String fontName, final String encoding, boolean embedded, int style, boolean cached) throws IOException {
        FontProgram fontProgram = FontFactory.createRegisteredFont(fontName, style, cached);
        return createFont(fontProgram, encoding, embedded);
    }

    public static PdfFont createRegisteredFont(String fontName, final String encoding, boolean embedded) throws IOException {
        return createRegisteredFont(fontName, encoding, embedded, FontConstants.UNDEFINED);
    }

    public static PdfFont createRegisteredFont(String fontName, final String encoding, boolean embedded, int style) throws IOException {
        return createRegisteredFont(fontName, encoding, embedded, style, false);
    }

    public static PdfFont createRegisteredFont(String fontName, final String encoding) throws IOException {
        return createRegisteredFont(fontName, encoding, false, FontConstants.UNDEFINED, false);
    }

    public static PdfFont createRegisteredFont(String fontName) throws IOException {
        return createRegisteredFont(fontName, null, false, FontConstants.UNDEFINED, false);
    }

    /**
     * Register a font by giving explicitly the font family and name.
     *
     * @param familyName the font family
     * @param fullName   the font name
     * @param path       the font path
     */
    public static void registerFamily(final String familyName, final String fullName, final String path) {
        FontFactory.registerFamily(familyName, fullName, path);
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
        FontFactory.register(path, alias);
    }

    /**
     * Register all the fonts in a directory.
     *
     * @param dir the directory
     * @return the number of fonts registered
     */
    public static int registerDirectory(final String dir) {
        return FontFactory.registerDirectory(dir);
    }

    /**
     * Register fonts in some probable directories. It usually works in Windows,
     * Linux and Solaris.
     *
     * @return the number of fonts registered
     */
    public static int registerSystemDirectories() {
        return FontFactory.registerSystemDirectories();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered fonts
     */
    public static Set<String> getRegisteredFonts() {
        return FontFactory.getRegisteredFonts();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered font families
     */
    public static Set<String> getRegisteredFamilies() {
        return FontFactory.getRegisteredFamilies();
    }

    /**
     * Checks if a certain font is registered.
     *
     * @param fontname the name of the font that has to be checked.
     * @return true if the font is found
     */
    public static boolean isRegistered(final String fontname) {
        return FontFactory.isRegistered(fontname);
    }

    /**
     * Create {@see PdfType1Font}.
     *
     * @param metrics path to .afm or .pfm metrics file.
     * @param binary  .pfb binary file
     */
    public static PdfFont createType1Font(String metrics, String binary, String encoding, boolean embedded) throws IOException {
        return new PdfType1Font(Type1Font.createFont(metrics, binary), encoding, embedded);
    }

    /**
     * Create {@see PdfType1Font}.
     *
     * @param metrics path to .afm or .pfm metrics file.
     * @param binary  .pfb binary file
     */
    public static PdfFont createType1Font(String metrics, String binary, boolean embedded) throws IOException {
        return new PdfType1Font(Type1Font.createFont(metrics, binary), null, embedded);
    }

    public static PdfFont createType1Font(byte[] metrics) throws IOException {
        return createType1Font(metrics, null, null, false);
    }

    public static PdfFont createType1Font(byte[] metrics, String encoding) throws IOException {
        return createType1Font(metrics, null, encoding, false);
    }

    public static PdfFont createType1Font(byte[] metrics, byte[] binary, String encoding) throws IOException {
        return createType1Font(metrics, binary, encoding, false);
    }

    /**
     * Create {@see PdfType1Font}.
     *
     * @param metrics .afm or .pfm metrics file.
     * @param binary  .pfb binary file
     */
    public static PdfFont createType1Font(byte[] metrics, byte[] binary, boolean embedded) throws IOException {
        return new PdfType1Font(Type1Font.createFont(metrics, binary), null, embedded);
    }

    /**
     * Create {@see PdfType1Font}.
     *
     * @param metrics .afm or .pfm metrics file.
     * @param binary  .pfb binary file
     */
    public static PdfFont createType1Font(byte[] metrics, byte[] binary, String encoding, boolean embedded) throws IOException {
        return new PdfType1Font(Type1Font.createFont(metrics, binary), encoding, embedded);
    }

    protected static boolean checkFontDictionary(PdfDictionary fontDic, PdfName fontType, boolean isException) {
        if (fontDic == null || fontDic.get(PdfName.Subtype) == null
                || !fontDic.get(PdfName.Subtype).equals(fontType)) {
            if (isException) {
                throw new PdfException(PdfException.DictionaryNotContainFontData).setMessageParams(fontType.getValue());
            }
            return false;
        }
        return true;
    }
}
