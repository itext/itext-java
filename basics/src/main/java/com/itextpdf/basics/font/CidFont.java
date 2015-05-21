package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;

import java.util.HashMap;
import java.util.StringTokenizer;

public class CidFont extends FontProgram {

    private String name;
    private String style;
    private String registry;
    private String panose;
    private int llx;
    private int lly;
    private int urx;
    private int ury;
    private int capHeight;
    private int ascent;
    private int descent;
    private int italicAngle;
    private int stemV;
    private int flags;

    private IntHashtable hMetrics;
    private IntHashtable vMetrics;

    public CidFont(String fontName) throws PdfException {
        initializeCidFontNameAndStyle(fontName);
        HashMap<String, Object> fontDesc = CidFontProperties.getAllFonts().get(name);
        if (fontDesc == null) {
            throw new PdfException("no.such.predefined.font.1").setMessageParams(fontName);
        }
        initializeCidFontProperties(fontDesc);
    }

    //TODO describe supported font properties, so that user could create his own custom cid font.
    public CidFont(String fontName, HashMap<String, Object> fontDesc) {
        initializeCidFontNameAndStyle(fontName);
        initializeCidFontProperties(fontDesc);
    }

    @Override
    public String getFontName() {
        return name;
    }

    @Override
    public String getStyle() {
        return style;
    }

    public String getRegistry() {
        return registry;
    }

    @Override
    public String getPanose() {
        return panose;
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public int getLlx() {
        return llx;
    }

    @Override
    public int getLly() {
        return lly;
    }

    @Override
    public int getUrx() {
        return urx;
    }

    @Override
    public int getUry() {
        return ury;
    }

    @Override
    public int getCapHeight() {
        return capHeight;
    }

    @Override
    public int getAscent() {
        return ascent;
    }

    @Override
    public int getDescent() {
        return descent;
    }

    @Override
    public float getItalicAngle() {
        return italicAngle;
    }

    @Override
    public int getStemV() {
        return stemV;
    }

    public IntHashtable getHMetrics() {
        return hMetrics;
    }

    public IntHashtable getVMetrics() {
        return vMetrics;
    }

    private void initializeCidFontNameAndStyle(String fontName) {
        String nameBase = getBaseName(fontName);
        if (nameBase.length() < fontName.length()) {
            name = nameBase;
            style = fontName.substring(nameBase.length());
        } else {
            name = fontName;
        }
    }

    private void initializeCidFontProperties(HashMap<String, Object> fontDesc) {
        registry = (String)fontDesc.get("Registry");
        hMetrics = (IntHashtable)fontDesc.get("W");
        vMetrics = (IntHashtable)fontDesc.get("W2");
        panose = (String)fontDesc.get("Panose");
        italicAngle = Integer.parseInt((String)fontDesc.get("ItalicAngle"));
        capHeight = Integer.parseInt((String)fontDesc.get("CapHeight"));
        ascent = Integer.parseInt((String)fontDesc.get("Ascent"));
        descent = Integer.parseInt((String)fontDesc.get("Descent"));
        stemV = Integer.parseInt((String) fontDesc.get("StemV"));
        flags = Integer.parseInt((String)fontDesc.get("Flags"));
        String fontBBox = (String)fontDesc.get("FontBBox");
        StringTokenizer tk = new StringTokenizer(fontBBox, " []\r\n\t\f");
        tk.nextToken();
        llx = Integer.parseInt(tk.nextToken());
        lly = Integer.parseInt(tk.nextToken());
        urx = Integer.parseInt(tk.nextToken());
        ury = Integer.parseInt(tk.nextToken());
    }

}
