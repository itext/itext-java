package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontWeight;

final class FontCharacteristicsUtils {

    static FontWeight calculateFontWeight(short fw) {
        switch (fw) {
            case 100:
                return FontWeight.THIN;
            case 200:
                return FontWeight.EXTRA_LIGHT;
            case 300:
                return FontWeight.LIGHT;
            case 400:
                return FontWeight.NORMAL;
            case 500:
                return FontWeight.MEDIUM;
            case 600:
                return FontWeight.SEMI_BOLD;
            case 700:
                return FontWeight.BOLD;
            case 800:
                return FontWeight.EXTRA_BOLD;
            case 900:
                return FontWeight.BLACK;
            default:
                return FontWeight.NORMAL;
        }
    }

    static short calculateFontWeightNumber(FontWeight fw) {
        switch (fw) {
            case THIN:
                return 100;
            case EXTRA_LIGHT:
                return 200;
            case LIGHT:
                return 300;
            case NORMAL:
                return 400;
            case MEDIUM:
                return 500;
            case SEMI_BOLD:
                return 600;
            case BOLD:
                return 700;
            case EXTRA_BOLD:
                return 800;
            case BLACK:
                return 900;
            default:
                return 400;
        }
    }

    static short normalizeFontWeight(short fw) {
        fw = (short) ((fw/100)*100);
        if (fw < 100) return 100;
        if (fw > 900) return 900;
        return fw;
    }

    static short parseFontWeight(String fw) {
        if (fw == null || fw.length() == 0) {
            return -1;
        }
        fw = fw.trim().toLowerCase();
        switch (fw) {
            case "bold":
                return 700;
            case "normal":
                return 400;
            default:
                try {
                    return normalizeFontWeight((short) Integer.parseInt(fw));
                } catch (NumberFormatException ignored) {
                    return -1;
                }
        }
    }
}
