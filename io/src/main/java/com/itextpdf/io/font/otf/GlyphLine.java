/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.font.otf;

import com.itextpdf.io.util.TextUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GlyphLine {
    private int start;
    private int end;
    private int idx;
    protected List<Glyph> glyphs;
    protected List<ActualText> actualText;

    public GlyphLine() {
        this.glyphs = new ArrayList<>();
    }

    /**
     * Create a new line of Glyphs.
     *
     * @param glyphs list containing the glyphs
     */
    public GlyphLine(List<Glyph> glyphs) {
        this.glyphs = glyphs;
        this.start = 0;
        this.end = glyphs.size();
    }

    /**
     * Create a new line of Glyphs from a slice of a List of Glyphs.
     *
     * @param glyphs list of Glyphs to slice
     * @param start  starting index of the slice
     * @param end    terminating index of the slice
     */
    public GlyphLine(List<Glyph> glyphs, int start, int end) {
        this.glyphs = glyphs;
        this.start = start;
        this.end = end;
    }

    /**
     * Create a new line of Glyphs from a slice of a List of Glyphs, and add the actual text.
     *
     * @param glyphs     list of Glyphs to slice
     * @param actualText corresponding list containing the actual text the glyphs represent
     * @param start      starting index of the slice
     * @param end        terminating index of the slice
     */
    protected GlyphLine(List<Glyph> glyphs, List<ActualText> actualText, int start, int end) {
        this(glyphs, start, end);
        this.actualText = actualText;
    }

    /**
     * Copy a line of Glyphs.
     *
     * @param other line of Glyphs to copy
     */
    public GlyphLine(GlyphLine other) {
        this.glyphs = other.glyphs;
        this.actualText = other.actualText;
        this.start = other.start;
        this.end = other.end;
        this.idx = other.idx;
    }

    /**
     * Copy a slice of a line of Glyphs
     *
     * @param other line of Glyphs to copy
     * @param start starting index of the slice
     * @param end   terminating index of the slice
     */
    public GlyphLine(GlyphLine other, int start, int end) {
        this.glyphs = other.glyphs.subList(start, end);
        if (other.actualText != null) {
            this.actualText = other.actualText.subList(start, end);
        }
        this.start = 0;
        this.end = end - start;
        this.idx = other.idx - start;
    }

    /**
     * Retrieves the start of the glyph line.
     *
     * @return start of glyph line
     */
    public int getStart() {
        return start;
    }

    /**
     * Sets the start of the glyph line.
     *
     * @param start start of glyph line
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Retrieves the end of the glyph line.
     *
     * @return end of glyph line
     */
    public int getEnd() {
        return end;
    }

    /**
     * Sets the end of the glyph line.
     *
     * @param end end of glyph line
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * Retrieves the idx of the glyph line.
     *
     * @return idx of glyph line
     */
    public int getIdx() {
        return idx;
    }

    /**
     * Sets the idx of the glyph line.
     *
     * @param idx idx of glyph line
     */
    public void setIdx(int idx) {
        this.idx = idx;
    }

    /**
     * Get the unicode string representation of the GlyphLine slice.
     *
     * @param start starting index of the slice
     * @param end   terminating index of the slice
     * @return String containing the unicode representation of the slice.
     */
    public String toUnicodeString(int start, int end) {
        ActualTextIterator iter = new ActualTextIterator(this, start, end);
        StringBuilder str = new StringBuilder();
        while (iter.hasNext()) {
            GlyphLinePart part = iter.next();
            if (part.getActualText() != null) {
                str.append(part.getActualText());
            } else {
                for (int i = part.getStart(); i < part.getEnd(); i++) {
                    str.append(glyphs.get(i).getUnicodeChars());
                }
            }
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return toUnicodeString(start, end);
    }

    /**
     * Copy a slice of this Glyphline.
     *
     * @param left  leftmost index of the slice
     * @param right rightmost index of the slice
     * @return new GlyphLine containing the copied slice
     */
    public GlyphLine copy(int left, int right) {
        GlyphLine glyphLine = new GlyphLine();
        glyphLine.setStart(0);
        glyphLine.setEnd(right - left);
        glyphLine.setGlyphs(new ArrayList<>(glyphs.subList(left, right)));
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

    public void add(int index, Glyph glyph) {
        glyphs.add(index, glyph);
        if (actualText != null) {
            actualText.add(index, null);
        }
    }

    public void setGlyphs(List<Glyph> replacementGlyphs) {
        glyphs = new ArrayList<>(replacementGlyphs);
        start = 0;
        end = replacementGlyphs.size();
        actualText = null;
    }

    /**
     * Add a line to the current one.
     * The glyphs from the start till the end points will be copied.
     * The same is true for the actual text.
     *
     * @param other the line that should be added to the current one
     */
    public void add(GlyphLine other) {
        if (other.actualText != null) {
            if (actualText == null) {
                actualText = new ArrayList<ActualText>(glyphs.size());
                for (int i = 0; i < glyphs.size(); i++) {
                    actualText.add(null);
                }
            }
            actualText.addAll(other.actualText.subList(other.getStart(), other.getEnd()));
        }
        glyphs.addAll(other.glyphs.subList(other.getStart(), other.getEnd()));
        if (null != actualText) {
            while (actualText.size() < glyphs.size()) {
                actualText.add(null);
            }
        }
    }

    /**
     * Replaces the current content with the other line's content.
     *
     * @param other the line with the content to be set to the current one
     */
    public void replaceContent(GlyphLine other) {
        glyphs.clear();
        glyphs.addAll(other.glyphs);
        if (other.actualText != null) {
            if (actualText == null) {
                actualText = new ArrayList<>();
            } else {
                actualText.clear();
            }
            actualText.addAll(other.actualText);
        } else {
            actualText = null;
        }
        start = other.getStart();
        end = other.getEnd();
    }

    public int size() {
        return glyphs.size();
    }

    public void substituteManyToOne(OpenTypeFontTableReader tableReader, int lookupFlag, int rightPartLen, int substitutionGlyphIndex) {
        OpenTableLookup.GlyphIndexer gidx = new OpenTableLookup.GlyphIndexer();
        gidx.setLine(this);
        gidx.setIdx(idx);

        StringBuilder chars = new StringBuilder();
        Glyph currentGlyph = glyphs.get(idx);
        if (currentGlyph.getChars() != null) {
            chars.append(currentGlyph.getChars());
        } else if (currentGlyph.hasValidUnicode()) {
            chars.append(TextUtil.convertFromUtf32(currentGlyph.getUnicode()));
        }

        for (int j = 0; j < rightPartLen; ++j) {
            gidx.nextGlyph(tableReader, lookupFlag);
            currentGlyph = glyphs.get(gidx.getIdx());
            if (currentGlyph.getChars() != null) {
                chars.append(currentGlyph.getChars());
            } else if (currentGlyph.hasValidUnicode()) {
                chars.append(TextUtil.convertFromUtf32(currentGlyph.getUnicode()));
            }
            removeGlyph(gidx.getIdx());
            gidx.setIdx(gidx.getIdx() - 1);
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
        } else if (newGlyph.hasValidUnicode()) {
            newGlyph.setChars(TextUtil.convertFromUtf32(newGlyph.getUnicode()));
        } else if (oldGlyph.hasValidUnicode()) {
            newGlyph.setChars(TextUtil.convertFromUtf32(oldGlyph.getUnicode()));
        }
        glyphs.set(idx, newGlyph);
    }

    public void substituteOneToMany(OpenTypeFontTableReader tableReader, int[] substGlyphIds) {
        //sequence length shall be at least 1
        int substCode = substGlyphIds[0];
        Glyph oldGlyph = glyphs.get(idx);
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
            if (null != actualText) {
                if (null == actualText.get(idx)) {
                    actualText.set(idx, new ActualText(oldGlyph.getUnicodeString()));
                }
                for (int i = 0; i < additionalGlyphs.size(); i++) {
                    this.actualText.set(idx + 1 + i, actualText.get(idx));
                }
            }
            idx += substGlyphIds.length - 1;
            end += substGlyphIds.length - 1;
        }
    }

    public GlyphLine filter(IGlyphLineFilter filter) {
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
            for (int i = 0; i < glyphs.size(); i++)
                this.actualText.add(null);
        }
        ActualText actualText = new ActualText(text);
        for (int i = left; i < right; i++) {
            this.actualText.set(i, actualText);
        }
    }

    public Iterator<GlyphLinePart> iterator() {
        return new ActualTextIterator(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GlyphLine other = (GlyphLine) obj;
        if (end - start != other.end - other.start) {
            return false;
        }
        if (actualText == null && other.actualText != null || actualText != null && other.actualText == null) {
            return false;
        }
        for (int i = start; i < end; i++) {
            int otherPos = other.start + i - start;
            Glyph myGlyph = get(i);
            Glyph otherGlyph = other.get(otherPos);
            if (myGlyph == null && otherGlyph != null || myGlyph != null && !myGlyph.equals(otherGlyph)) {
                return false;
            }
            ActualText myAT = actualText == null ? null : actualText.get(i);
            ActualText otherAT = other.actualText == null ? null : other.actualText.get(otherPos);
            if (myAT == null && otherAT != null || myAT != null && !myAT.equals(otherAT)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31*result + start;
        result = 31*result + end;
        for (int i = start; i < end; i++) {
            result = 31*result + glyphs.get(i).hashCode();
        }
        if (null != actualText) {
            for (int i = start; i < end; i++) {
                result = 31*result;
                if (null != actualText.get(i)) {
                    result += actualText.get(i).hashCode();
                }
            }
        }
        return result;
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
            for (int i = 0; i < additionalGlyphs.size(); i++) {
                this.actualText.add(index, null);
            }
        }
    }

    public interface IGlyphLineFilter {
        boolean accept(Glyph glyph);
    }

    public static class GlyphLinePart {
        private int start;
        private int end;
        // Might be null if it's not necessary
        private String actualText;
        private boolean reversed;

        /**
         * Creates a glyph line part object with given start and end values.
         * Actual text is set to null.
         *
         * @param start start value of the glyph line part
         * @param end end value of the glyph line part
         */
        public GlyphLinePart(int start, int end) {
            this(start, end, null);
        }

        /**
         * Creates a glyph line part object with given start, end and actual text values.
         *
         * @param start start value of the glyph line part
         * @param end end value of the glyph line part
         * @param actualText actual text
         */
        public GlyphLinePart(int start, int end, String actualText) {
            this.start = start;
            this.end = end;
            this.actualText = actualText;
        }

        /**
         * Retrieves the start of the glyph line part.
         *
         * @return start value of glyph line part
         */
        public int getStart() {
            return start;
        }

        /**
         * Sets the start of the glyph line part.
         *
         * @param start start of the glyph line part
         *
         * @return Altered glyph line part object
         */
        public GlyphLinePart setStart(int start) {
            this.start = start;
            return this;
        }

        /**
         * Retrieves the end of the glyph line part.
         *
         * @return end value of glyph line part
         */
        public int getEnd() {
            return end;
        }

        /**
         * Sets the end of the glyph line part.
         *
         * @param end end value of glyph line part
         *
         * @return Altered glyph line part object
         */
        public GlyphLinePart setEnd(int end) {
            this.end = end;
            return this;
        }

        /**
         * Retrieves the actual text of the glyph line part.
         *
         * @return Actual text of glyph line part
         */
        public String getActualText() {
            return actualText;
        }

        /**
         * Sets the actual text of the glyph line part.
         *
         * @param actualText Actual text of glyph line part
         *
         * @return Altered Glyph line part object
         */
        public GlyphLinePart setActualText(String actualText) {
            this.actualText = actualText;
            return this;
        }

        /**
         * Retrieves whether the glyph line part is reversed.
         *
         * @return True if it is reversed, false otherwise.
         */
        public boolean isReversed() {
            return reversed;
        }

        /**
         * Sets whether the glyph line part is reversed.
         *
         * @param reversed true if it should be reversed, false otherwise
         *
         * @return Altered glyph line part object
         */
        public GlyphLinePart setReversed(boolean reversed) {
            this.reversed = reversed;
            return this;
        }
    }

    protected static class ActualText {
        private final String value;

        public ActualText(String value) {
            this.value = value;
        }

        /**
         * Retrieves the value of the actual text.
         *
         * @return actual text value
         */
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ActualText other = (ActualText) obj;
            return value == null && other.getValue() == null || value.equals(other.getValue());
        }

        @Override
        public int hashCode() {
            return 31*value.hashCode();
        }
    }
}
