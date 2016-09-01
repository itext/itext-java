/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.CidFont;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;

import java.io.IOException;
import java.util.Set;

public final class PdfFontFactory {

    /**
     * This is the default encoding to use.
     */
    private static String DEFAULT_ENCODING = "";
    /**
     * This is the default value of the <VAR>embedded</VAR> variable.
     */
    private static boolean DEFAULT_EMBEDDING = false;
    /**
     * This is the default value of the <VAR>cached</VAR> variable.
     */
    private static boolean DEFAULT_CACHED = true;

    public static PdfFont createFont() throws IOException {
        return createFont(FontConstants.HELVETICA, PdfEncodings.WINANSI);
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

    public static PdfFont createFont(String fontProgram) throws IOException {
        return createFont(fontProgram, DEFAULT_ENCODING);
    }

    public static PdfFont createFont(String fontProgram, String encoding) throws IOException {
        return createFont(fontProgram, encoding, DEFAULT_EMBEDDING);
    }

    public static PdfFont createTtcFont(byte[] ttc, int ttcIndex, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(ttc, ttcIndex, cached);
        return createFont(fontProgram, encoding, embedded);
    }

    public static PdfFont createTtcFont(String ttc, int ttcIndex, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(ttc, ttcIndex, cached);
        return createFont(fontProgram, encoding, embedded);
    }

    public static PdfFont createFont(String fontProgram, boolean embedded) throws IOException {
        return createFont(fontProgram, DEFAULT_ENCODING, embedded);
    }

    public static PdfFont createFont(String fontProgram, String encoding, boolean embedded) throws IOException {
        return createFont(fontProgram, encoding, embedded, DEFAULT_CACHED);
    }

    public static PdfFont createFont(String fontProgram, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createFont(fontProgram, cached);
        return createFont(fp, encoding, embedded);
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
        return createFont(fontProgram, encoding, DEFAULT_EMBEDDING);
    }

    public static PdfFont createFont(FontProgram fontProgram) throws IOException {
        return createFont(fontProgram, DEFAULT_ENCODING);
    }

    public static PdfFont createFont(byte[] fontProgram, String encoding) throws IOException {
        return createFont(fontProgram, encoding, DEFAULT_EMBEDDING);
    }

    public static PdfFont createFont(byte[] fontProgram, boolean embedded) throws IOException {
        return createFont(fontProgram, null, embedded);
    }

    public static PdfFont createFont(byte[] fontProgram, String encoding, boolean embedded) throws IOException {
        return createFont(fontProgram, encoding, embedded, DEFAULT_CACHED);
    }

    public static PdfFont createFont(byte[] fontProgram, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createFont(null, fontProgram, cached);
        return createFont(fp, encoding, embedded);
    }

    public static PdfType3Font createType3Font(PdfDocument document, boolean colorized) throws IOException {
        return new PdfType3Font(document, colorized);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded, int style, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createRegisteredFont(fontName, style, cached);
        return createFont(fp, encoding, embedded);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded, boolean cached) throws IOException {
        return createRegisteredFont(fontName, encoding, embedded, FontConstants.UNDEFINED, cached);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded) throws IOException {
        return createRegisteredFont(fontName, encoding, embedded, FontConstants.UNDEFINED);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded, int style) throws IOException {
        return createRegisteredFont(fontName, encoding, embedded, style, DEFAULT_CACHED);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding) throws IOException {
        return createRegisteredFont(fontName, encoding, false, FontConstants.UNDEFINED);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName) throws IOException {
        return createRegisteredFont(fontName, null, false, FontConstants.UNDEFINED);
    }

    /**
     * Register a font by giving explicitly the font family and name.
     *
     * @param familyName the font family
     * @param fullName   the font name
     * @param path       the font path
     */
    public static void registerFamily(String familyName, String fullName, String path) {
        FontProgramFactory.registerFontFamily(familyName, fullName, path);
    }

    /**
     * Register a ttf- or a ttc-file.
     *
     * @param path the path to a ttf- or ttc-file
     */
    public static void register(String path) {
        register(path, null);
    }

    /**
     * Register a font file and use an alias for the font contained in it.
     *
     * @param path  the path to a font file
     * @param alias the alias you want to use for the font
     */
    public static void register(String path, String alias) {
        FontProgramFactory.registerFont(path, alias);
    }

    /**
     * Register all the fonts in a directory.
     *
     * @param dir the directory
     * @return the number of fonts registered
     */
    public static int registerDirectory(String dir) {
        return FontProgramFactory.registerFontDirectory(dir);
    }

    /**
     * Register fonts in some probable directories. It usually works in Windows,
     * Linux and Solaris.
     *
     * @return the number of fonts registered
     */
    public static int registerSystemDirectories() {
        return FontProgramFactory.registerSystemFontDirectories();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered fonts
     */
    public static Set<String> getRegisteredFonts() {
        return FontProgramFactory.getRegisteredFonts();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered font families
     */
    public static Set<String> getRegisteredFamilies() {
        return FontProgramFactory.getRegisteredFontFamilies();
    }

    /**
     * Checks if a certain font is registered.
     *
     * @param fontname the name of the font that has to be checked.
     * @return true if the font is found
     */
    public static boolean isRegistered(String fontname) {
        return FontProgramFactory.isRegisteredFont(fontname);
    }

    @Deprecated
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
