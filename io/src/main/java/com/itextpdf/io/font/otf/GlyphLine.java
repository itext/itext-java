package com.itextpdf.io.font.otf;

import com.itextpdf.io.util.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GlyphLine implements Iterable<GlyphLine.GlyphLinePart> {
    public List<Glyph> glyphs;
    public List<ActualText> actualText;
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

    public GlyphLine(List<Glyph> glyphs, List<ActualText> actualText, int start, int end) {
        this(glyphs, start, end);
        this.actualText = actualText;
    }

    public GlyphLine(GlyphLine other) {
        this.glyphs = other.glyphs;
        this.actualText = other.actualText;
        this.start = other.start;
        this.end = other.end;
        this.idx = other.idx;
    }

    public String toUnicodeString(int start, int end) {
        Iterator<GlyphLinePart> iter = new GlyphLinePartIterator(this, start, end);
        StringBuilder str = new StringBuilder();
        while (iter.hasNext()) {
            GlyphLinePart part = iter.next();
            if (part.actualText != null) {
                str.append(part.actualText);
            } else {
                for (int i = part.start; i < part.end; i++) {
                    if (glyphs.get(i).getChars() != null) {
                        str.append(glyphs.get(i).getChars());
                    } else if (glyphs.get(i).getUnicode() != null) {
                        str.append(Utilities.convertFromUtf32(glyphs.get(i).getUnicode()));
                    }
                }
            }
        }
        return str.toString();
    }

    public GlyphLine copy(int left, int right) {
        GlyphLine glyphLine = new GlyphLine();
        glyphLine.start = 0;
        glyphLine.end = right - left;
        glyphLine.glyphs = new ArrayList<>(glyphs.subList(left, right));
        glyphLine.actualText = actualText == null ? null : new ArrayList<>(actualText.subList(left, right));
        return glyphLine;
    }

    public Glyph get(int index) {
        return glyphs.get(index);
    }

    public Glyph set(int index, Glyph glyph) {
        return glyphs.set(index, glyph);
    }

    public void add(Glyph glyph) {
        glyphs.add(glyph);
        if (actualText != null) {
            actualText.add(null);
        }
    }

    public int size() {
        return glyphs.size();
    }

    public void substituteManyToOne(OpenTypeFontTableReader tableReader, int lookupFlag, int rightPartLen, int substitutionGlyphIndex) {
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.line = this;
        gidx.idx = idx;

        StringBuilder chars = new StringBuilder();
        Glyph currentGlyph = glyphs.get(idx);
        if (currentGlyph.getChars() != null) {
            chars.append(currentGlyph.getChars());
        } else if (currentGlyph.getUnicode() != null) {
            chars.append(Utilities.convertFromUtf32(currentGlyph.getUnicode()));
        }

        for (int j = 0; j < rightPartLen; ++j) {
            gidx.nextGlyph(tableReader, lookupFlag);
            currentGlyph = glyphs.get(gidx.idx);
            if (currentGlyph.getChars() != null) {
                chars.append(currentGlyph.getChars());
            } else if (currentGlyph.getUnicode() != null) {
                chars.append(Utilities.convertFromUtf32(currentGlyph.getUnicode()));
            }
            removeGlyph(gidx.idx--);
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
        } else if (newGlyph.getUnicode() != null) {
            newGlyph.setChars(Utilities.convertFromUtf32(newGlyph.getUnicode()));
        } else if (oldGlyph.getUnicode() != null) {
            newGlyph.setChars(Utilities.convertFromUtf32(oldGlyph.getUnicode()));
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
            addAllGlyphs(idx + 1, additionalGlyphs);
            idx += substGlyphIds.length - 1;
            end += substGlyphIds.length - 1;
        }
    }

    public GlyphLine filter(GlyphLineFilter filter) {
        boolean anythingFiltered = false;
        List<Glyph> filteredGlyphs = new ArrayList<>(end - start);
        List<ActualText> filteredActualText = actualText != null ? new ArrayList<ActualText>(end - start) : null;
        for (int i = start; i < end; i++) {
            if (filter.accept(glyphs.get(i))) {
                filteredGlyphs.add(glyphs.get(i));
                if (filteredActualText != null) {
                    filteredActualText.add(actualText.get(i));
                }
            } else {
                anythingFiltered = true;
            }
        }
        if (anythingFiltered) {
            return new GlyphLine(filteredGlyphs, filteredActualText, 0, filteredGlyphs.size());
        } else {
            return this;
        }
    }

    public void setActualText(int left, int right, String text) {
        if (this.actualText == null) {
            this.actualText = new ArrayList<>(glyphs.size());
            List<ActualText> actualText = Collections.nCopies(glyphs.size(), null);
            this.actualText.addAll(actualText);
        }
        ActualText actualText = new ActualText(text);
        for (int i = left; i < right; i++) {
            this.actualText.set(i, actualText);
        }
    }

    @Override
    public Iterator<GlyphLinePart> iterator() {
        return new GlyphLinePartIterator(this);
    }

    private void removeGlyph(int index) {
        glyphs.remove(index);
        if (actualText != null) {
            actualText.remove(index);
        }
    }

    private void addAllGlyphs(int index, List<Glyph> additionalGlyphs) {
        glyphs.addAll(index, additionalGlyphs);
        if (actualText != null) {
            List<ActualText> actualTexts = Collections.nCopies(additionalGlyphs.size(), null);
            actualText.addAll(index, actualTexts);
        }
    }

    public static class GlyphLinePart {
        public int start;
        public int end;
        public String actualText;

        public GlyphLinePart(int start, int end, String actualText) {
            this.start = start;
            this.end = end;
            this.actualText = actualText;
        }
    }

    public interface GlyphLineFilter {
        boolean accept(Glyph glyph);
    }

    protected static class ActualText {
        public ActualText(String value) {
            this.value = value;
        }

        public String value;
    }

    private static class GlyphLinePartIterator implements Iterator<GlyphLinePart> {

        private GlyphLine glyphLine;

        public GlyphLinePartIterator(GlyphLine glyphLine) {
            this.glyphLine = glyphLine;
            this.pos = glyphLine.start;
        }

        public GlyphLinePartIterator(GlyphLine glyphLine, int start, int end) {
            this(new GlyphLine(glyphLine.glyphs, glyphLine.actualText, start, end));
        }

        private int pos;

        @Override
        public boolean hasNext() {
            return pos < glyphLine.end;
        }

        @Override
        public GlyphLinePart next() {
            if (glyphLine.actualText == null) {
                GlyphLinePart result = new GlyphLinePart(pos, glyphLine.end, null);
                pos = glyphLine.end;
                return result;
            } else {
                GlyphLinePart currentResult = nextGlyphLinePart(pos);
                if (currentResult == null) {
                    return null;
                }
                pos = currentResult.end;
                while (pos < glyphLine.end && !glyphLinePartNeedsActualText(currentResult)) {
                    currentResult.actualText = null;
                    GlyphLinePart nextResult = nextGlyphLinePart(pos);
                    if (nextResult != null && !glyphLinePartNeedsActualText(nextResult)) {
                        currentResult.end = nextResult.end;
                        pos = nextResult.end;
                    } else {
                        break;
                    }
                }
                return currentResult;
            }
        }

        @Override
        public void remove() {
            throw new IllegalStateException("Operation not supported");
        }

        private GlyphLinePart nextGlyphLinePart(int pos) {
            if (pos >= glyphLine.end) {
                return null;
            }
            int startPos = pos;
            ActualText startActualText = glyphLine.actualText.get(pos);
            while (pos < glyphLine.end && glyphLine.actualText.get(pos) == startActualText) {
                pos++;
            }
            return new GlyphLinePart(startPos, pos, startActualText != null ? startActualText.value : null);
        }

        private boolean glyphLinePartNeedsActualText(GlyphLinePart glyphLinePart) {
            if (glyphLinePart.actualText == null) {
                return false;
            }
            boolean needsActualText = false;
            StringBuilder toUnicodeMapResult = new StringBuilder();
            for (int i = glyphLinePart.start; i < glyphLinePart.end; i++) {
                Glyph currentGlyph = glyphLine.glyphs.get(i);
                if (currentGlyph.getUnicode() == null) {
                    needsActualText = true;
                    break;
                }

                // TODO zero glyph is a special case. Unicode might be special

                toUnicodeMapResult.append(Utilities.convertFromUtf32(currentGlyph.getUnicode()));
            }

            return needsActualText || !toUnicodeMapResult.toString().equals(glyphLinePart.actualText);
        }
    }
}
