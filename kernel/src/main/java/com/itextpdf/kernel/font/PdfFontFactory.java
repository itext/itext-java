/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
import com.itextpdf.kernel.pdf.PdfObject;

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
    private static final String DEFAULT_ENCODING = "";
    /**
     * This is the default value of the <VAR>embeddedStrategy</VAR> variable.
     */
    private static final EmbeddingStrategy DEFAULT_EMBEDDING = EmbeddingStrategy.PREFER_NOT_EMBEDDED;
    /**
     * This is the default value of the <VAR>cached</VAR> variable.
     */
    private static final boolean DEFAULT_CACHED = true;

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
        if (fontDictionary == null) {
            throw new PdfException(PdfException.CannotCreateFontFromNullFontDictionary);
        }
        PdfObject subtypeObject = fontDictionary.get(PdfName.Subtype);
        if (PdfName.Type1.equals(subtypeObject)) {
            return new PdfType1Font(fontDictionary);
        } else if (PdfName.Type0.equals(subtypeObject)) {
            return new PdfType0Font(fontDictionary);
        } else if (PdfName.TrueType.equals(subtypeObject)) {
            return new PdfTrueTypeFont(fontDictionary);
        } else if (PdfName.Type3.equals(subtypeObject)) {
            return new PdfType3Font(fontDictionary);
        } else if (PdfName.MMType1.equals(subtypeObject)) {
            // this very rare font type, that's why it was moved to the bottom of the if-else.
            return new PdfType1Font(fontDictionary);
        } else {
            throw new PdfException(PdfException.DictionaryDoesntHaveSupportedFontData);
        }
    }

    /**
     * Creates a {@link PdfFont} instance by the path of the font program file and given encoding
     * and place it inside the {@link PdfDocument}. If such {@link PdfFont} has already been created
     * and placed inside the {@link PdfDocument}, then retries its instance instead of creating.
     *
     * @param fontProgram the path of the font program file
     * @param encoding the font encoding. See {@link PdfEncodings}
     * @param cacheTo the {@link PdfDocument} to cache the font
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     */
    public static PdfFont createFont(String fontProgram, String encoding, PdfDocument cacheTo) throws IOException {
        if (cacheTo == null) {
            return createFont(fontProgram, encoding);
        }

        PdfFont pdfFont = cacheTo.findFont(fontProgram, encoding);
        if (pdfFont == null) {
            pdfFont = createFont(fontProgram, encoding);
            if (pdfFont != null) {
                pdfFont.makeIndirect(cacheTo);
            }
        }

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
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param embedded indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createFont(String, EmbeddingStrategy)} instead
     */
    @Deprecated
    public static PdfFont createFont(String fontProgram, boolean embedded) throws IOException {
        return createFont(fontProgram, getEmbeddingStrategy(embedded));
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createFont(String, String, EmbeddingStrategy)} instead
     */
    @Deprecated
    public static PdfFont createFont(String fontProgram, String encoding, boolean embedded) throws IOException {
        return createFont(fontProgram, encoding, getEmbeddingStrategy(embedded));
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createFont(String, String, EmbeddingStrategy, boolean)} instead
     */
    @Deprecated
    public static PdfFont createFont(String fontProgram, String encoding, boolean embedded,
            boolean cached) throws IOException {
        return createFont(fontProgram, encoding, getEmbeddingStrategy(embedded), cached);
    }

    /**
     * Created a {@link PdfFont} instance given the given underlying {@link FontProgram} instance.
     *
     * @param fontProgram the font program of the {@link PdfFont} instance to be created
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createFont(FontProgram, String, EmbeddingStrategy)} instead
     */
    @Deprecated
    public static PdfFont createFont(FontProgram fontProgram, String encoding, boolean embedded) {
        return createFont(fontProgram, encoding, getEmbeddingStrategy(embedded));
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     */
    public static PdfFont createFont(String fontProgram, EmbeddingStrategy embeddingStrategy) throws IOException {
        return createFont(fontProgram, DEFAULT_ENCODING, embeddingStrategy);
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     */
    public static PdfFont createFont(String fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy) throws IOException {
        return createFont(fontProgram, encoding, embeddingStrategy, DEFAULT_CACHED);
    }

    /**
     * Created a {@link PdfFont} instance given the path to the font file.
     *
     * @param fontProgram the font program file
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found or the contents of the font file is mal-formed
     */
    public static PdfFont createFont(String fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createFont(fontProgram, cached);
        return createFont(fp, encoding, embeddingStrategy);
    }

    /**
     * Created a {@link PdfFont} instance given the given underlying {@link FontProgram} instance.
     *
     * @param fontProgram the font program of the {@link PdfFont} instance to be created
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     */
    public static PdfFont createFont(FontProgram fontProgram, String encoding, EmbeddingStrategy embeddingStrategy) {
        if (fontProgram == null) {
            return null;
        } else if (fontProgram instanceof Type1Font) {
            return createFontFromType1FontProgram((Type1Font) fontProgram, encoding, embeddingStrategy);
        } else if (fontProgram instanceof TrueTypeFont) {
            if (PdfEncodings.IDENTITY_H.equals(encoding) || PdfEncodings.IDENTITY_V.equals(encoding)) {
                return createType0FontFromTrueTypeFontProgram((TrueTypeFont) fontProgram, encoding, embeddingStrategy);
            } else {
                return createTrueTypeFontFromTrueTypeFontProgram(
                        (TrueTypeFont) fontProgram, encoding, embeddingStrategy);
            }
        } else if (fontProgram instanceof CidFont) {
            return createType0FontFromCidFontProgram((CidFont) fontProgram, encoding, embeddingStrategy);
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
     * @param embedded indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException signals that an I/O exception has occurred.
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createFont(byte[], EmbeddingStrategy)} instead
     */
    @Deprecated
    public static PdfFont createFont(byte[] fontProgram, boolean embedded) throws IOException {
        return createFont(fontProgram, getEmbeddingStrategy(embedded));
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException signals that an I/O exception has occurred.
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createFont(byte[], String, EmbeddingStrategy)} instead
     */
    @Deprecated
    public static PdfFont createFont(byte[] fontProgram, String encoding, boolean embedded) throws IOException {
        return createFont(fontProgram, encoding, getEmbeddingStrategy(embedded));
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException signals that an I/O exception has occurred.
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createFont(byte[], String, EmbeddingStrategy, boolean)} instead
     */
    @Deprecated
    public static PdfFont createFont(byte[] fontProgram, String encoding,
            boolean embedded, boolean cached) throws IOException {
        return createFont(fontProgram, encoding, getEmbeddingStrategy(embedded), cached);
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static PdfFont createFont(byte[] fontProgram, EmbeddingStrategy embeddingStrategy) throws IOException {
        return createFont(fontProgram, DEFAULT_ENCODING, embeddingStrategy);
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static PdfFont createFont(byte[] fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy) throws IOException {
        return createFont(fontProgram, encoding, embeddingStrategy, DEFAULT_CACHED);
    }

    /**
     * Created a {@link PdfFont} instance by the bytes of the underlying font program.
     *
     * @param fontProgram the bytes of the underlying font program
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static PdfFont createFont(byte[] fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createFont(fontProgram, cached);
        return createFont(fp, encoding, embeddingStrategy);
    }

    /**
     * Creates a {@link PdfFont} instance from the TrueType Collection represented by its byte contents.
     *
     * @param ttc the byte contents of the TrueType Collection
     * @param ttcIndex the index of the font in the collection, zero-based
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the contents of the TrueType Collection is mal-formed or an error
     * occurred during reading the font
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createTtcFont(byte[], int, String, EmbeddingStrategy, boolean)} instead
     */
    @Deprecated
    public static PdfFont createTtcFont(byte[] ttc, int ttcIndex, String encoding, boolean embedded,
            boolean cached) throws IOException {
        return createTtcFont(ttc, ttcIndex, encoding, getEmbeddingStrategy(embedded), cached);
    }

    /**
     * Creates a {@link PdfFont} instance from the TrueType Collection given by the path to the .ttc file.
     *
     * @param ttc the path of the .ttc file
     * @param ttcIndex the index of the font in the collection, zero-based
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embedded indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found, contents of the TrueType Collection is mal-formed
     *                     or an error occurred during reading the font
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createTtcFont(String, int, String, EmbeddingStrategy, boolean)} instead
     */
    @Deprecated
    public static PdfFont createTtcFont(String ttc, int ttcIndex, String encoding, boolean embedded,
            boolean cached) throws IOException {
        return createTtcFont(ttc, ttcIndex, encoding, getEmbeddingStrategy(embedded), cached);
    }

    /**
     * Creates a {@link PdfFont} instance from the TrueType Collection represented by its byte
     * contents.
     *
     * @param ttc the byte contents of the TrueType Collection
     * @param ttcIndex the index of the font in the collection, zero-based
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the contents of the TrueType Collection is mal-formed or an error
     * occurred during reading the font
     */
    public static PdfFont createTtcFont(byte[] ttc, int ttcIndex, String encoding,
            EmbeddingStrategy embeddingStrategy, boolean cached) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(ttc, ttcIndex, cached);
        return createFont(fontProgram, encoding, embeddingStrategy);
    }

    /**
     * Creates a {@link PdfFont} instance from the TrueType Collection given by the path to the .ttc file.
     *
     * @param ttc the path of the .ttc file
     * @param ttcIndex the index of the font in the collection, zero-based
     * @param encoding the encoding of the font to be created. See {@link PdfEncodings}
     * @param embeddingStrategy indicates whether the font is to be embedded into the target document
     * @param cached indicates whether the font will be cached
     *
     * @return created {@link PdfFont} instance
     *
     * @throws IOException in case the file is not found, contents of the TrueType Collection is mal-formed
     *                     or an error occurred during reading the font
     */
    public static PdfFont createTtcFont(String ttc, int ttcIndex, String encoding,
            EmbeddingStrategy embeddingStrategy, boolean cached) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(ttc, ttcIndex, cached);
        return createFont(fontProgram, encoding, embeddingStrategy);
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
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param style Font style from {@link FontStyles}.
     * @param cached If true font will be cached for another PdfDocument
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createRegisteredFont(String, String, EmbeddingStrategy, int, boolean)} instead
     */
    @Deprecated
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded,
            int style, boolean cached) throws IOException {
        return createRegisteredFont(fontName, encoding, getEmbeddingStrategy(embedded), style, cached);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param cached If true font will be cached for another PdfDocument
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createRegisteredFont(String, String, EmbeddingStrategy, boolean)} instead
     */
    @Deprecated
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded,
            boolean cached) throws IOException {
        return createRegisteredFont(fontName, encoding, getEmbeddingStrategy(embedded), cached);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createRegisteredFont(String, String, EmbeddingStrategy)} instead
     */
    @Deprecated
    public static PdfFont createRegisteredFont(String fontName, String encoding, boolean embedded) throws IOException {
        return createRegisteredFont(fontName, encoding, getEmbeddingStrategy(embedded));
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embedded if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param style Font style from {@link FontStyles}.
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     *
     * @deprecated Will be removed in next major release. Use
     * {@link PdfFontFactory#createRegisteredFont(String, String, EmbeddingStrategy, int)} instead
     */
    @Deprecated
    public static PdfFont createRegisteredFont(String fontName, String encoding,
            boolean embedded, int style) throws IOException {
        return createRegisteredFont(fontName, encoding, getEmbeddingStrategy(embedded), style);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embeddingStrategy if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param style Font style from {@link FontStyles}.
     * @param cached If true font will be cached for another PdfDocument
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding,
            EmbeddingStrategy embeddingStrategy, int style, boolean cached) throws IOException {
        FontProgram fp = FontProgramFactory.createRegisteredFont(fontName, style, cached);
        return createFont(fp, encoding, embeddingStrategy);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embeddingStrategy if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param cached If true font will be cached for another PdfDocument
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding,
            EmbeddingStrategy embeddingStrategy, boolean cached) throws IOException {
        return createRegisteredFont(fontName, encoding, embeddingStrategy, FontStyles.UNDEFINED, cached);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embeddingStrategy if true font will be embedded. Note, standard font won't be embedded in any case.
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding,
            EmbeddingStrategy embeddingStrategy) throws IOException {
        return createRegisteredFont(fontName, encoding, embeddingStrategy, FontStyles.UNDEFINED);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @param embeddingStrategy if true font will be embedded. Note, standard font won't be embedded in any case.
     * @param style Font style from {@link FontStyles}.
     *
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
     *
     * @throws IOException exception is thrown in case an I/O error occurs when reading the file
     *
     * @see PdfFontFactory#register(String)
     * @see PdfFontFactory#register(String, String)
     * @see PdfFontFactory#registerFamily(String, String, String)
     * @see PdfFontFactory#registerDirectory(String)
     * @see PdfFontFactory#registerSystemDirectories()
     * @see PdfFontFactory#getRegisteredFamilies()
     * @see PdfFontFactory#getRegisteredFonts()
     */
    public static PdfFont createRegisteredFont(String fontName, String encoding,
            EmbeddingStrategy embeddingStrategy, int style) throws IOException {
        return createRegisteredFont(fontName, encoding, embeddingStrategy, style, DEFAULT_CACHED);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @param encoding Font encoding from {@link PdfEncodings}.
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
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
        return createRegisteredFont(fontName, encoding, DEFAULT_EMBEDDING);
    }

    /**
     * Creates {@link PdfFont} based on registered {@link FontProgram}'s. Required font program is expected to be
     * previously registered by one of the register method from {@link PdfFontFactory}.
     *
     * @param fontName Path to font file or Standard font name
     * @return created font if required {@link FontProgram} was found among registered, otherwise null.
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
        return createRegisteredFont(fontName, DEFAULT_ENCODING);
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

    private static PdfType1Font createFontFromType1FontProgram(Type1Font fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy) {
        boolean embedded;
        switch (embeddingStrategy) {
            case FORCE_EMBEDDED:
                if (fontProgram.isBuiltInFont()) {
                    throw new PdfException(PdfException.CannotEmbedStandardFont);
                }
                embedded = true;
                break;
            case PREFER_EMBEDDED:
                // can not embed standard fonts
                embedded = !fontProgram.isBuiltInFont();
                break;
            case PREFER_NOT_EMBEDDED:
            case FORCE_NOT_EMBEDDED:
                embedded = false;
                break;
            default:
                throw new PdfException(PdfException.UnsupportedFontEmbeddingStrategy);
        }
        return new PdfType1Font(fontProgram, encoding, embedded);
    }

    private static PdfType0Font createType0FontFromTrueTypeFontProgram(TrueTypeFont fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy) {
        if (!fontProgram.getFontNames().allowEmbedding()) {
            throw new PdfException(PdfException.CannotBeEmbeddedDueToLicensingRestrictions).setMessageParams(
                    fontProgram.getFontNames().getFontName() + fontProgram.getFontNames().getStyle());
        }
        switch (embeddingStrategy) {
            case FORCE_EMBEDDED:
            case PREFER_EMBEDDED:
            case PREFER_NOT_EMBEDDED:
                // always embedded
                return new PdfType0Font(fontProgram, encoding);
            case FORCE_NOT_EMBEDDED:
                throw new PdfException(PdfException.CannotCreateType0FontWithTrueTypeFontProgramWithoutEmbedding);
            default:
                throw new PdfException(PdfException.UnsupportedFontEmbeddingStrategy);
        }
    }

    private static PdfTrueTypeFont createTrueTypeFontFromTrueTypeFontProgram(TrueTypeFont fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy) {
        boolean embedded;
        switch (embeddingStrategy) {
            case FORCE_EMBEDDED:
                if (!fontProgram.getFontNames().allowEmbedding()) {
                    throw new PdfException(PdfException.CannotBeEmbeddedDueToLicensingRestrictions).setMessageParams(
                                    fontProgram.getFontNames().getFontName() + fontProgram.getFontNames().getStyle());
                }
                embedded = true;
                break;
            case PREFER_EMBEDDED:
                embedded = fontProgram.getFontNames().allowEmbedding();
                break;
            case PREFER_NOT_EMBEDDED:
            case FORCE_NOT_EMBEDDED:
                embedded = false;
                break;
            default:
                throw new PdfException(PdfException.UnsupportedFontEmbeddingStrategy);
        }
        return new PdfTrueTypeFont(fontProgram, encoding, embedded);
    }

    private static PdfType0Font createType0FontFromCidFontProgram(CidFont fontProgram, String encoding,
            EmbeddingStrategy embeddingStrategy) {
        if (!fontProgram.compatibleWith(encoding)) {
            return null;
        }
        switch (embeddingStrategy) {
            case FORCE_EMBEDDED:
                throw new PdfException(PdfException.CannotEmbedType0FontWithCidFontProgram);
            case PREFER_EMBEDDED:
            case PREFER_NOT_EMBEDDED:
            case FORCE_NOT_EMBEDDED:
                // always not embedded
                return new PdfType0Font(fontProgram, encoding);
            default:
                throw new PdfException(PdfException.UnsupportedFontEmbeddingStrategy);
        }
    }

    private static EmbeddingStrategy getEmbeddingStrategy(boolean embedded) {
        return embedded ? EmbeddingStrategy.PREFER_EMBEDDED : EmbeddingStrategy.PREFER_NOT_EMBEDDED;
    }

    /**
     * Enum values for font embedding strategies.
     */
    public enum EmbeddingStrategy {
        /**
         * Force embedding fonts. It expected to get an exception if the font cannot be embedded.
         */
        FORCE_EMBEDDED,
        /**
         * Force not embedding fonts. It is expected to get an exception if the font cannot be
         * not embedded.
         */
        FORCE_NOT_EMBEDDED,
        /**
         * Embedding fonts if possible.
         */
        PREFER_EMBEDDED,
        /**
         * Not embedding fonts if possible.
         */
        PREFER_NOT_EMBEDDED
    }
}
