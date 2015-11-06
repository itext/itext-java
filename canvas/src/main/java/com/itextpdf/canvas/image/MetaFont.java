package com.itextpdf.canvas.image;

import com.itextpdf.basics.font.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MetaFont extends MetaObject {
    static final String[] fontNames = {
        "Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique",
        "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique",
        "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic",
        "Symbol", "ZapfDingbats"};

    static final int MARKER_BOLD = 1;
    static final int MARKER_ITALIC = 2;
    static final int MARKER_COURIER = 0;
    static final int MARKER_HELVETICA = 4;
    static final int MARKER_TIMES = 8;
    static final int MARKER_SYMBOL = 12;

    static final int DEFAULT_PITCH = 0;
    static final int FIXED_PITCH = 1;
    static final int VARIABLE_PITCH = 2;
    static final int FF_DONTCARE = 0;
    static final int FF_ROMAN = 1;
    static final int FF_SWISS = 2;
    static final int FF_MODERN = 3;
    static final int FF_SCRIPT = 4;
    static final int FF_DECORATIVE = 5;
    static final int BOLDTHRESHOLD = 600;    
    static final int nameSize = 32;
    static final int ETO_OPAQUE = 2;
    static final int ETO_CLIPPED = 4;

    int height;
    float angle;
    int bold;
    int italic;
    boolean underline;
    boolean strikeout;
    int charset;
    int pitchAndFamily;
    String faceName = "arial";
    FontProgram font = null;

    public MetaFont() {
        type = META_FONT;
    }

    public void init(InputMeta in) throws IOException {
        height = Math.abs(in.readShort());
        in.skip(2);
        angle = (float)(in.readShort() / 1800.0 * Math.PI);
        in.skip(2);
        bold = (in.readShort() >= BOLDTHRESHOLD ? MARKER_BOLD : 0);
        italic = (in.readByte() != 0 ? MARKER_ITALIC : 0);
        underline = (in.readByte() != 0);
        strikeout = (in.readByte() != 0);
        charset = in.readByte();
        in.skip(3);
        pitchAndFamily = in.readByte();
        byte name[] = new byte[nameSize];
        int k;
        for (k = 0; k < nameSize; ++k) {
            int c = in.readByte();
            if (c == 0) {
                break;
            }
            name[k] = (byte)c;
        }
        try {
            faceName = new String(name, 0, k, "Cp1252");
        } catch (UnsupportedEncodingException e) {
            faceName = new String(name, 0, k);
        }
        faceName = faceName.toLowerCase();
    }

    public FontProgram getFont() throws IOException {
        if (font != null)
            return font;
        //TODO ff2 = FontFactory.getFont(faceName, BaseFont.CP1252, true, 10, ((italic != 0) ? Font.ITALIC : 0) | ((bold != 0) ? Font.BOLD : 0));
        FontProgram ff2 = FontFactory.createRegisteredFont(faceName, PdfEncodings.CP1252, ((italic != 0) ? FontConstants.ITALIC : 0) | ((bold != 0) ? FontConstants.BOLD : 0));
        font = ff2;
        if (font != null)
            return font;
        String fontName;
        if (faceName.contains("courier") || faceName.contains("terminal")
            || faceName.contains("fixedsys")) {
            fontName = fontNames[MARKER_COURIER + italic + bold];
        }
        else if (faceName.contains("ms sans serif") || faceName.contains("arial")
            || faceName.contains("system")) {
            fontName = fontNames[MARKER_HELVETICA + italic + bold];
        }
        else if (faceName.contains("arial black")) {
            fontName = fontNames[MARKER_HELVETICA + italic + MARKER_BOLD];
        }
        else if (faceName.contains("times") || faceName.contains("ms serif")
            || faceName.contains("roman")) {
            fontName = fontNames[MARKER_TIMES + italic + bold];
        }
        else if (faceName.contains("symbol")) {
            fontName = fontNames[MARKER_SYMBOL];
        }
        else {
            int pitch = pitchAndFamily & 3;
            int family = (pitchAndFamily >> 4) & 7;
            switch (family) {
                case FF_MODERN:
                    fontName = fontNames[MARKER_COURIER + italic + bold];
                    break;
                case FF_ROMAN:
                    fontName = fontNames[MARKER_TIMES + italic + bold];
                    break;
                case FF_SWISS:
                case FF_SCRIPT:
                case FF_DECORATIVE:
                    fontName = fontNames[MARKER_HELVETICA + italic + bold];
                    break;
                default:
                {
                    switch (pitch) {
                        case FIXED_PITCH:
                            fontName = fontNames[MARKER_COURIER + italic + bold];
                            break;
                        default:
                            fontName = fontNames[MARKER_HELVETICA + italic + bold];
                            break;
                    }
                }
            }
        }
        try {
            font = Type1Font.createStandardFont(fontName, PdfEncodings.WINANSI);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return font;
    }

    public float getAngle() {
        return angle;
    }
    
    public boolean isUnderline() {
        return underline;
    }
    
    public boolean isStrikeout() {
        return strikeout;
    }
    
    public float getFontSize(MetaState state) {
        return Math.abs(state.transformY(height) - state.transformY(0)) * WmfImageHelper.wmfFontCorrection;
    }
}
