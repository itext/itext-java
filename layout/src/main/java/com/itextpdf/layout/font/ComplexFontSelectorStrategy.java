package com.itextpdf.layout.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.font.PdfFont;

import java.util.ArrayList;
import java.util.List;

public class ComplexFontSelectorStrategy extends FontSelectorStrategy {
    private PdfFont font;
    private FontSelector selector;

    public ComplexFontSelectorStrategy(String text, FontSelector selector, FontProvider provider) {
        super(text, provider);
        this.font = null;
        this.selector = selector;
    }

    @Override
    public PdfFont getCurrentFont() {
        return font;
    }

    @Override
    public List<Glyph> nextGlyphs() {
        int nextUnignorable = nextSignificantIndex();
        for (FontProgramInfo f : selector.getFonts()) {
            font = f.getPdfFont(provider);
            if (font.containsGlyph(text, nextUnignorable)) {
                break;
            } else {
                font = null;
            }
        }
        List<Glyph> glyphs = new ArrayList<>();
        if (font != null) {
            Character.UnicodeScript unicodeScript = nextSignificantUnicodeScript(nextUnignorable);
            int to = nextUnignorable;
            for (int i = nextUnignorable; i < text.length(); i++) {
                int codePoint = text.codePointAt(i);
                Character.UnicodeScript currScript = Character.UnicodeScript.of(codePoint);
                if (isSignificantUnicodeScript(currScript) && currScript != unicodeScript) {
                    break;
                }
                if (isSurrogatePair(codePoint)) i++;
                to = i;
            }

            index += font.appendGlyphs(text, index, to, glyphs);
        } else {
            font = selector.bestMatch().getPdfFont(provider);
            if (index != nextUnignorable) {
                index += font.appendGlyphs(text, index, nextUnignorable - 1, glyphs);
            }
            index += font.appendAnyGlyph(text, nextUnignorable, glyphs);
        }
        return glyphs;
    }

    private int nextSignificantIndex() {
        int nextValidChar = index;
        for (; nextValidChar < text.length(); nextValidChar++) {
            if (!Character.isIdentifierIgnorable(text.charAt(nextValidChar))) {
                break;
            }
        }
        return nextValidChar;
    }

    private Character.UnicodeScript nextSignificantUnicodeScript(int from) {
        for (int i = from; i < text.length(); i++) {
            int codePoint = text.codePointAt(i);
            Character.UnicodeScript unicodeScript = Character.UnicodeScript.of(codePoint);
            if (isSignificantUnicodeScript(unicodeScript)) {
                return unicodeScript;
            }

            if (isSurrogatePair(codePoint)) i++;
        }
        return Character.UnicodeScript.COMMON;
    }

    private static boolean isSignificantUnicodeScript(Character.UnicodeScript unicodeScript) {
        // Character.UnicodeScript.UNKNOWN will be handled as significant unicode script
        return unicodeScript != Character.UnicodeScript.COMMON && unicodeScript != Character.UnicodeScript.INHERITED;
    }

    private static boolean isSurrogatePair(int codePoint) {
        //lazy surrogate pair check
        return codePoint > 0xFFFF;
    }
}
