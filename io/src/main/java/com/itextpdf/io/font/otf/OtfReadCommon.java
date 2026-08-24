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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the OpenType font reading utility.
 */
public class OtfReadCommon {
    /**
     * Reads the ushort array from OpenType data.
     *
     * @param rf the raw source
     * @param size the size
     * @param location the location
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static int[] readUShortArray(RandomAccessFileOrArray rf, int size, int location) throws java.io.IOException {
        int[] ret = new int[size];
        for (int k = 0; k < size; ++k) {
            int offset = rf.readUnsignedShort();
            ret[k] = offset == 0 ? offset : offset + location;
        }
        return ret;
    }

    /**
     * Reads the ushort array from OpenType data
     *
     * @param rf the raw source
     * @param size the size
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static int[] readUShortArray(RandomAccessFileOrArray rf, int size) throws java.io.IOException {
        return readUShortArray(rf, size, 0);
    }

    /**
     * Reads the coverages from OpenType data.
     *
     * @param rf the raw source
     * @param locations the locations
     * @param coverage the coverage
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static void readCoverages(RandomAccessFileOrArray rf, int[] locations, List<Set<Integer>> coverage)
            throws java.io.IOException {
        for (int location : locations) {
            coverage.add(new HashSet<>(readCoverageFormat(rf, location)));
        }
    }

    /**
     * Reads the coverage format from OpenType data.
     *
     * @param rf the raw source
     * @param coverageLocation the coverage location
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static List<Integer> readCoverageFormat(RandomAccessFileOrArray rf, int coverageLocation)
            throws java.io.IOException {
        rf.seek(coverageLocation);
        int coverageFormat = rf.readShort();
        List<Integer> glyphIds;
        if (coverageFormat == 1) {
            int glyphCount = rf.readUnsignedShort();
            glyphIds = new ArrayList<>(glyphCount);
            for (int i = 0; i < glyphCount; i++) {
                int coverageGlyphId = rf.readUnsignedShort();
                glyphIds.add(coverageGlyphId);
            }
        } else if (coverageFormat == 2) {
            int rangeCount = rf.readUnsignedShort();
            glyphIds = new ArrayList<>();
            for (int i = 0; i < rangeCount; i++) {
                readRangeRecord(rf, glyphIds);
            }

        } else {
            throw new UnsupportedOperationException(
                    MessageFormatUtil.format("Invalid coverage format: {0}", coverageFormat));
        }

        return Collections.unmodifiableList(glyphIds);
    }

    private static void readRangeRecord(RandomAccessFileOrArray rf, List<Integer> glyphIds) throws java.io.IOException {
        int startGlyphId = rf.readUnsignedShort();
        int endGlyphId = rf.readUnsignedShort();
        @SuppressWarnings("unused")
        int startCoverageIndex = rf.readShort();
        for (int glyphId = startGlyphId; glyphId <= endGlyphId; glyphId++) {
            glyphIds.add(glyphId);
        }
    }

    /**
     * Reads the GPOS value record from OpenType data.
     *
     * @param tableReader the table reader
     * @param mask the mask
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static GposValueRecord readGposValueRecord(OpenTypeFontTableReader tableReader, int mask)
            throws java.io.IOException {
        GposValueRecord vr = new GposValueRecord();
        if ((mask & 0x0001) != 0) {
            vr.setXPlacement(
                    FontProgram.convertGlyphSpaceToTextSpace(tableReader.rf.readShort()) / tableReader.getUnitsPerEm());
        }
        if ((mask & 0x0002) != 0) {
            vr.setYPlacement(
                    FontProgram.convertGlyphSpaceToTextSpace(tableReader.rf.readShort()) / tableReader.getUnitsPerEm());
        }
        if ((mask & 0x0004) != 0) {
            vr.setXAdvance(
                    FontProgram.convertGlyphSpaceToTextSpace(tableReader.rf.readShort()) / tableReader.getUnitsPerEm());
        }
        if ((mask & 0x0008) != 0) {
            vr.setYAdvance(
                    FontProgram.convertGlyphSpaceToTextSpace(tableReader.rf.readShort()) / tableReader.getUnitsPerEm());
        }
        if ((mask & 0x0010) != 0) {
            tableReader.rf.skip(2);
        }
        if ((mask & 0x0020) != 0) {
            tableReader.rf.skip(2);
        }
        if ((mask & 0x0040) != 0) {
            tableReader.rf.skip(2);
        }
        if ((mask & 0x0080) != 0) {
            tableReader.rf.skip(2);
        }
        return vr;
    }

    /**
     * Reads the GPOS anchor from OpenType data.
     *
     * @param tableReader the table reader
     * @param location the location
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static GposAnchor readGposAnchor(OpenTypeFontTableReader tableReader, int location)
            throws java.io.IOException {
        if (location == 0) {
            return null;
        }
        tableReader.rf.seek(location);
        int format = tableReader.rf.readUnsignedShort();
        GposAnchor t = null;

        switch (format) {
            default:
                t = new GposAnchor();
                t.setXCoordinate(FontProgram.convertGlyphSpaceToTextSpace(tableReader.rf.readShort())
                        / tableReader.getUnitsPerEm());
                t.setYCoordinate(FontProgram.convertGlyphSpaceToTextSpace(tableReader.rf.readShort())
                        / tableReader.getUnitsPerEm());
                break;
        }

        return t;
    }

    /**
     * Reads the mark array from OpenType data.
     *
     * @param tableReader the table reader
     * @param location the location
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static List<OtfMarkRecord> readMarkArray(OpenTypeFontTableReader tableReader, int location)
            throws java.io.IOException {
        tableReader.rf.seek(location);
        int markCount = tableReader.rf.readUnsignedShort();
        int[] classes = new int[markCount];
        int[] locations = new int[markCount];
        for (int k = 0; k < markCount; ++k) {
            classes[k] = tableReader.rf.readUnsignedShort();
            int offset = tableReader.rf.readUnsignedShort();
            locations[k] = location + offset;
        }
        List<OtfMarkRecord> marks = new ArrayList<OtfMarkRecord>();
        for (int k = 0; k < markCount; ++k) {
            OtfMarkRecord rec = new OtfMarkRecord();
            rec.setMarkClass(classes[k]);
            rec.setAnchor(readGposAnchor(tableReader, locations[k]));
            marks.add(rec);
        }
        return marks;
    }

    /**
     * Reads the substitution lookup records from OpenType data.
     *
     * @param rf the raw source
     * @param substCount the subst count
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static SubstLookupRecord[] readSubstLookupRecords(RandomAccessFileOrArray rf, int substCount)
            throws java.io.IOException {
        SubstLookupRecord[] substLookUpRecords = new SubstLookupRecord[substCount];
        for (int i = 0; i < substCount; ++i) {
            SubstLookupRecord slr = new SubstLookupRecord();
            slr.sequenceIndex = rf.readUnsignedShort();
            slr.lookupListIndex = rf.readUnsignedShort();
            substLookUpRecords[i] = slr;
        }
        return substLookUpRecords;
    }

    /**
     * Reads the positioning lookup records from OpenType data.
     *
     * @param rf the raw source
     * @param recordCount the record count
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static PosLookupRecord[] readPosLookupRecords(RandomAccessFileOrArray rf, int recordCount)
            throws java.io.IOException {
        PosLookupRecord[] posLookUpRecords = new PosLookupRecord[recordCount];
        for (int i = 0; i < recordCount; ++i) {
            PosLookupRecord lookupRecord = new PosLookupRecord();
            lookupRecord.sequenceIndex = rf.readUnsignedShort();
            lookupRecord.lookupListIndex = rf.readUnsignedShort();
            posLookUpRecords[i] = lookupRecord;
        }
        return posLookUpRecords;
    }

    /**
     * Reads the anchor array from OpenType data.
     *
     * @param tableReader the table reader
     * @param locations the locations
     * @param left the left
     * @param right the right
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static GposAnchor[] readAnchorArray(OpenTypeFontTableReader tableReader, int[] locations, int left,
                                               int right) throws java.io.IOException {
        GposAnchor[] anchors = new GposAnchor[right - left];
        for (int i = left; i < right; i++) {
            anchors[i - left] = readGposAnchor(tableReader, locations[i]);
        }
        return anchors;
    }

    /**
     * Reads the base array from OpenType data.
     *
     * @param tableReader the table reader
     * @param classCount the class count
     * @param location the location
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static List<GposAnchor[]> readBaseArray(OpenTypeFontTableReader tableReader, int classCount, int location)
            throws java.io.IOException {
        List<GposAnchor[]> baseArray = new ArrayList<>();
        tableReader.rf.seek(location);
        int baseCount = tableReader.rf.readUnsignedShort();
        int[] anchorLocations = readUShortArray(tableReader.rf, baseCount * classCount, location);
        int idx = 0;
        for (int k = 0; k < baseCount; ++k) {
            baseArray.add(readAnchorArray(tableReader, anchorLocations, idx, idx + classCount));
            idx += classCount;
        }
        return baseArray;
    }

    /**
     * Reads the ligature array from OpenType data.
     *
     * @param tableReader the table reader
     * @param classCount the class count
     * @param location the location
     *
     * @return the requested result
     *
     * @throws java.io.IOException if the OpenType data cannot be read
     */
    public static List<List<GposAnchor[]>> readLigatureArray(OpenTypeFontTableReader tableReader, int classCount,
                                                             int location) throws java.io.IOException {
        List<List<GposAnchor[]>> ligatureArray = new ArrayList<>();
        tableReader.rf.seek(location);
        int ligatureCount = tableReader.rf.readUnsignedShort();
        int[] ligatureAttachLocations = readUShortArray(tableReader.rf, ligatureCount, location);
        for (int liga = 0; liga < ligatureCount; ++liga) {
            int ligatureAttachLocation = ligatureAttachLocations[liga];
            List<GposAnchor[]> ligatureAttach = new ArrayList<>();
            tableReader.rf.seek(ligatureAttachLocation);
            int componentCount = tableReader.rf.readUnsignedShort();
            final int[] componentRecordsLocation = readUShortArray(tableReader.rf, classCount * componentCount,
                    ligatureAttachLocation);
            int idx = 0;
            for (int k = 0; k < componentCount; ++k) {
                ligatureAttach.add(readAnchorArray(tableReader, componentRecordsLocation, idx, idx + classCount));
                idx += classCount;
            }
            ligatureArray.add(ligatureAttach);
        }
        return ligatureArray;
    }
}
