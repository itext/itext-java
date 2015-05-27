package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.AdobeGlyphList;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.font.*;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PdfTrueTypeFont extends PdfSimpleFont<TrueTypeFont> {


    /**
     * Forces the output of the width array. Only matters for the 14 built-in fonts.
     */
    protected boolean forceWidthsOutput = false;
    /**
     * Indicates if all the glyphs and widths for that particular encoding should be included in the document.
     */
    private boolean subset = false;
    /**
     * The array used with single byte encodings.
     */
    private byte[] shortTag = new byte[256];

    public PdfTrueTypeFont(PdfDocument pdfDocument, TrueTypeFont ttf, boolean embedded) throws PdfException {
        super(new PdfDictionary(), pdfDocument);
        fontProgram = ttf;
        this.embedded = embedded;
        if (embedded && !ttf.allowEmbedding()) {
            throw new PdfException("1.cannot.be.embedded.due.to.licensing.restrictions").setMessageParams(ttf.getFontName() + ttf.getStyle());
        }
    }

    public PdfTrueTypeFont(PdfDocument pdfDocument, TrueTypeFont trueTypeFont) throws PdfException {
        this(pdfDocument, trueTypeFont, false);
    }



    public PdfTrueTypeFont(PdfDocument pdfDocument, PdfDictionary fontDictionary) throws PdfException, IOException {
        super(new PdfDictionary(), pdfDocument);
        this.fontDictionary = fontDictionary;
        isCopy = true;
        checkTrueTypeFontDictionary();
        init();
    }

    public PdfTrueTypeFont(PdfDocument pdfDocument, PdfIndirectReference indirectReference) throws PdfException, IOException {
        this(pdfDocument, (PdfDictionary) indirectReference.getRefersTo());
    }

    /**
     * Returns the width of a certain character of this font.
     *
     * @param ch a certain character.
     * @return a width in Text Space.
     */
    public float getWidth(int ch) {
        return fontProgram.getWidth(ch);
    }

    /**
     * Returns the width of a string of this font.
     *
     * @param s a string content.
     * @return a width of string in Text Space.
     */
    public float getWidth(String s) {
        return fontProgram.getWidth(s);
    }

    /**
     * Gets the state of the property.
     *
     * @return value of property forceWidthsOutput
     */
    public boolean isForceWidthsOutput() {
        return forceWidthsOutput;
    }

    /**
     * Set to {@code true} to force the generation of the widths array.
     *
     * @param forceWidthsOutput {@code true} to force the generation of the widths array
     */
    public void setForceWidthsOutput(boolean forceWidthsOutput) {
        this.forceWidthsOutput = forceWidthsOutput;
    }

    @Override
    public byte[] convertToBytes(String text) {
        byte[] content = fontProgram.convertToBytes(text);
        for (byte b : content) {
            shortTag[b & 0xff] = 1;
        }
        return content;
    }

    @Override
    public void flush() throws PdfException {
        if (isCopy) {
            flushCopyFontData();
        } else {
            flushFontData();
        }
    }

    protected void addRangeUni(HashSet<Integer> longTag) {
        if (!subset && (subsetRanges != null || fontProgram.getDirectoryOffset() > 0)) {
            int[] rg = subsetRanges == null && fontProgram.getDirectoryOffset() > 0
                    ? new int[]{0, 0xffff} : compactRanges(subsetRanges);
            HashMap<Integer, int[]> usemap = fontProgram.getActiveCmap();
            assert usemap != null;
            for (Map.Entry<Integer, int[]> e : usemap.entrySet()) {
                int[] v = e.getValue();
                Integer gi = v[0];
                if (longTag.contains(gi)) {
                    continue;
                }
                int c = e.getKey();
                boolean skip = true;
                for (int k = 0; k < rg.length; k += 2) {
                    if (c >= rg[k] && c <= rg[k + 1]) {
                        skip = false;
                        break;
                    }
                }
                if (!skip) {
                    longTag.add(gi);
                }
            }
        }
    }


    /**
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     *
     * @return the PdfStream containing the font or {@code null}.
     * @throws PdfException if there is an error reading the font.
     */
    protected PdfStream getFontStream(byte[] fontStreamBytes, int[] fontStreamLengths) throws PdfException {
        if (fontStreamBytes == null) {
            return null;
        }
        PdfStream fontStream = new PdfStream(getDocument(), fontStreamBytes);
        for (int k = 0; k < fontStreamLengths.length; ++k) {
            fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
        }
        return fontStream;
    }

    private void flushFontData() throws PdfException {
        getPdfObject().put(PdfName.BaseFont, new PdfName(fontProgram.getFontName()));
        int firstChar;
        int lastChar;
        for (firstChar = 0; firstChar < 256; ++firstChar) {
            if (shortTag[firstChar] != 0) break;
        }
        for (lastChar = 255; lastChar >= firstChar; --lastChar) {
            if (shortTag[lastChar] != 0) break;
        }
        if (firstChar > 255) {
            firstChar = 255;
            lastChar = 255;
        }
        if (!(subset && embedded)) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; ++k) {
                shortTag[k] = 1;
            }
        }
        String fontName = fontProgram.getFontName() + fontProgram.getStyle();
        String baseEncoding = fontProgram.getEncoding().getBaseEncoding();
        String subsetPrefix = "";
        PdfStream fontStream = null;
        if (embedded) {
            if (fontProgram.isCff()) {
                try {
                    byte[] fontStreamBytes = fontProgram.getFontStreamBytes();
                    fontStream = getFontStream(fontStreamBytes, new int[]{fontStreamBytes.length});
                    fontStream.put(PdfName.Subtype, new PdfName("Type1C"));
                } catch (PdfException e) {
                    Logger logger = LoggerFactory.getLogger(PdfTrueTypeFont.class);
                    logger.error(e.getMessage());
                    fontStream = null;
                }
            } else {
                if (subset) {
                    subsetPrefix = createSubsetPrefix();
                }
                HashSet<Integer> glyphs = new HashSet<Integer>();
                for (int k = firstChar; k <= lastChar; ++k) {
                    if (shortTag[k] != 0) {
                        int[] metrics = null;
                        if (fontProgram.getEncoding().hasSpecialEncoding()) {
                            int[] cd = AdobeGlyphList.nameToUnicode(fontProgram.getEncoding().getDifferences(k));
                            if (cd != null) {
                                metrics = fontProgram.getMetrics(cd[0]);
                            }
                        } else {
                            if (fontProgram.getEncoding().isFontSpecific()) {
                                metrics = fontProgram.getMetrics(k);
                            } else {
                                metrics = fontProgram.getMetrics(fontProgram.getEncoding().getUnicodeDifferences(k));
                            }
                        }
                        if (metrics != null) {
                            glyphs.add(metrics[0]);
                        }
                    }
                }
                addRangeUni(glyphs);
                try {
                    byte[] fontStreamBytes;
                    if (subset || fontProgram.getDirectoryOffset() != 0 || subsetRanges != null) {
                        //clone glyphs due to possible cache issue
                        fontStreamBytes = fontProgram.getSubset(new HashSet<Integer>(glyphs), subset);
                    } else {
                        fontStreamBytes = fontProgram.getFontStreamBytes();
                    }
                    fontStream = getFontStream(fontStreamBytes, new int[]{fontStreamBytes.length});
                } catch (PdfException e) {
                    Logger logger = LoggerFactory.getLogger(PdfTrueTypeFont.class);
                    logger.error(e.getMessage());
                    fontStream = null;
                }
            }
        }

        if (fontProgram.isCff()) {
            getPdfObject().put(PdfName.Subtype, PdfName.Type1);
            getPdfObject().put(PdfName.BaseFont, new PdfName(fontName));
        } else {
            getPdfObject().put(PdfName.Subtype, PdfName.TrueType);
            getPdfObject().put(PdfName.BaseFont, new PdfName(subsetPrefix + fontName));
        }
        if (!fontProgram.getEncoding().isFontSpecific()) {
            for (int k = firstChar; k <= lastChar; ++k) {
                if (!fontProgram.getEncoding().getDifferences(k).equals(FontConstants.notdef)) {
                    firstChar = k;
                    break;
                }
            }
            if (baseEncoding.equals(PdfEncodings.CP1252) || baseEncoding.equals(PdfEncodings.MACROMAN)) {
                getPdfObject().put(PdfName.Encoding,
                        baseEncoding.equals(PdfEncodings.CP1252) ? PdfName.WinAnsiEncoding : PdfName.MacRomanEncoding);
            } else {
                PdfDictionary enc = new PdfDictionary();
                enc.put(PdfName.Type, PdfName.Encoding);
                PdfArray dif = new PdfArray();
                boolean gap = true;
                for (int k = firstChar; k <= lastChar; ++k) {
                    if (shortTag[k] != 0) {
                        if (gap) {
                            dif.add(new PdfNumber(k));
                            gap = false;
                        }
                        dif.add(new PdfName(fontProgram.getEncoding().getDifferences(k)));
                    } else {
                        gap = true;
                    }
                }
                enc.put(PdfName.Differences, dif);
                getPdfObject().put(PdfName.Encoding, enc);
            }
        }
        getPdfObject().put(PdfName.FirstChar, new PdfNumber(firstChar));
        getPdfObject().put(PdfName.LastChar, new PdfNumber(lastChar));
        PdfArray wd = new PdfArray();
        int[] widths = fontProgram.getRawWidths();
        for (int k = firstChar; k <= lastChar; ++k) {
            if (shortTag[k] == 0) {
                wd.add(new PdfNumber(0));
            } else {
                wd.add(new PdfNumber(widths[k]));
            }
        }
        getPdfObject().put(PdfName.Widths, wd);

        PdfDictionary fontDescriptor = getFontDescriptor(getDocument(), fontProgram, fontStream, subsetPrefix);
        getPdfObject().put(PdfName.FontDescriptor, fontDescriptor);
        fontDescriptor.flush();
    }

    private void flushCopyFontData() throws PdfException {
        super.flush();
    }


    /**
     * Generates the font descriptor for this font or {@code null} if it is one of the 14 built in fonts.
     *
     * @param fontStream   the PdfStream containing the font or {@code null}.
     * @param subsetPrefix the subset prefix.
     * @return the PdfDictionary containing the font descriptor or {@code null}.
     */
    protected static PdfDictionary getFontDescriptor(PdfDocument document, TrueTypeFont ttf, PdfStream fontStream, String subsetPrefix) throws PdfException {
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(document);
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(ttf.getFontDescriptor(FontConstants.ASCENT)));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(ttf.getFontDescriptor(FontConstants.DESCENT)));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(ttf.getFontDescriptor(FontConstants.CAPHEIGHT)));

        Rectangle fontBBox = new Rectangle(
                ttf.getFontDescriptor(FontConstants.BBOXLLX),
                ttf.getFontDescriptor(FontConstants.BBOXLLY),
                ttf.getFontDescriptor(FontConstants.BBOXURX),
                ttf.getFontDescriptor(FontConstants.BBOXURY)
        );
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontBBox));
        fontDescriptor.put(PdfName.FontName, new PdfName(subsetPrefix + ttf.getFontName() + ttf.getStyle()));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(ttf.getFontDescriptor(FontConstants.ITALICANGLE)));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(80));
        if (fontStream != null) {
            if (ttf.isCff()) {
                fontDescriptor.put(PdfName.FontFile3, fontStream);
            } else {
                fontDescriptor.put(PdfName.FontFile2, fontStream);
            }
        }
        fontDescriptor.put(PdfName.Flags, new PdfNumber(ttf.getFlags()));
        return fontDescriptor;
    }


    @Override
    protected TrueTypeFont initializeTypeFontForCopy(String encodingName) throws PdfException, IOException {
        return new TrueTypeFont(encodingName);
    }

    @Override
    protected TrueTypeFont initializeTypeFont(String fontName, String encodingName) throws IOException, PdfException {
        return new TrueTypeFont(fontName, encodingName, null);
    }
}
