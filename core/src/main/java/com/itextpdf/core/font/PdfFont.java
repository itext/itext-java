package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.CidFont;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontFactory;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.TrueTypeCollection;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfOutputStream;
import com.itextpdf.core.pdf.PdfStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class PdfFont extends PdfObjectWrapper<PdfDictionary> {

    protected static final byte[] emptyBytes = new byte[0];

    HashMap<Integer, Glyph> notdefGlyphs = new HashMap<>();

    /**
     * true if the font is to be embedded in the PDF.
     */
    //TODO mark as final
    protected boolean embedded = false;
    /**
     * Indicates if all the glyphs and widths for that particular encoding should be included in the document.
     */
    protected boolean subset = true;
    protected ArrayList<int[]> subsetRanges;

    public static PdfFont getDefaultFont(PdfDocument pdfDocument) throws IOException {
        return createStandardFont(pdfDocument, FontConstants.HELVETICA, null);
    }

    public static PdfFont createFont(PdfDictionary fontDictionary) {
        if (checkFontDictionary(fontDictionary, PdfName.Type1, false)) {
            return new PdfType1Font(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.Type0, false)) {
            return new PdfType0Font(fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.TrueType, false)) {
            return new PdfTrueTypeFont(fontDictionary);
        } else {
            throw new PdfException(PdfException.DictionaryNotContainFontData);
        }
    }

    public static PdfFont createFont(PdfDocument pdfDocument, String path, String encoding) throws IOException {
        return createFont(pdfDocument, path, encoding, false);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, byte[] ttc, int ttcIndex, String encoding) throws IOException {
        return createFont(pdfDocument, ttc, ttcIndex, encoding, false);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, byte[] ttc, int ttcIndex, String encoding, boolean embedded) throws IOException {
        TrueTypeCollection collection = new TrueTypeCollection(ttc, encoding);
        FontProgram program = collection.getFontByTccIndex(ttcIndex);
        return new PdfTrueTypeFont(pdfDocument, (TrueTypeFont) program, encoding, embedded);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, String ttcPath, int ttcIndex, String encoding) throws IOException {
        return createFont(pdfDocument, ttcPath, ttcIndex, encoding, false);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, String ttcPath, int ttcIndex, String encoding, boolean embedded) throws IOException {
        TrueTypeCollection collection = new TrueTypeCollection(ttcPath, encoding);
        FontProgram fontProgram = collection.getFontByTccIndex(ttcIndex);
        return createFont(pdfDocument, fontProgram, encoding, embedded);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, String path, boolean embedded) throws IOException {
        return createFont(pdfDocument, path, null, embedded);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, String path, String encoding, boolean embedded) throws IOException {
        FontProgram fontProgram = FontFactory.createFont(path);
        return createFont(pdfDocument, fontProgram, encoding, embedded);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, FontProgram fontProgram, String encoding, boolean embedded) throws IOException {
        if (fontProgram == null) {
            return null;
        } else if (fontProgram instanceof Type1Font) {
            return new PdfType1Font(pdfDocument, (Type1Font) fontProgram, encoding, embedded);
        } else if (fontProgram instanceof TrueTypeFont) {
            if (PdfEncodings.IDENTITY_H.equals(encoding) || PdfEncodings.IDENTITY_V.equals(encoding)) {
                return new PdfType0Font(pdfDocument, (TrueTypeFont) fontProgram, encoding);
            } else {
                return new PdfTrueTypeFont(pdfDocument, (TrueTypeFont) fontProgram, encoding, embedded);
            }
        } else if (fontProgram instanceof CidFont) {
            if (((CidFont) fontProgram).compatibleWith(encoding)) {
                return new PdfType0Font(pdfDocument, (CidFont) fontProgram, encoding);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static PdfFont createFont(PdfDocument pdfDocument, byte[] font, String encoding) throws IOException {
        return createFont(pdfDocument, font, encoding, false);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, byte[] font, boolean embedded) throws IOException {
        return createFont(pdfDocument, font, null, embedded);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, byte[] font, String encoding, boolean embedded) throws IOException {
        FontProgram fontProgram = FontFactory.createFont(null, false, font, null, true);
        if (fontProgram == null) {
            return null;
        } else if (fontProgram instanceof Type1Font) {
            return new PdfType1Font(pdfDocument, (Type1Font) fontProgram, encoding, embedded);
        } else if (fontProgram instanceof TrueTypeFont) {
            if (PdfEncodings.IDENTITY_H.equals(encoding) || PdfEncodings.IDENTITY_V.equals(encoding)) {
                return new PdfType0Font(pdfDocument, (TrueTypeFont) fontProgram, encoding);
            } else {
                return new PdfTrueTypeFont(pdfDocument, (TrueTypeFont) fontProgram, encoding, embedded);
            }
        } else {
            return null;
        }
    }

    public static PdfFont createStandardFont(PdfDocument pdfDocument, String name) throws IOException {
        return createStandardFont(pdfDocument, name, null);
    }

    public static PdfFont createStandardFont(PdfDocument pdfDocument, String name, String encoding) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createStandardFont(name), encoding);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics) throws IOException {
        return createType1Font(pdfDocument, metrics, null, null, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, null, encoding, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics, String binary, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, binary, encoding, false);
    }

    public static PdfFont createRegisteredFont(PdfDocument pdfDocument, String fontName, final String encoding, boolean embedded, int style, boolean cached) throws IOException {
        FontProgram fontProgram = FontFactory.createRegisteredFont(fontName, style, cached);
        return createFont(pdfDocument, fontProgram, encoding, embedded);
    }

    public static PdfFont createRegisteredFont(PdfDocument pdfDocument, String fontName, final String encoding,boolean embedded) throws IOException {
        return createRegisteredFont(pdfDocument, fontName, encoding, embedded, FontConstants.UNDEFINED);
    }

    public static PdfFont createRegisteredFont(PdfDocument pdfDocument, String fontName, final String encoding,boolean embedded, int style) throws IOException {
        return createRegisteredFont(pdfDocument, fontName, encoding, embedded, style, false);
    }

    public static PdfFont createRegisteredFont(PdfDocument pdfDocument, String fontName, final String encoding) throws IOException {
        return createRegisteredFont(pdfDocument, fontName, encoding, false, FontConstants.UNDEFINED, false);
    }

    public static PdfFont createRegisteredFont(PdfDocument pdfDocument, String fontName) throws IOException {
        return createRegisteredFont(pdfDocument, fontName, null, false, FontConstants.UNDEFINED, false);
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
     * Gets a set of registered font names.
     *
     * @param fontname of a font that may or may not be registered
     * @return true if a given font is registered
     */
    public static boolean contains(final String fontname) {
        return FontFactory.isRegistered(fontname);
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
    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics, String binary, String encoding, boolean embedded) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createFont(metrics, binary), encoding, embedded);
    }

    /**
     * Create {@see PdfType1Font}.
     *
     * @param metrics path to .afm or .pfm metrics file.
     * @param binary  .pfb binary file
     */
    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics, String binary, boolean embedded) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createFont(metrics, binary), null, embedded);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics) throws IOException {
        return createType1Font(pdfDocument, metrics, null, null, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, null, encoding, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics, byte[] binary, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, binary, encoding, false);
    }

    /**
     * Create {@see PdfType1Font}.
     *
     * @param metrics .afm or .pfm metrics file.
     * @param binary  .pfb binary file
     */
    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics, byte[] binary, boolean embedded) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createFont(metrics, binary), null, embedded);
    }

    /**
     * Create {@see PdfType1Font}.
     *
     * @param metrics .afm or .pfm metrics file.
     * @param binary  .pfb binary file
     */
    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics, byte[] binary, String encoding, boolean embedded) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createFont(metrics, binary), encoding, embedded);
    }

    protected PdfFont(PdfDictionary fontDictionary) {
        super(fontDictionary);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    protected PdfFont(PdfDocument document) {
        super(new PdfDictionary());
        makeIndirect(document);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    //TODO as abstract + comments!
    public Glyph getGlyph(int ch) {throw new RuntimeException();}

    public boolean containsGlyph(char ch) {
        return getGlyph(ch) != null;
    }

    //TODO remove
    public GlyphLine createGlyphLine(String content) {
        throw new RuntimeException();
    }

    /**
     * Converts the text into bytes to be placed in the document.
     * The conversion is done according to the font and the encoding and the characters
     * used are stored.
     *
     * @param text the text to convert
     * @return the conversion
     */
    //TODO abstract
    public byte[] convertToBytes(String text) {
        //TODO when implement document fonts, throw exception
        //throw new IllegalStateException();
        return PdfEncodings.convertToBytes(text, "");
    }

    //TODO abstract
    public byte[] convertToBytes(GlyphLine glyphLine) {
        // TODO implement correctly for all fonts after moved to GlyphLines without intermediate unicode conversion
        // convert to printable array
//        StringBuilder sb = new StringBuilder();
//        for (int i = glyphLine.start; i < glyphLine.end; i++) {
//            if (glyphLine.glyphs.get(i).getUnicode() != null) {
//                sb.append(Utilities.convertFromUtf32(glyphLine.glyphs.get(i).getUnicode()));
//            }
//        }
//        return convertToBytes(sb.toString());
        throw new RuntimeException();
    }

    //TODO abstract
    public byte[] convertToBytes(Glyph glyph) {
        throw new RuntimeException();
    }

    //TODO abstract
    //TODO or writePdfString?
    public void writeText(GlyphLine text, int from, int to, PdfOutputStream stream) {
        throw new RuntimeException();
    }

    //TODO abstract
    //TODO or writePdfString?
    public void writeText(String text, PdfOutputStream stream) {
        throw new RuntimeException();
    }

    //TODO or writePdfString?
    public void writeText(GlyphLine text, PdfOutputStream stream) {
        writeText(text, 0, text.size() - 1, stream);
    }

    public double[] getFontMatrix() {
        return FontConstants.DefaultFontMatrix;
    }

    /**
     * Returns the width of a certain character of this font in 1000 normalized units.
     *
     * @param ch a certain character.
     * @return a width in Text Space.
     */
    public int getWidth(int ch) {
        // TODO abstract method
        throw new IllegalStateException();
    }

    /**
     * Returns the width of a string of this font in 1000 normalized units.
     *
     * @param s a string content.
     * @return a width of string in Text Space.
     */
    public int getWidth(String s) {
        // TODO abstract method
        throw new IllegalStateException();
    }

    /**
     * Gets the width of a {@code String} in points.
     *
     * @param text     the {@code String} to get the width of
     * @param fontSize the font size
     * @return the width in points
     */
    public float getWidthPoint(String text, float fontSize) {
        return getWidth(text) * fontSize / FontProgram.UNITS_NORMALIZATION;
    }

    /**
     * Gets the width of a {@code char} in points.
     *
     * @param ch       the {@code char} to get the width of
     * @param fontSize the font size
     * @return the width in points
     */
    public float getWidthPoint(int ch, float fontSize) {
        return getWidth(ch) * fontSize / FontProgram.UNITS_NORMALIZATION;
    }

    /**
     * Gets the descent of a {@code String} in normalized 1000 units. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
     * @param text the {@code String} to get the descent of
     * @return the descent in normalized 1000 units
     */
    public int getDescent(String text) {
        // TODO abstract method
        throw new IllegalStateException();
    }

    /**
     * Gets the descent of a char code in normalized 1000 units. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
     * @param ch the char code to get the descent of
     * @return the descent in normalized 1000 units
     */
    public int getDescent(int ch) {
        // TODO abstract method
        throw new IllegalStateException();

    }

    /**
     * Gets the ascent of a {@code String} in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param text the {@code String} to get the ascent of
     * @return the ascent in normalized 1000 units
     */
    public int getAscent(String text) {
        // TODO abstract method
        throw new IllegalStateException();

    }

    /**
     * Gets the ascent of a char code in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param ch the char code to get the ascent of
     * @return the ascent in normalized 1000 units
     */
    public int getAscent(int ch) {
        // TODO abstract method
        throw new IllegalStateException();

    }

    public boolean hasKernPairs() {
        FontProgram fontProgram = getFontProgram();
        return fontProgram != null && fontProgram.hasKernPairs();
    }

    public int getKerning(Glyph glyph1, Glyph glyph2) {
        FontProgram fontProgram = getFontProgram();
        if (fontProgram != null) {
            return fontProgram.getKerning(glyph1, glyph2);
        } else {
            return 0;
        }
    }

    public int getKerning(int char1, int char2) {
        FontProgram fontProgram = getFontProgram();
        if (fontProgram != null) {
            return fontProgram.getKerning(char1, char2);
        } else {
            return 0;
        }
    }

    /**
     * Applies kerning to this {@see GlyphLine} using advance glyph transformations
     * @param text the {@see GlyphLine} to be kerned
     * @return {@code true}, if any kern positioning applied
     */
    public boolean applyKerning(GlyphLine text) {
        boolean transformed = false;
        if (text.size() > 0) {
            for (int iter = 1; iter < text.size(); iter++) {
                int kern = getKerning(text.glyphs.get(iter - 1), text.glyphs.get(iter));
                if (kern != 0) {
                    text.glyphs.set(iter - 1, new Glyph(text.glyphs.get(iter - 1), 0, 0, kern, 0, 0));
                    transformed = true;
                }
            }
        }
        return transformed;
    }

    public FontProgram getFontProgram() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public boolean isEmbedded() {
        return embedded;
    }

    /**
     * Indicates if all the glyphs and widths for that particular
     * encoding should be included in the document.
     *
     * @return <CODE>false</CODE> to include all the glyphs and widths.
     */
    public boolean isSubset() {
        return subset;
    }

    /**
     * Indicates if all the glyphs and widths for that particular
     * encoding should be included in the document. When set to <CODE>true</CODE>
     * only the glyphs used will be included in the font. When set to <CODE>false</CODE>
     * and {@link #addSubsetRange(int[])} was not called the full font will be included
     * otherwise just the characters ranges will be included.
     *
     * @param subset new value of property subset
     */
    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    /**
     * Adds a character range when subsetting. The range is an <CODE>int</CODE> array
     * where the first element is the start range inclusive and the second element is the
     * end range inclusive. Several ranges are allowed in the same array.
     *
     * @param range the character range
     */
    //TODO
    public void addSubsetRange(int[] range) {
        if (subsetRanges == null) {
            subsetRanges = new ArrayList<>();
        }
        subsetRanges.add(range);
    }

    @Override
    public PdfFont copy(PdfDocument document) {
        throw new RuntimeException("Not implemented");
        //return new PdfFont(document, (PdfDictionary) getPdfObject().copyToDocument(document));
    }

    public List<String> splitString(String text, int fontSize, float maxWidth) {
        List<String> resultString = new ArrayList<>();
        int lastWhiteSpace = 0;
        int startPos = 0;

        float tokenLength = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isWhitespace(ch)) {
                lastWhiteSpace = i;
            }
            tokenLength += getWidthPoint(ch, fontSize);
            if (tokenLength >= maxWidth || ch == '\n') {
                if(startPos < lastWhiteSpace) {
                    resultString.add(text.substring(startPos, lastWhiteSpace));
                    startPos = lastWhiteSpace + 1;
                    tokenLength = 0;
                    i = lastWhiteSpace;
                }else{
                    resultString.add(text.substring(startPos, i+1));
                    startPos = i+1;
                    tokenLength = 0;
                    i=i+1;
                }
            }

        }

        resultString.add(text.substring(startPos));
        return resultString;
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

    protected boolean checkFontDictionary(PdfDictionary fontDic, PdfName fontType) {
        return checkFontDictionary(fontDic, fontType, true);
    }

    protected boolean checkTrueTypeFontDictionary(PdfDictionary fontDic) {
        return checkTrueTypeFontDictionary(fontDic, true);
    }

    protected boolean checkTrueTypeFontDictionary(PdfDictionary fontDic, boolean isException) {
        if (fontDic == null || fontDic.get(PdfName.Subtype) == null
                || !(fontDic.get(PdfName.Subtype).equals(PdfName.TrueType) || fontDic.get(PdfName.Subtype).equals(PdfName.Type1))) {
            if (isException) {
                throw new PdfException(PdfException.DictionaryNotContainFontData).setMessageParams(PdfName.TrueType.getValue());
            }
            return false;
        }
        return true;
    }

    /**
     * Creates a unique subset prefix to be added to the font name when the font is embedded and subset.
     *
     * @return the subset prefix
     */
    protected static String createSubsetPrefix() {
        StringBuilder s = new StringBuilder("");
        for (int k = 0; k < 6; ++k) {
            s.append((char) (Math.random() * 26 + 'A'));
        }
        return s + "+";
    }

    /**
     * TODO strange comments
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     *
     * @return the PdfStream containing the font or {@code null}, if there is an error reading the font.
     * @exception PdfException Method will throw exception if {@code fontStreamBytes} is {@code null}.
     */
    protected PdfStream getPdfFontStream(byte[] fontStreamBytes, int[] fontStreamLengths) {
        if (fontStreamBytes == null) {
            throw new PdfException(PdfException.FontEmbeddingIssue);
        }
        PdfStream fontStream = new PdfStream(fontStreamBytes).makeIndirect(getDocument());
        for (int k = 0; k < fontStreamLengths.length; ++k) {
            fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
        }
        return fontStream;
    }

    protected static int[] compactRanges(ArrayList<int[]> ranges) {
        ArrayList<int[]> simp = new ArrayList<>();
        for (int[] range : ranges) {
            for (int j = 0; j < range.length; j += 2) {
                simp.add(new int[]{Math.max(0, Math.min(range[j], range[j + 1])), Math.min(0xffff, Math.max(range[j], range[j + 1]))});
            }
        }
        for (int k1 = 0; k1 < simp.size() - 1; ++k1) {
            for (int k2 = k1 + 1; k2 < simp.size(); ++k2) {
                int[] r1 = simp.get(k1);
                int[] r2 = simp.get(k2);
                if (r1[0] >= r2[0] && r1[0] <= r2[1] || r1[1] >= r2[0] && r1[0] <= r2[1]) {
                    r1[0] = Math.min(r1[0], r2[0]);
                    r1[1] = Math.max(r1[1], r2[1]);
                    simp.remove(k2);
                    --k2;
                }
            }
        }
        int[] s = new int[simp.size() * 2];
        for (int k = 0; k < simp.size(); ++k) {
            int[] r = simp.get(k);
            s[k * 2] = r[0];
            s[k * 2 + 1] = r[1];
        }
        return s;
    }
}
