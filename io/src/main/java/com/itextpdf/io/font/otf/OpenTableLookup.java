/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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


public abstract class OpenTableLookup {

    protected int lookupFlag;
    protected int[] subTableLocations;
    protected OpenTypeFontTableReader openReader;
    
    protected OpenTableLookup(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) {
        this.lookupFlag = lookupFlag;
        this.subTableLocations = subTableLocations;
        this.openReader = openReader;
    }
    
    public int getLookupFlag() {
        return lookupFlag;
    }

    public abstract boolean transformOne(GlyphLine line);
    
    public boolean transformLine(GlyphLine line) {
        boolean changed = false;
        line.setIdx(line.getStart());
        while (line.getIdx() < line.getEnd() && line.getIdx() >= line.getStart()) {
            changed = transformOne(line) || changed;
        }
        return changed;
    }

    public boolean hasSubstitution(int index) {
        return false;
    }

    protected void readSubTables() throws java.io.IOException {
        for (int subTableLocation : subTableLocations) {
            readSubTable(subTableLocation);
        }
    }

    protected abstract void readSubTable(int subTableLocation) throws java.io.IOException;

    public static class GlyphIndexer {
        private GlyphLine line;
        private Glyph glyph;
        private int idx;

        /**
         * Retrieves the glyph line of the object.
         *
         * @return glyph line
         */
        public GlyphLine getLine() {
            return line;
        }

        /**
         * Sets the glyph line of the object.
         *
         * @param line glyph line
         */
        public void setLine(GlyphLine line) {
            this.line = line;
        }

        /**
         * Retrieves the glyph of the object.
         *
         * @return glyph
         */
        public Glyph getGlyph() {
            return glyph;
        }

        /**
         * Sets the glyph of the object.
         *
         * @param glyph glyph
         */
        public void setGlyph(Glyph glyph) {
            this.glyph = glyph;
        }

        /**
         * Retrieves the idx of the glyph indexer.
         *
         * @return idx
         */
        public int getIdx() {
            return idx;
        }

        /**
         * Sets the idx of the glyph indexer.
         *
         * @param idx idx
         */
        public void setIdx(int idx) {
            this.idx = idx;
        }

        public void nextGlyph(OpenTypeFontTableReader openReader, int lookupFlag) {
            glyph = null;
            while (++idx < line.getEnd()) {
                Glyph g = line.get(idx);
                if (!openReader.isSkip(g.getCode(), lookupFlag)) {
                    glyph = g;
                    break;
                }
            }
        }

        public void previousGlyph(OpenTypeFontTableReader openReader, int lookupFlag) {
            glyph = null;
            while (--idx >= line.getStart()) {
                Glyph g = line.get(idx);
                if (!openReader.isSkip(g.getCode(), lookupFlag)) {
                    glyph = g;
                    break;
                }
            }
        }
    }

}
