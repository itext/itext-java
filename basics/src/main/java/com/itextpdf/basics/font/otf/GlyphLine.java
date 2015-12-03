/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.basics.font.otf;

import com.itextpdf.basics.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
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
            // TODO ligatures correspond to more than one unicode symbol
            str.append(Utilities.convertFromUtf32(glyphs.get(i).unicode));
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

}
