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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.CidFont;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.constants.FontStyles;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;

import java.io.IOException;
import java.util.Set;

/**
 * This class provides helpful methods for creating fonts ready to be used in a {@link PdfDocument}
 * <p>
 * Note, just created {@link PdfFont} is almost empty until it will be flushed,
 * because it is impossible to fulfill font data until flush.
 */
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

    /**
     * Creates a new instance of default font, namely {@link StandardFonts#HELVETICA} standard font
     * with {@link PdfEncodings#WINANSI} encoding.
     * Note, if you want to reuse the same instance of default font, you may use
     * {@link PdfDocument#getDefaultFont()}.
     *
     * @return created font
     * @throws IOException if error occurred while creating the font, e.g. metrics loading failure
     */
    public static PdfFont createFont() throws IOException {
        return createFont(StandardFonts.HELVETICA, DEFAULT_ENCODING);
    }

    /**
     * Creates a {@link PdfFont} by already existing font dictionary.
     * <p>
     * Note, the font won't be added to any document,
     * until you add it to {@link com.itextpdf.kernel.pdf.canvas.PdfCanvas}.
     * While adding to {@link com.itextpdf.kernel.pdf.canvas.PdfCanvas}, or to
     * {@link com.itextpdf.kernel.pdf.PdfResources} the font will be made indirect implicitly.
     * <p>
     * {@link PdfDocument#getFont} method is strongly recommended if you want to get PdfFont by both
     * existing font dictionary, or just created and hasn't flushed yet.
     *
     * @param fontDictionary the font dictionary to create the font from
     * @return created {@link PdfFont} instance
     */
    public static PdfFont createFont(PdfDictionary fontDictionary) {
        if (checkFontDictionary(fontDictionary, PdfName.Type1, false)) {
            return new PdfType1Font(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.Type0, false)) {
            return new PdfType0Font(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.TrueType, false)) {
            return new PdfTrueTypeFont(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.Type3, false)) {
            return new PdfType3Font(fontDictionary);
        } if (checkFontDictionary(fontDictionary, PdfName.MMType1, false)) {
            // this very rare font type, that's why it was moved to the bottom of the if-else.
            return new PdfType1Font(fontDictionary);
        } else {
            throw new PdfException(PdfException.DictionaryDoesntHaveSupportedFontData);
        }
    }

    public static PdfFont createFont(String fontProgram, String encoding, PdfDocument cacheTo) throws IOException {
        PdfFont pdfFont;
        if (cacheTo != null) {
            pdfFont = cacheTo.findFont(fontProgram, encoding);
            if (pdfFont != null) {
                return pdfFont;
            }
        }

        pdfFont = createFont(fontProgram, encoding);
        if (cacheTo != null) pdfFont.makeIndirect(cacheTo);

        return pdfFont;
    }

    /**
     * Creates a {@link PdfFont} instance by the path of the font program file
     *
     * @param fontProgram the path of the font program file
     * @return created {@link PdfFont} instance
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static PdfFont createFont(String fontProgram) throws IOException {
        return createFont(fontProgram, DEFAULT_ENCODING);
    }

    /**
     * Creates a {@link PdfFont} instance by the path of the font program file and given encoding.
     *
     * @param fontProgram the path of the font program file
     * @param encoding    the font encoding. See {@link PdfEncodings}
     * @return created {@link PdfFont} instance
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static PdfFont createFont(String fontProgram, String encoding) throws IOException {
        return createFont(fontProgram, encoding, DEFAULT_EMBEDDING);
    }

    /**
     * Creates a {@link PdfFont} instance from the TrueType Collection represented by its byte contents.
     *
     * @param ttc      the byte contents of the TrueType Collection
     * @param ttcIndex the index of the font in the collection, zero-based
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     * @param cached   indicates whether the font will be cached
     * @return created {@link PdfFont} instance
     * @throws IOException in case the contents of the TrueType Collection is mal-formed or an error occurred during reading the font
     */
    public static PdfFont createTtcFont(byte[] ttc, int ttcIndex, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(ttc, ttcIndex, cached);
        return createFont(fontProgram, encoding, embedded);
    }

    /**
     * Creates a {@link PdfFont} instance from the TrueType Collection given by the path to the .ttc file.
     *
     * @param ttc      the path of the .ttc file
     * @param ttcIndex the index of the font in the collection, zero-based
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     * @param cached   indicates whether the font will be cached
     * @return created {@link PdfFont} instance
     * @throws IOException in case the file is not found, contents of the TrueType Collection is mal-formed
     *                     or an error occurred during reading the font
     */
    public static PdfFont createTtcFont(String ttc, int ttcIndex, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(ttc, ttcIndex, cached);
        return createFont(fontProgram, encoding, embedded);
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param embedded    indicates whether the font is to be embedded into the target document
     * @return created {@link PdfFont} instance
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     */
    public static PdfFont createFont(String fontProgram, boolean embedded) throws IOException {
        return createFont(fontProgram, DEFAULT_ENCODING, embedded);
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param encoding    the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded    indicates whether the font is to be embedded into the target document
     * @return created {@link PdfFont} instance
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     */
    public static PdfFont createFont(String fontProgram, String encoding, boolean embedded) throws IOException {
        return createFont(fontProgram, encoding, embedded, DEFAULT_CACHED);
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param encoding    the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded    indicates whether the font is to be embedded into the target document
     * @param cached      indicates whether the font will be cached
     * @return created {@link PdfFont} instance
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     */
    public static PdfFont createFont(String fontProgram, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createFont(fontProgram, cached);
        return createFont(fp, encoding, embedded);
    }

    /**
     * Created a {@link PdfFont} instance given the given underlying {@link FontProgram} instance.
     *
     * @param fontProgram the font program of the {@link PdfFont} instance to be created
     * @param encoding    the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded    indicates whether the font is to be embedded into the target document
     * @return created {@link PdfFont} instance
     */
    public static PdfFont createFont(FontProgram fontProgram, String encoding, boolean embedded) {
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

    /**
     * Created a {@link PdfFont} instance given the given underlying {@link FontProgram} instance.
     *
     * @param fontProgram the font program of the {@link PdfFont} instance to be created
     * @param encoding    the encoding of the font to be created. See {@link PdfEncodings}
     * @return created {@link PdfFont} instance
     */
    public static PdfFont createFont(FontProgram fontProgram, String encoding) {
        return createFont(fontProgram, encoding, DEFAULT_EMBEDDING);
    }

    /**
     * Created a {@link PdfFont} instance given the given underlying {@link FontProgram} instance.
     *
     * @param fontProgram the font program of the {@link PdfFont} instance to be created
     * @return created {@link PdfFont} instance
     */
    public static PdfFont createFont(FontProgram fontProgram) {
        return createFont(fontProgram, DEFAULT_ENCODING);
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param encoding    the encoding of the font to be created. See {@link PdfEncodings}
     * @return created {@link PdfFont} instance
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static PdfFont createFont(byte[] fontProgram, String encoding) throws IOException {
        return createFont(fontProgram, encoding, DEFAULT_EMBEDDING);
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param embedded    indicates whether the font is to be embedded into the target document
     * @return created {@link PdfFont} instance
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static PdfFont createFont(byte[] fontProgram, boolean embedded) throws IOException {
        return createFont(fontProgram, null, embedded);
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param encoding    the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded    indicates whether the font is to be embedded into the target document
     * @return created {@link PdfFont} instance
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static PdfFont createFont(byte[] fontProgram, String encoding, boolean embedded) throws IOException {
        return createFont(fontProgram, encoding, embedded, DEFAULT_CACHED);
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param encoding    the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded    indicates whether the font is to be embedded into the target document
     * @param cached      indicates whether the font will be cached
     * @return created {@link PdfFont} instance
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static PdfFont createFont(byte[] fontProgram, String encoding, boolean embedded, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createFont(fontProgram, cached);
        return createFont(fp, encoding, embedded);
    }

    /**
     * Creates a new instance of {@link PdfType3Font}
     *
     * @param document  the target document of the new font
     * @param colorized indicates whether the font will be colorized
     * @return created font
     */
    public static PdfType3Font createType3Font(PdfDocument document, boolean colorized) {
        return new PdfType3Font(document, colorized);
    }

    /**
     * Creates a new instance of {@link PdfType3Font}
     *
     * @param document   the target document of the new font.
     * @param fontName   the PostScript name of the font, shall not be null or empty.
     * @param fontFamily a preferred font family name.
     * @param colorized  indicates whether the font will be colorized
     * @return created font.
     */
    public static PdfType3Font createType3Font(PdfDocument document, String fontName, String fontFamily, boolean colorized) {
        return new PdfType3Font(document, fontName, fontFamily, colorized);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param style    Font style from {@link FontStyles}.
     * @param cached   If true font will be cached for another PdfDocument
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
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
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param cached   If true font will be cached for another PdfDocument
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded, boolean cached) throws IOException {
        return createRegisteredFont(fontName, encoding, embedded, FontStyles.UNDEFINED, cached);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded) throws IOException {
        return createRegisteredFont(fontName, encoding, embedded, FontStyles.UNDEFINED);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param style    Font style from {@link FontStyles}.
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
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
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding) throws IOException {
        return createRegisteredFont(fontName, encoding, false, FontStyles.UNDEFINED);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s.
     *
     * @param fontName Path to font file or Standard font name
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName) throws IOException {
        return createRegisteredFont(fontName, null, false, FontStyles.UNDEFINED);
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
     * Registers a .ttf, .otf, .afm, .pfm, or a .ttc font file.
     * In case if TrueType Collection (.ttc), an additional parameter may be specified defining the index of the font
     * to be registered, e.g. "path/to/font/collection.ttc,0". The index is zero-based.
     *
     * @param path the path to a font file
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
     * Registers all the fonts in a directory.
     *
     * @param dirPath the directory path to be registered as a font directory path
     * @return the number of fonts registered
     */
    public static int registerDirectory(String dirPath) {
        return FontProgramFactory.registerFontDirectory(dirPath);
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
     * Gets a set of registered font families.
     *
     * @return a set of registered font families
     */
    public static Set<String> getRegisteredFamilies() {
        return FontProgramFactory.getRegisteredFontFamilies();
    }

    /**
     * Checks if a certain font is registered.
     *
     * @param fontName the name of the font that has to be checked.
     * @return <code>true</code> if the font is found, <code>false</code> otherwise
     */
    public static boolean isRegistered(String fontName) {
        return FontProgramFactory.isRegisteredFont(fontName);
    }

    /**
     * Checks if the provided dictionary is a valid font dictionary of the provided font type.
     *
     * @return <code>true</code> if the passed dictionary is a valid dictionary, <code>false</code> otherwise
     */
    private static boolean checkFontDictionary(PdfDictionary fontDic, PdfName fontType, boolean isException) {
        if (fontDic == null || fontDic.get(PdfName.Subtype) == null
                || !fontDic.get(PdfName.Subtype).equals(fontType)) {
            if (isException) {
                throw new PdfException(PdfException.DictionaryDoesntHave1FontData).setMessageParams(fontType.getValue());
            }
            return false;
        }
        return true;
    }
}
