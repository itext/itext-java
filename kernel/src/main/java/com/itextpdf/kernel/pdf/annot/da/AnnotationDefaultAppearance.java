package com.itextpdf.kernel.pdf.annot.da;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for setting
 */
public class AnnotationDefaultAppearance {

    private static final Map<StandardAnnotationFont, String> stdAnnotFontNames = new HashMap<>();
    private static final Map<ExtendedAnnotationFont, String> extAnnotFontNames = new HashMap<>();
    static {
        stdAnnotFontNames.put(StandardAnnotationFont.CourierBoldOblique, "/" + FontConstants.COURIER_BOLDOBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.CourierBold, "/" + FontConstants.COURIER_BOLD);
        stdAnnotFontNames.put(StandardAnnotationFont.CourierOblique, "/" + FontConstants.COURIER_OBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.Courier, "/" + FontConstants.COURIER);
        stdAnnotFontNames.put(StandardAnnotationFont.HelveticaBoldOblique, "/" + FontConstants.HELVETICA_BOLDOBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.HelveticaBold, "/" + FontConstants.HELVETICA_BOLD);
        stdAnnotFontNames.put(StandardAnnotationFont.HelveticaOblique, "/" + FontConstants.COURIER_OBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.Helvetica, "/" + FontConstants.HELVETICA);
        stdAnnotFontNames.put(StandardAnnotationFont.Symbol, "/" + FontConstants.SYMBOL);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesBoldItalic, "/" + FontConstants.TIMES_BOLDITALIC);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesBold, "/" + FontConstants.TIMES_BOLD);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesItalic, "/" + FontConstants.TIMES_ITALIC);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesRoman, "/" + FontConstants.TIMES_ROMAN);
        stdAnnotFontNames.put(StandardAnnotationFont.ZapfDingbats, "/" + FontConstants.ZAPFDINGBATS);

        extAnnotFontNames.put(ExtendedAnnotationFont.HYSMyeongJoMedium, "/HySm");
        extAnnotFontNames.put(ExtendedAnnotationFont.HYGoThicMedium, "/HyGo");
        extAnnotFontNames.put(ExtendedAnnotationFont.HeiseiKakuGoW5, "/KaGo");
        extAnnotFontNames.put(ExtendedAnnotationFont.HeiseiMinW3, "/KaMi");
        extAnnotFontNames.put(ExtendedAnnotationFont.MHeiMedium, "/MHei");
        extAnnotFontNames.put(ExtendedAnnotationFont.MSungLight, "/MSun");
        extAnnotFontNames.put(ExtendedAnnotationFont.STSongLight, "/STSo");
        extAnnotFontNames.put(ExtendedAnnotationFont.MSungStdLight, "/MSun");
        extAnnotFontNames.put(ExtendedAnnotationFont.STSongStdLight, "/STSo");
        extAnnotFontNames.put(ExtendedAnnotationFont.HYSMyeongJoStdMedium, "/HySm");
        extAnnotFontNames.put(ExtendedAnnotationFont.KozMinProRegular, "/KaMi");
    }

    private String colorOperand = "0 g";
    private String rawFontName = "/Helv";
    private float fontSize = 0;

    public AnnotationDefaultAppearance() {
        setFont(StandardAnnotationFont.Helvetica);
        setFontSize(12);
    }

    public AnnotationDefaultAppearance setFont(StandardAnnotationFont font) {
        setRawFontName(stdAnnotFontNames.get(font));
        return this;
    }

    public AnnotationDefaultAppearance setFont(ExtendedAnnotationFont font) {
        setRawFontName(extAnnotFontNames.get(font));
        return this;
    }

    public AnnotationDefaultAppearance setFontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public AnnotationDefaultAppearance setColor(DeviceRgb rgbColor) {
        setColorOperand(rgbColor.getColorValue(), "rg");
        return this;
    }

    public AnnotationDefaultAppearance setColor(DeviceCmyk cmykColor) {
        setColorOperand(cmykColor.getColorValue(), "k");
        return this;
    }

    public AnnotationDefaultAppearance setColor(DeviceGray grayColor) {
        setColorOperand(grayColor.getColorValue(), "g");
        return this;
    }

    public PdfString toPdfString() {
        return new PdfString(rawFontName + " " + fontSize + " Tf " + colorOperand);
    }

    private void setColorOperand(float[] colorValues, String operand) {
        StringBuilder builder = new StringBuilder();
        for (float value : colorValues) {
            builder.append(value);
            builder.append(' ');
        }
        builder.append(operand);
        this.colorOperand = builder.toString();
    }

    private void setRawFontName(String rawFontName) {
        if (rawFontName == null) {
            throw new NullPointerException();
        }
        this.rawFontName = rawFontName;
    }
}
