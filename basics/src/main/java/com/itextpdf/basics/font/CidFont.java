package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class CidFont extends FontProgram {

    private IntHashtable hMetrics;
    private IntHashtable vMetrics;
    private int pdfFontFlags;
    private Set<String> compatibleCmaps;

    CidFont(String fontName, Set<String> cmaps) {
        compatibleCmaps = cmaps;
        initializeCidFontNameAndStyle(fontName);
        avgWidth = 0;
        for(int code : hMetrics.getKeys()) {
            avgWidth += hMetrics.get(code);
        }
        avgWidth /= hMetrics.size();
        Map<String, Object> fontDesc = CidFontProperties.getAllFonts().get(fontNames.getFontName());
        if (fontDesc == null) {
            throw new PdfException("no.such.predefined.font.1").setMessageParams(fontName);
        }
        initializeCidFontProperties(fontDesc);
    }

    CidFont(String fontName, Set<String> cmaps, Map<String, Object> fontDescription) {
        initializeCidFontNameAndStyle(fontName);
        initializeCidFontProperties(fontDescription);
        compatibleCmaps = cmaps;
    }

    public boolean compatibleWith(String cmap) {
        if (cmap.equals(PdfEncodings.IDENTITY_H) || cmap.equals(PdfEncodings.IDENTITY_V)) {
            return true;
        } else {
            return compatibleCmaps != null && compatibleCmaps.contains(cmap);
        }
    }

    public IntHashtable getHMetrics() {
        return hMetrics;
    }

    public IntHashtable getVMetrics() {
        return vMetrics;
    }

    public void setHMetrics(IntHashtable hMetrics) {
        this.hMetrics = hMetrics;
    }

    public void setVMetrics(IntHashtable vMetrics) {
        this.vMetrics = vMetrics;
    }

    @Override
    public int getWidth(int ch) {
        return hMetrics.get(ch);
    }

    @Override
    public int getKerning(Glyph glyph1, Glyph glyph2) {
        return 0;
    }

    @Override
    public int getPdfFontFlags() {
        return pdfFontFlags;
    }

    @Override
    public boolean isFontSpecific() {
        return false;
    }

    //TODO remove
    public GlyphLine createGlyphLine(String content) {
        return null;
    }

    private void initializeCidFontNameAndStyle(String fontName) {
        String nameBase = getBaseName(fontName);
        if (nameBase.length() < fontName.length()) {
            fontNames.setFontName(fontName);
            fontNames.setStyle(fontName.substring(nameBase.length()));
        } else {
            fontNames.setFontName(fontName);
        }
    }

    private void initializeCidFontProperties(Map<String, Object> fontDesc) {
        setHMetrics((IntHashtable) fontDesc.get("W"));
        setVMetrics((IntHashtable) fontDesc.get("W2"));
        fontIdentification.setPanose((String) fontDesc.get("Panose"));
        fontMetrics.setItalicAngle(Integer.parseInt((String) fontDesc.get("ItalicAngle")));
        fontMetrics.setCapHeight(Integer.parseInt((String) fontDesc.get("CapHeight")));
        fontMetrics.setTypoAscender(Integer.parseInt((String) fontDesc.get("Ascent")));
        fontMetrics.setTypoDescender(Integer.parseInt((String) fontDesc.get("Descent")));
        fontMetrics.setStemV(Integer.parseInt((String) fontDesc.get("StemV")));
        pdfFontFlags = Integer.parseInt((String) fontDesc.get("Flags"));
        String fontBBox = (String) fontDesc.get("FontBBox");
        StringTokenizer tk = new StringTokenizer(fontBBox, " []\r\n\t\f");
        Integer llx = Integer.parseInt(tk.nextToken());
        Integer lly = Integer.parseInt(tk.nextToken());
        Integer urx = Integer.parseInt(tk.nextToken());
        Integer ury = Integer.parseInt(tk.nextToken());
        fontMetrics.updateBbox(llx, lly, urx, ury);
        registry = (String) fontDesc.get("Registry");
    }
}
