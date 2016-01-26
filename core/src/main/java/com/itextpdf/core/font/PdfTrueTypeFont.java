package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontEncoding;
import com.itextpdf.basics.font.FontNames;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.font.cmap.CMapToUnicode;
import com.itextpdf.basics.font.otf.Glyph;

import java.util.Map;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfStream;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note. For TrueType FontNames.getStyle() is the same to Subfamily(). So, we shouldn't add style to /BaseFont.
 */
public class PdfTrueTypeFont extends PdfSimpleFont<TrueTypeFont> {

    public PdfTrueTypeFont(TrueTypeFont ttf, String encoding, boolean embedded) {
        super();
        setFontProgram(ttf);
        this.embedded = embedded;
        FontNames fontNames = ttf.getFontNames();
        if (embedded && !fontNames.allowEmbedding()) {
            throw new PdfException("1.cannot.be.embedded.due.to.licensing.restrictions")
                    .setMessageParams(fontNames.getFontName());
        }
        if ((encoding == null || encoding.length() == 0) && ttf.isFontSpecific()) {
            encoding = FontEncoding.FontSpecific;
        }
        if (encoding != null && FontEncoding.FontSpecific.toLowerCase().equals(encoding.toLowerCase())) {
            fontEncoding = FontEncoding.createFontSpecificEncoding();
        } else {
            fontEncoding = FontEncoding.createFontEncoding(encoding);
        }
    }

    public PdfTrueTypeFont(TrueTypeFont ttf, String encoding) {
        this(ttf, encoding, false);
    }

    public PdfTrueTypeFont(TrueTypeFont ttf) {
        this(ttf, null, false);
    }

    public PdfTrueTypeFont(PdfDictionary fontDictionary) {
        super(fontDictionary);
        checkFontDictionary(fontDictionary, PdfName.TrueType);

        CMapToUnicode toUni = FontUtils.processToUnicode(fontDictionary.get(PdfName.ToUnicode));
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), toUni);
        fontProgram = DocTrueTypeFont.createFontProgram(fontDictionary, fontEncoding);
        embedded = ((DocFontProgram) fontProgram).getFontFile() != null;
        subset = false;
    }

    @Override
    public Glyph getGlyph(int unicode) {
        if (fontEncoding.canEncode(unicode)) {
            Glyph glyph = getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode));
            //TODO TrueType what if font is specific?
            if (glyph == null && (glyph = notdefGlyphs.get(unicode)) == null) {
                Glyph notdef = getFontProgram().getGlyphByCode(0);
                if (notdef != null) {
                    glyph = new Glyph(getFontProgram().getGlyphByCode(0), unicode);
                    notdefGlyphs.put(unicode, glyph);
                }
            }
            return glyph;
        }
        return null;
    }

    //TODO make subtype class member and simplify this method
    @Override
    public void flush() {
        PdfName subtype;
        String fontName;
        if (fontProgram instanceof DocFontProgram) {
            subtype = ((DocFontProgram) fontProgram).getSubtype();
            fontName = fontProgram.getFontNames().getFontName();
        } else if (fontProgram.isCff()) {
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

    protected void addRangeUni(Set<Integer> longTag) {
        if (!subset && (subsetRanges != null || getFontProgram().getDirectoryOffset() > 0)) {
            int[] rg = subsetRanges == null && getFontProgram().getDirectoryOffset() > 0
                    ? new int[]{0, 0xffff} : compactRanges(subsetRanges);
            Map<Integer, int[]> usemap = getFontProgram().getActiveCmap();
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

    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
        if (embedded) {
            PdfName fontFileName;
            PdfStream fontStream;
            if (fontProgram instanceof DocFontProgram) {
                fontFileName = ((DocFontProgram) fontProgram).getFontFileName();
                fontStream = ((DocFontProgram) fontProgram).getFontFile();
            } else if (fontProgram.isCff()) {
                fontFileName = PdfName.FontFile3;
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
                fontFileName = PdfName.FontFile2;
                Set<Integer> glyphs = new HashSet<>();
                for (int k = 0; k < shortTag.length; k++) {
                    if (shortTag[k] != 0) {
                        Integer uni = fontEncoding.getUnicode(k);
                        Glyph glyph = uni != null ? fontProgram.getGlyph(uni) : fontProgram.getGlyphByCode(k);
                        if (glyph != null) {
                            glyphs.add(glyph.getCode());
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
            if (fontStream != null) {
                fontDescriptor.put(fontFileName, fontStream);
            }
        }
    }
}
