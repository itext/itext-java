/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

/**
 * A Lookup table defines the specific conditions, type, and results of
 * substitution or positioning actions that are used to implement a feature.
 *
 * <p>
 * The data describing the actions of a lookup are contained in one or more lookup subtables.
 * Different lookup types support different types of operation; for example, positioning
 * adjustment on a single glyph versus positioning adjustments on pairs of glyphs.
 *
 * <p>
 * For more information see <a href="https://learn.microsoft.com/en-us/typography/opentype/spec/chapter2#lookup-table">Lookup table</a>
 */
public abstract class OpenTableLookup {
    /** Indicates to a text-processing client certain processing options to use when substituting or positioning glyphs. */
    protected int lookupFlag;
    /** Subtables locations. */
    protected int[] subTableLocations;
    /** OpenType font table reader. */
    protected OpenTypeFontTableReader openReader;

    private int indexInLookupList;

    /**
     * Instantiates a new instance of {@link OpenTableLookup}.
     *
     * @param openReader the OpenType font table reader
     * @param lookupFlag the lookup flag
     * @param subTableLocations the subtables locations
     */
    protected OpenTableLookup(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) {
        this.lookupFlag = lookupFlag;
        this.subTableLocations = subTableLocations;
        this.openReader = openReader;
    }

    /**
     * Gets the lookup flag.
     *
     * <p>
     * The flag indicates to a text-processing client certain processing
     * options to use when substituting or positioning glyphs.
     *
     * @return the lookup flag
     */
    public int getLookupFlag() {
        return lookupFlag;
    }

    /**
     * Apply transformation to only one glyph from the glyph line.
     *
     * @param line the glyph line to transform
     *
     * @return {@code true} if transformation was applied, {@code false} otherwise
     */
    public abstract boolean transformOne(GlyphLine line);

    /**
     * Apply transformation to the glyph line.
     *
     * @param line the glyph line to transform
     *
     * @return {@code true} if transformation was applied, {@code false} otherwise
     */
    public boolean transformLine(GlyphLine line) {
        boolean changed = false;
        line.setIdx(line.getStart());
        while (line.getIdx() < line.getEnd() && line.getIdx() >= line.getStart()) {
            changed = transformOne(line) || changed;
        }
        return changed;
    }

    /**
     * Checks whether there is a substitution (replacement) for the specified index in {@code this} lookup table.
     *
     * @param index the index to check for a substitution
     *
     * @return {@code true} if there is substitution, {@code false} otherwise
     */
    public boolean hasSubstitution(int index) {
        return false;
    }

    /**
     * Reads subtables.
     *
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading subtables
     */
    protected void readSubTables() throws java.io.IOException {
        for (int subTableLocation : subTableLocations) {
            readSubTable(subTableLocation);
        }
    }

    /**
     * Reads subtable from the specified location.
     *
     * @param subTableLocation the subtable location
     *
     * @throws java.io.IOException exception is thrown in case an I/O error occurs when reading subtable
     */
    protected abstract void readSubTable(int subTableLocation) throws java.io.IOException;

    /**
     * Gets {@code this} lookup table index in the LookupList.
     *
     * @return the table index in the LookupList
     */
    public int getIndexInLookupList() {
        return indexInLookupList;
    }

    /**
     * Sets lookup table index in the LookupList.
     *
     * @param indexInLookupList the table index in the LookupList
     */
    public void setIndexInLookupList(int indexInLookupList) {
        this.indexInLookupList = indexInLookupList;
    }

    /**
     * Utility class to iterate over {@link GlyphLine}.
     */
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

        /**
         * Reads the next glyph taking into account glyph class and lookup flag.
         *
         * @param openReader the OpenType reader to check glyph class against lookup flag
         * @param lookupFlag the lookup flag
         */
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

        /**
         * Reads the previous glyph taking into account glyph class and lookup flag.
         *
         * @param openReader the OpenType reader to check glyph class against lookup flag
         * @param lookupFlag the lookup flag
         */
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
