package com.itextpdf.core.font;

import com.itextpdf.basics.font.FontEncoding;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;

class DocType1Font extends Type1Font {

    private DocType1Font(String fontName) {
        super(fontName);
    }

    public static Type1Font createSimpleFontProgram(PdfDictionary fontDictionary, FontEncoding fontEncoding) {
        PdfName baseFontName = fontDictionary.getAsName(PdfName.BaseFont);
        String baseFont;
        if (baseFontName != null) {
            baseFont = baseFontName.getValue();
        } else {
            baseFont = DocFontUtils.createRandomFontName();
        }
        if (!fontDictionary.containsKey(PdfName.FontDescriptor)) {
            Type1Font type1StdFont;
            try {
                //if there are no font modifiers, cached font could be used,
                //otherwise a new instance should be created.
                type1StdFont = createStandardFont(baseFont);
            } catch (Exception e) {
                type1StdFont = null;
            }
            if (type1StdFont != null) {
                return type1StdFont;
            }
        }
        DocType1Font fontProgram = new DocType1Font(baseFont);
        PdfNumber firstCharNumber = fontDictionary.getAsNumber(PdfName.FirstChar);
        int firstChar = firstCharNumber != null ? Math.min(firstCharNumber.getIntValue(), 0) : 0;
        int[] widths = DocFontUtils.convertSimpleWidthsArray(fontDictionary.getAsArray(PdfName.Widths), firstChar);

        for (int i = 0; i < 256; i++) {
            int width = i - firstChar < widths.length ? widths[i - firstChar] : 0;
            Glyph glyph = new Glyph(i, width, fontEncoding.getUnicode(i));
            fontProgram.codeToGlyph.put(i, glyph);
            if (glyph.getUnicode() != null) {
                fontProgram.unicodeToGlyph.put(glyph.getUnicode(), glyph);
            }
        }
        fillFontDescriptor(fontProgram, fontDictionary.getAsDictionary(PdfName.FontDescriptor));

        return fontProgram;
    }

    static void fillFontDescriptor(DocType1Font font, PdfDictionary fontDesc) {
        if (fontDesc == null) {
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.Ascent);
        if (v != null) {
            font.setAscender(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.Descent);
        if (v != null) {
            font.setDescender(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.CapHeight);
        if (v != null) {
            font.setCapHeight(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.ItalicAngle);
        if (v != null) {
            font.setItalicAngle(v.getIntValue());
        }
        v = fontDesc.getAsNumber(PdfName.StemV);
        if (v != null) {
            font.setStemV(v.getIntValue());
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
        float maxAscent = Math.max(font.fontMetrics.getBbox().getTop(), font.fontMetrics.getAscender());
        float minDescent = Math.min(font.fontMetrics.getBbox().getBottom(), font.fontMetrics.getDescender());
        //This magic comes from iText5
        font.setAscender((int) (maxAscent * 1000 / (maxAscent - minDescent)));
        font.setDescender((int) (minDescent * 1000 / (maxAscent - minDescent)));

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
    }
}
