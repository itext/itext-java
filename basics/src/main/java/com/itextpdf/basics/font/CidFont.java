package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;

import java.util.HashMap;
import java.util.StringTokenizer;

public class CidFont extends FontProgram {

    private IntHashtable hMetrics;
    private IntHashtable vMetrics;

    public CidFont(String fontName) {
        initializeCidFontNameAndStyle(fontName);
        HashMap<String, Object> fontDesc = CidFontProperties.getAllFonts().get(getFontName());
        if (fontDesc == null) {
            throw new PdfException("no.such.predefined.font.1").setMessageParams(fontName);
        }
        initializeCidFontProperties(fontDesc);
    }

    //TODO describe supported font properties, so that user could create his own custom cid font.
    public CidFont(String fontName, HashMap<String, Object> fontDescription) {
        initializeCidFontNameAndStyle(fontName);
        initializeCidFontProperties(fontDescription);
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
    protected int getRawWidth(int c, String name) {
        throw new IllegalStateException();
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        throw new IllegalStateException();
    }

    @Override
    public int getWidth(int ch) {
        throw new IllegalStateException();
    }

    @Override
    public int getWidth(String text) {
        throw new IllegalStateException();
    }

    private void initializeCidFontNameAndStyle(String fontName) {
        String nameBase = getBaseName(fontName);
        if (nameBase.length() < fontName.length()) {
            this.fontName = fontName;
            this.style = fontName.substring(nameBase.length());
        } else {
            this.fontName = fontName;
            this.style = "";
        }
    }

    private void initializeCidFontProperties(HashMap<String, Object> fontDesc) {
        setRegistry((String) fontDesc.get("Registry"));
        setHMetrics((IntHashtable) fontDesc.get("W"));
        setVMetrics((IntHashtable) fontDesc.get("W2"));
        setPanose((String) fontDesc.get("Panose"));
        setItalicAngle(Integer.parseInt((String) fontDesc.get("ItalicAngle")));
        setCapHeight(Integer.parseInt((String) fontDesc.get("CapHeight")));
        setAscender(Integer.parseInt((String) fontDesc.get("Ascent")));
        setDescender(Integer.parseInt((String) fontDesc.get("Descent")));
        setStemV(Integer.parseInt((String) fontDesc.get("StemV")));
        setFlags(Integer.parseInt((String) fontDesc.get("Flags")));
        String fontBBox = (String) fontDesc.get("FontBBox");
        StringTokenizer tk = new StringTokenizer(fontBBox, " []\r\n\t\f");
        setLlx(Integer.parseInt(tk.nextToken()));
        setLly(Integer.parseInt(tk.nextToken()));
        setUrx(Integer.parseInt(tk.nextToken()));
        setUry(Integer.parseInt(tk.nextToken()));
    }

}
