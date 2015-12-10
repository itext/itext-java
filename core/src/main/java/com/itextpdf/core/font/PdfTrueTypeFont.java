package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.*;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note. For TrueType FontNames.getStyle() is the same to Subfamily(). So, we shouldn't add style to /BaseFont.
 */
public class PdfTrueTypeFont extends PdfSimpleFont<TrueTypeFont> {

    public PdfTrueTypeFont(PdfDocument pdfDocument, TrueTypeFont ttf, String encoding, boolean embedded) {
        super(pdfDocument, new PdfDictionary());
        setFontProgram(ttf);
        this.embedded = embedded;
        FontNames fontNames = ttf.getFontNames();
        if (embedded && !fontNames.allowEmbedding()) {
            throw new PdfException("1.cannot.be.embedded.due.to.licensing.restrictions")
                    .setMessageParams(fontNames.getFontName());
        }
        fontEncoding = new FontEncoding(encoding, ttf.isFontSpecific());
    }

    public PdfTrueTypeFont(PdfDocument pdfDocument, TrueTypeFont trueTypeFont, String encoding) {
        this(pdfDocument, trueTypeFont, encoding, false);
    }

    public PdfTrueTypeFont(PdfDocument pdfDocument, TrueTypeFont trueTypeFont) {
        this(pdfDocument, trueTypeFont, PdfEncodings.WINANSI, false);
    }

    public PdfTrueTypeFont(PdfDocument pdfDocument, PdfDictionary fontDictionary) throws IOException {
        super(pdfDocument,fontDictionary,true);
        checkTrueTypeFontDictionary(fontDictionary);
        init();
    }

    public PdfTrueTypeFont(PdfDocument pdfDocument, PdfIndirectReference indirectReference) throws IOException {
        this(pdfDocument, (PdfDictionary) indirectReference.getRefersTo());
    }

    public Glyph getGlyph(int ch) {
        if (fontEncoding.canEncode(ch)) {
            return getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(ch));
        }
        // TODO to return notdef -> override
        return null;
    }

    @Override
    public void flush() {
        if (isCopy) {
            flushCopyFontData();
        } else {
            PdfName subtype;
            String fontName;
            if (getFontProgram().isCff()) {
                subtype = PdfName.Type1;
                fontName = fontProgram.getFontNames().getFontName();
            } else {
                subtype = PdfName.TrueType;
                fontName = subset
                        ? createSubsetPrefix() + fontProgram.getFontNames().getFontName()
                        : fontProgram.getFontNames().getFontName();
            }
            flushFontData(fontName, subtype);
        }
    }

    protected void addRangeUni(HashSet<Integer> longTag) {
        if (!subset && (subsetRanges != null || getFontProgram().getDirectoryOffset() > 0)) {
            int[] rg = subsetRanges == null && getFontProgram().getDirectoryOffset() > 0
                    ? new int[]{0, 0xffff} : compactRanges(subsetRanges);
            HashMap<Integer, int[]> usemap = getFontProgram().getActiveCmap();
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

    protected PdfStream getFontStream() {
        if (embedded) {
            PdfStream fontStream;
            if (getFontProgram().isCff()) {
                try {
                    byte[] fontStreamBytes = getFontProgram().getFontStreamBytes();
                    fontStream = getPdfFontStream(fontStreamBytes, new int[]{fontStreamBytes.length});
                    fontStream.put(PdfName.Subtype, new PdfName("Type1C"));
                } catch (PdfException e) {
                    Logger logger = LoggerFactory.getLogger(PdfTrueTypeFont.class);
                    logger.error(e.getMessage());
                    fontStream = null;
                }
            } else {
                HashSet<Integer> glyphs = new HashSet<>();
                for (int k = 0; k < shortTag.length; k++) {
                    if (shortTag[k] != 0) {
                        Glyph glyph = fontProgram.getGlyph(fontEncoding.getUnicode(k));
                        if (glyph != null) {
                            glyphs.add(glyph.index);
                        }
                    }
                }
                addRangeUni(glyphs);
                try {
                    byte[] fontStreamBytes;
                    if (subset || getFontProgram().getDirectoryOffset() != 0 || subsetRanges != null) {
                        //clone glyphs due to possible cache issue
                        fontStreamBytes = getFontProgram().getSubset(new HashSet<>(glyphs), subset);
                    } else {
                        fontStreamBytes = getFontProgram().getFontStreamBytes();
                    }
                    fontStream = getPdfFontStream(fontStreamBytes, new int[]{fontStreamBytes.length});
                } catch (PdfException e) {
                    Logger logger = LoggerFactory.getLogger(PdfTrueTypeFont.class);
                    logger.error(e.getMessage());
                    fontStream = null;
                }
            }
            return fontStream;
        } else {
            return null;
        }
    }

    private void flushCopyFontData() {
        super.flush();
    }


    /**
     * Generates the font descriptor for this font or {@code null} if it is one of the 14 built in fonts.
     *
     * @param fontStream   the PdfStream containing the font or {@code null}.
     * @param fontName a name of the font.
     * @return the PdfDictionary containing the font descriptor or {@code null}.
     */
    protected PdfDictionary getFontDescriptor(PdfStream fontStream, String fontName) {
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(getDocument());
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
        Rectangle fontBBox = new Rectangle(getFontProgram().getFontMetrics().getBbox().clone());
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontBBox));
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(getFontProgram().getFontMetrics().getTypoAscender()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(getFontProgram().getFontMetrics().getTypoDescender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(getFontProgram().getFontMetrics().getCapHeight()));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(getFontProgram().getFontMetrics().getItalicAngle()));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(getFontProgram().getFontMetrics().getStemV()));
        fontDescriptor.put(PdfName.Flags, new PdfNumber(getFontProgram().getPdfFontFlags()));
        if (fontStream != null) {
            if (getFontProgram().isCff()) {
                fontDescriptor.put(PdfName.FontFile3, fontStream);
            } else {
                fontDescriptor.put(PdfName.FontFile2, fontStream);
            }
        }

        return fontDescriptor;
    }


    @Override
    protected TrueTypeFont initializeTypeFontForCopy(String encodingName) {
        throw new RuntimeException();
    }

    @Override
    protected TrueTypeFont initializeTypeFont(String fontName, String encodingName) throws IOException {
        return new TrueTypeFont(fontName);
    }
}
