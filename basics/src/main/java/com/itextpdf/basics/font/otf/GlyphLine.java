package com.itextpdf.basics.font.otf;

import com.itextpdf.basics.Utilities;

import java.util.ArrayList;
import java.util.List;

public class GlyphLine {
    public List<Glyph> glyphs;
    public int start;
    public int end;
    public int idx;

    public GlyphLine() {
        this.glyphs = new ArrayList<>();
    }

    public GlyphLine(List<Glyph> glyphs) {
        this.glyphs = glyphs;
        this.start = 0;
        this.end = glyphs.size();
    }

    public GlyphLine(List<Glyph> glyphs, int start, int end) {
        this.glyphs = glyphs;
        this.start = start;
        this.end = end;
    }

    public GlyphLine(GlyphLine other) {
        this.glyphs = other.glyphs;
        this.start = other.start;
        this.end = other.end;
        this.idx = other.idx;
    }

    public String toUnicodeString(int left, int right) {
        StringBuilder str = new StringBuilder();
        for (int i = left; i < right; i++) {
            if (glyphs.get(i).getChars() != null) {
                str.append(glyphs.get(i).getChars());
            } else if (glyphs.get(i).unicode != null) {
                str.append(Utilities.convertFromUtf32(glyphs.get(i).unicode));
            }
        }
        return str.toString();
    }

    public GlyphLine copy(int left, int right) {
        GlyphLine glyphLine = new GlyphLine();
        glyphLine.start = 0;
        glyphLine.end = right - left;
        glyphLine.glyphs = new ArrayList<>(glyphs.subList(left, right));
        return glyphLine;
    }

    public int length() {
        return end - start;
    }

    public void substituteManyToOne(OpenTypeFontTableReader tableReader, int lookupFlag, int rightPartLen, int substitutionGlyphIndex) {
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.line = this;
        gidx.idx = idx;

        StringBuilder chars = new StringBuilder();
        Glyph currentGlyph = glyphs.get(idx);
        if (currentGlyph.getChars() != null) {
            chars.append(currentGlyph.getChars());
        } else if (currentGlyph.unicode != null) {
            chars.append(Utilities.convertFromUtf32(currentGlyph.unicode));
        }

        for (int j = 0; j < rightPartLen; ++j) {
            gidx.nextGlyph(tableReader, lookupFlag);
            currentGlyph = glyphs.get(gidx.idx);
            if (currentGlyph.getChars() != null) {
                chars.append(currentGlyph.getChars());
            } else if (currentGlyph.unicode != null) {
                chars.append(Utilities.convertFromUtf32(currentGlyph.unicode));
            }
            glyphs.remove(gidx.idx--);
        }
        char[] newChars = new char[chars.length()];
        chars.getChars(0, chars.length(), newChars, 0);
        Glyph newGlyph = tableReader.getGlyph(substitutionGlyphIndex);
        newGlyph.setChars(newChars);
        glyphs.set(idx, newGlyph);
        end -= rightPartLen;
    }

    public void substituteOneToOne(OpenTypeFontTableReader tableReader, int substitutionGlyphIndex) {
        Glyph oldGlyph = glyphs.get(idx);
        Glyph newGlyph = tableReader.getGlyph(substitutionGlyphIndex);
        if (oldGlyph.getChars() != null) {
            newGlyph.setChars(oldGlyph.getChars());
        } else if (newGlyph.unicode != null) {
            newGlyph.setChars(Utilities.convertFromUtf32(newGlyph.unicode));
        } else if (oldGlyph.unicode != null) {
            newGlyph.setChars(Utilities.convertFromUtf32(oldGlyph.unicode));
        }
        glyphs.set(idx, newGlyph);
    }

    public void substituteOneToMany(OpenTypeFontTableReader tableReader, int[] substGlyphIds) {
        int substCode = substGlyphIds[0]; //sequence length shall be at least 1
        Glyph glyph = tableReader.getGlyph(substCode);
        glyphs.set(idx, glyph);

        if (substGlyphIds.length > 1) {
            List<Glyph> additionalGlyphs = new ArrayList<>(substGlyphIds.length - 1);
            for (int i = 1; i < substGlyphIds.length; ++i) {
                substCode = substGlyphIds[i];
                glyph = tableReader.getGlyph(substCode);
                additionalGlyphs.add(glyph);
            }
            glyphs.addAll(idx + 1, additionalGlyphs);
            idx += substGlyphIds.length - 1;
            end += substGlyphIds.length - 1;
        }
    }

}
