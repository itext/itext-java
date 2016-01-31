package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

class DocType1Font extends Type1Font implements DocFontProgram {

    private PdfStream fontFile;
    private PdfName fontFileName;
    private PdfName subtype;

    private DocType1Font(String fontName) {
        super(fontName);
    }

    static Type1Font createFontProgram(PdfDictionary fontDictionary, FontEncoding fontEncoding) {
        PdfName baseFontName = fontDictionary.getAsName(PdfName.BaseFont);
        String baseFont;
        if (baseFontName != null) {
            baseFont = baseFontName.getValue();
        } else {
            baseFont = FontUtils.createRandomFontName();
        }
        if (!fontDictionary.containsKey(PdfName.FontDescriptor)) {
            Type1Font type1StdFont;
            try {
                //if there are no font modifiers, cached font could be used,
                //otherwise a new instance should be created.
                type1StdFont = Type1Font.createStandardFont(baseFont);
            } catch (Exception e) {
                type1StdFont = null;
            }
            if (type1StdFont != null) {
                return type1StdFont;
            }
        }
        DocType1Font fontProgram = new DocType1Font(baseFont);
        PdfDictionary fontDesc = fontDictionary.getAsDictionary(PdfName.FontDescriptor);
        fontProgram.subtype = fontDesc.getAsName(PdfName.Subtype);
        fillFontDescriptor(fontProgram, fontDesc);

        PdfNumber firstCharNumber = fontDictionary.getAsNumber(PdfName.FirstChar);
        int firstChar = firstCharNumber != null ? Math.max(firstCharNumber.getIntValue(), 0) : 0;
        int[] widths = FontUtils.convertSimpleWidthsArray(fontDictionary.getAsArray(PdfName.Widths), firstChar);
        fontProgram.avgWidth = 0;
        int glyphsWithWidths = 0;
        for (int i = 0; i < 256; i++) {
            Glyph glyph = new Glyph(i, widths[i], fontEncoding.getUnicode(i));
            fontProgram.codeToGlyph.put(i, glyph);
            if (glyph.getUnicode() != null) {
                fontProgram.unicodeToGlyph.put(glyph.getUnicode(), glyph);
            }
            if (widths[i] > 0) {
                glyphsWithWidths++;
                fontProgram.avgWidth += widths[i];
            }
        }
        if (glyphsWithWidths != 0) {
            fontProgram.avgWidth /= glyphsWithWidths;
        }
        return fontProgram;
    }

    public PdfStream getFontFile() {
        return fontFile;
    }

    public PdfName getFontFileName() {
        return fontFileName;
    }

    public PdfName getSubtype() {
        return subtype;
    }

    static void fillFontDescriptor(DocType1Font font, PdfDictionary fontDesc) {
        if (fontDesc == null) {
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.Ascent);
        if (v != null) {
            font.setTypoAscender(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.Descent);
        if (v != null) {
            font.setTypoDescender(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.CapHeight);
        if (v != null) {
            font.setCapHeight(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.XHeight);
        if (v != null) {
            font.setXHeight(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.ItalicAngle);
        if (v != null) {
            font.setItalicAngle(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.StemV);
        if (v != null) {
            font.setStemV(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.StemH);
        if (v != null) {
            font.setStemH(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.FontWeight);
        if (v != null) {
            font.setFontWeight(v.getIntValue());
        }

        PdfName fontStretch = fontDesc.getAsName(PdfName.FontStretch);
        if (fontStretch != null) {
            font.setFontWidth(fontStretch.getValue());
        }


        PdfArray bboxValue = fontDesc.getAsArray(PdfName.FontBBox);

        if (bboxValue != null) {
            int[] bbox = new int[4];
            //llx
            bbox[0] = bboxValue.getAsNumber(0).getIntValue();
            //lly
            bbox[1] = bboxValue.getAsNumber(1).getIntValue();
            //urx
            bbox[2] = bboxValue.getAsNumber(2).getIntValue();
            //ury
            bbox[3] = bboxValue.getAsNumber(3).getIntValue();

            if (bbox[0] > bbox[2]) {
                int t = bbox[0];
                bbox[0] = bbox[2];
                bbox[2] = t;
            }
            if (bbox[1] > bbox[3]) {
                int t = bbox[1];
                bbox[1] = bbox[3];
                bbox[3] = t;
            }
            font.setBbox(bbox);
        }

        PdfString fontFamily = fontDesc.getAsString(PdfName.FontFamily);
        if (fontFamily != null) {
            font.setFontFamily(fontFamily.getValue());
        }

        PdfNumber flagsValue = fontDesc.getAsNumber(PdfName.Flags);
        if (flagsValue != null) {
            int flags = flagsValue.getIntValue();
            if ((flags & 1) != 0) {
                font.setFixedPitch(true);
            }
            if ((flags & 262144) != 0) {
                font.setBold(true);
            }
        }

        PdfName[] fontFileNames = new PdfName[] {PdfName.FontFile, PdfName.FontFile2, PdfName.FontFile3};
        for (PdfName fontFile: fontFileNames) {
            if (fontDesc.containsKey(fontFile)) {
                font.fontFileName = fontFile;
                font.fontFile = fontDesc.getAsStream(fontFile);
                break;
            }
        }
    }
}
