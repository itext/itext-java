package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.*;
import com.itextpdf.core.pdf.*;

import java.io.IOException;


public abstract class PdfSimpleFont<T extends FontProgram> extends PdfFont {

    T fontProgram;

    public PdfSimpleFont(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    protected PdfSimpleFont(PdfDocument pdfDocument) throws PdfException {
        super(pdfDocument);
    }

    /**
     * Creates a unique subset prefix to be added to the font name when the font is embedded and subset.
     *
     * @return the subset prefix
     */

    public T getFontProgram(){
        return fontProgram;
    }

    protected abstract T initializeTypeFontForCopy(String encodingName) throws PdfException, IOException;

    protected abstract T initializeTypeFont(String fontName, String encodingName) throws IOException, PdfException;




    protected void init() throws PdfException, IOException {

        PdfName baseFont = fontDictionary.getAsName(PdfName.BaseFont);
        getPdfObject().put(PdfName.Subtype, fontDictionary.getAsName(PdfName.Subtype));
        getPdfObject().put(PdfName.BaseFont, baseFont);
        PdfObject encodingObj = fontDictionary.get(PdfName.Encoding);
        initFontProgram(encodingObj);

        fontProgram.setFontName(baseFont.getValue());

        if (encodingObj == null) {

            if (Type1Parser.BuiltinFonts14.contains(baseFont.getValue())) {
                fillEncoding(baseFont);
            } else {
                fillEncoding(null);
            }
            //todo: add cmap
        } else if (encodingObj.isDictionary()) {

            PdfDictionary encDic = (PdfDictionary) encodingObj;
            PdfName baseEncoding = encDic.getAsName(PdfName.BaseEncoding);
            PdfDictionary enc = new PdfDictionary();
            enc.put(PdfName.Type, PdfName.Encoding);
            PdfArray diff = encDic.getAsArray(PdfName.Differences);

            if (diff != null) {
                enc.put(PdfName.Differences, diff);
            }

            if (baseEncoding == null) {
                fillEncoding(null);

            } else {
                fillEncoding(baseEncoding);
                enc.put(PdfName.BaseEncoding, baseEncoding);

            }

            getPdfObject().put(PdfName.Encoding, enc);
            fillDifference(diff);
        } else if (encodingObj.isName()) {
            getPdfObject().put(PdfName.Encoding, encodingObj);
            fillEncoding((PdfName) encodingObj);
        }

        PdfNumber firstChar = fontDictionary.getAsNumber(PdfName.FirstChar);
        PdfNumber lastChar = fontDictionary.getAsNumber(PdfName.LastChar);

        if (lastChar != null && firstChar != null) {
            getPdfObject().put(PdfName.FirstChar, firstChar);
            getPdfObject().put(PdfName.LastChar, lastChar);
        }

        PdfArray widths = fontDictionary.getAsArray(PdfName.Widths);
        if (widths != null) {
            getPdfObject().put(PdfName.Widths, widths);
            fontProgram.setWidths(getFillWidths(widths, firstChar, lastChar));
        }

        if (Type1Parser.BuiltinFonts14.contains(fontProgram.getFontName())) {
            fontProgram = initializeTypeFont(fontProgram.getFontName(), fontProgram.getEncoding().getBaseEncoding());
        }


        PdfDictionary fromDescriptorDictionary = fontDictionary.getAsDictionary(PdfName.FontDescriptor);
        if (fromDescriptorDictionary != null) {
            PdfDictionary toDescriptorDictionary = new PdfDictionary();
            toDescriptorDictionary.makeIndirect(getDocument());
            toDescriptorDictionary.put(PdfName.Type, PdfName.FontDescriptor);
            toDescriptorDictionary.put(PdfName.FontName, fromDescriptorDictionary.getAsName(PdfName.FontName));

            PdfName subtype = fromDescriptorDictionary.getAsName(PdfName.Subtype);
            if (subtype != null) {
                toDescriptorDictionary.put(PdfName.Subtype, subtype);
            }

            PdfNumber ascent = fromDescriptorDictionary.getAsNumber(PdfName.Ascent);
            if (ascent != null) {
                toDescriptorDictionary.put(PdfName.Ascent, ascent);
                fontProgram.setAscender(ascent.getIntValue());
            }

            PdfNumber descent = fromDescriptorDictionary.getAsNumber(PdfName.Descent);
            if (descent != null) {
                toDescriptorDictionary.put(PdfName.Descent, ascent);
                fontProgram.setDescender(descent.getIntValue());
            }

            PdfNumber capHeight = fromDescriptorDictionary.getAsNumber(PdfName.CapHeight);
            if (capHeight != null) {
                toDescriptorDictionary.put(PdfName.CapHeight, capHeight);
                fontProgram.setCapHeight(capHeight.getIntValue());
            }

            PdfNumber italicAngle = fromDescriptorDictionary.getAsNumber(PdfName.ItalicAngle);
            if (italicAngle != null) {
                toDescriptorDictionary.put(PdfName.ItalicAngle, italicAngle);
                fontProgram.setItalicAngle(italicAngle.getIntValue());
            }

            PdfNumber stemV = fromDescriptorDictionary.getAsNumber(PdfName.StemV);
            if (stemV != null) {
                toDescriptorDictionary.put(PdfName.StemV, stemV);
                fontProgram.setStemV(stemV.getIntValue());
            }

            PdfNumber fontWeight = fromDescriptorDictionary.getAsNumber(PdfName.FontWeight);
            if (fontWeight != null) {
                toDescriptorDictionary.put(PdfName.FontWeight, fontWeight);
            }


            PdfNumber flags = fromDescriptorDictionary.getAsNumber(PdfName.Flags);
            if (flags != null) {
                toDescriptorDictionary.put(PdfName.Flags, flags);
            }

            PdfStream fileStream = fromDescriptorDictionary.getAsStream(PdfName.FontFile);
            if (fileStream != null) {
                PdfStream newFileStream = copyFontFileStream(fileStream);
                toDescriptorDictionary.put(PdfName.FontFile, newFileStream);
                newFileStream.flush();
            }

            PdfStream fileStream2 = fromDescriptorDictionary.getAsStream(PdfName.FontFile2);
            if (fileStream2 != null) {
                PdfStream newFileStream = copyFontFileStream(fileStream2);
                toDescriptorDictionary.put(PdfName.FontFile2, newFileStream);
                newFileStream.flush();
            }

            PdfStream fileStream3 = fromDescriptorDictionary.getAsStream(PdfName.FontFile3);
            if (fileStream3 != null) {
                PdfStream newFileStream = copyFontFileStream(fileStream3);
                toDescriptorDictionary.put(PdfName.FontFile3, newFileStream);
                newFileStream.flush();
            }

            PdfArray bbox = fromDescriptorDictionary.getAsArray(PdfName.FontBBox);
            toDescriptorDictionary.put(PdfName.FontBBox, bbox);

            if (bbox != null) {
                int llx = bbox.getAsNumber(0).getIntValue();
                int lly = bbox.getAsNumber(1).getIntValue();
                int urx = bbox.getAsNumber(2).getIntValue();
                int ury = bbox.getAsNumber(3).getIntValue();
                if (llx > urx) {
                    int t = llx;
                    llx = urx;
                    urx = t;
                }
                if (lly > ury) {
                    int t = lly;
                    lly = ury;
                    ury = t;
                }

                fontProgram.setLlx(llx);
                fontProgram.setLly(lly);
                fontProgram.setUrx(urx);
                fontProgram.setUry(ury);


            }

            getPdfObject().put(PdfName.FontDescriptor, toDescriptorDictionary);
            toDescriptorDictionary.flush();
        }

    }

    private String getEncodingName(PdfName encoding) {
        String encodingName = PdfEncodings.WINANSI;
        if (PdfName.MacRomanEncoding.equals(encoding)) {
            encodingName = PdfEncodings.MACROMAN;
        } else if (FontConstants.SYMBOL.equals(encoding.getValue())) {
            encodingName = FontConstants.SYMBOL;
        } else if (FontConstants.ZAPFDINGBATS.equals(encoding.getValue())) {
            encodingName = FontConstants.ZAPFDINGBATS;
        }
        return encodingName;
    }

    private void initFontProgram(PdfObject encoding) throws PdfException, IOException {
        if (encoding == null) {
            fontProgram = initializeTypeFontForCopy(PdfEncodings.EmptyString);
        } else if (encoding.isName()) {
            PdfName encodingPdfName = (PdfName) encoding;
            fontProgram = initializeTypeFontForCopy(getEncodingName(encodingPdfName));
        } else if (encoding.isDictionary()) {
            PdfDictionary encDic = (PdfDictionary) encoding;
            PdfName baseEncodingName = encDic.getAsName(PdfName.BaseEncoding);
            if (baseEncodingName == null) {
                fontProgram = initializeTypeFontForCopy(PdfEncodings.EmptyString);
            } else {
                fontProgram = initializeTypeFontForCopy(getEncodingName(baseEncodingName));
            }
        }
    }

    private void fillEncoding(PdfName encoding) throws PdfException {
        if (encoding == null && isSymbolic()) {
            for (int k = 0; k < 256; ++k) {
                fontProgram.getEncoding().getSpecialMap().put(k, k);
                fontProgram.getEncoding().setUnicodeDifferences(k, (char) k);

            }
        } else if (PdfName.MacRomanEncoding.equals(encoding) || PdfName.WinAnsiEncoding.equals(encoding)
                || FontConstants.SYMBOL.equals(encoding.getValue()) || FontConstants.ZAPFDINGBATS.equals(encoding.getValue())) {

            byte b[] = new byte[256];
            for (int k = 0; k < 256; ++k) {
                b[k] = (byte) k;
            }

            String cv = PdfEncodings.convertToString(b, fontProgram.getEncoding().getBaseEncoding());
            char arr[] = cv.toCharArray();
            for (int k = 0; k < 256; ++k) {
                fontProgram.getEncoding().getSpecialMap().put(arr[k], k);
                fontProgram.getEncoding().setUnicodeDifferences(k, arr[k]);
            }
        } else {
            for (int k = 0; k < 256; ++k) {
                fontProgram.getEncoding().getSpecialMap().put(PdfEncodings.standardEncoding[k], k);
                fontProgram.getEncoding().setUnicodeDifferences(k, (char) PdfEncodings.standardEncoding[k]);
            }
        }
    }

    private void fillDifference(PdfArray diffs) throws PdfException {
        if (diffs != null) {
            int currentNumber = 0;
            for (int k = 0; k < diffs.size(); ++k) {
                PdfObject obj = diffs.get(k);
                if (obj.isNumber())
                    currentNumber = ((PdfNumber) obj).getIntValue();
                else {
                    int c[] = AdobeGlyphList.nameToUnicode(((PdfName) obj).getValue());
                    if (c != null && c.length > 0) {
                        fontProgram.getEncoding().getSpecialMap().put(c[0], currentNumber);
                        fontProgram.getEncoding().setDifferences(currentNumber, ((PdfName) obj).getValue());
                        fontProgram.getEncoding().setUnicodeDifferences(currentNumber, (char) c[0]);
                    }
                    //todo: add cmap to unicode
                    ++currentNumber;
                }
            }
        }
    }


}
