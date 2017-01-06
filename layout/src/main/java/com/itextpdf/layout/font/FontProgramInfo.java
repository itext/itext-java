package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontNamesFactory;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFont;

import java.io.IOException;
import java.util.Arrays;

/**
 * Contains all font related data to create {@link FontProgram} and {@link PdfFont}.
 * {@link FontNames} fetches with {@link FontNamesFactory}.
 */
public final class FontProgramInfo {

    private final String fontName;
    private final byte[] fontProgram;
    private final String encoding;
    private final FontNames names;
    private final int hash;

    private FontProgramInfo(String fontName, byte[] fontProgram, String encoding, FontNames names) {
        this.fontName = fontName;
        this.fontProgram = fontProgram;
        this.encoding = encoding;
        this.names = names;
        this.hash = calculateHashCode(fontName, fontProgram, encoding);
    }

    static FontProgramInfo create(FontProgram fontProgram, String encoding) {
        return new FontProgramInfo(fontProgram.getFontNames().getFontName(), null, encoding, fontProgram.getFontNames());
    }

    static FontProgramInfo create(String fontName, byte[] fontProgram, String encoding) {
        FontNames names = FontNamesFactory.fetchFontNames(fontName, fontProgram);
        if (names == null) {
            return null;
        }
        return new FontProgramInfo(fontName, fontProgram, encoding, names);
    }

    public PdfFont getPdfFont(FontProvider fontProvider) {
        try {
            return fontProvider.getPdfFont(this);
        } catch (IOException e) {
            throw new PdfException(PdfException.IoExceptionWhileCreatingFont, e);
        }
    }

    public FontNames getNames() {
        return names;
    }

    public String getFontName() {
        return fontName;
    }

    public byte[] getFontProgram() {
        return fontProgram;
    }

    public String getEncoding() {
        return encoding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FontProgramInfo)) return false;

        FontProgramInfo that = (FontProgramInfo) o;
        return (fontName != null ? fontName.equals(that.fontName) : that.fontName == null)
                && Arrays.equals(fontProgram, that.fontProgram)
                && (encoding != null ? encoding.equals(that.encoding) : that.encoding == null);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private static int calculateHashCode(String fontName, byte[] bytes, String encoding) {
        int result = fontName != null ? fontName.hashCode() : 0;
        result = 31 * result + ArrayUtil.hashCode(bytes);
        result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
        return result;
    }
}
