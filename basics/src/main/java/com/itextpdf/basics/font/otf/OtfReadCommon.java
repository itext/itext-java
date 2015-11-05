/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.basics.font.otf;

import com.itextpdf.basics.io.RandomAccessFileOrArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author admin
 */
public class OtfReadCommon {
    public static int[] readUShortArray(RandomAccessFileOrArray rf, int size, int location) throws IOException {
        int[] ret = new int[size];
        for (int k = 0; k < size; ++k) {
            int offset = rf.readUnsignedShort();
            ret[k] = offset == 0 ? offset : offset + location;
        }
        return ret;
    }
    
    public static int[] readUShortArray(RandomAccessFileOrArray rf, int size) throws IOException {
        return readUShortArray(rf, size, 0);
    }

    public static void readCoverages(RandomAccessFileOrArray rf, int[] locations, List<HashSet<Integer>> coverage) throws IOException {
        for (int location : locations) {
            coverage.add(new HashSet<>(readCoverageFormat(rf, location)));
        }
    }

	public static List<Integer> readCoverageFormat(RandomAccessFileOrArray rf, int coverageLocation)
			throws IOException {
		rf.seek(coverageLocation);
		int coverageFormat = rf.readShort();
		List<Integer> glyphIds;
		if (coverageFormat == 1) {
			int glyphCount = rf.readShort();
			glyphIds = new ArrayList<>(glyphCount);
			for (int i = 0; i < glyphCount; i++) {
				int coverageGlyphId = rf.readShort();
				glyphIds.add(coverageGlyphId);
			}
		} else if (coverageFormat == 2) {
			int rangeCount = rf.readShort();
			glyphIds = new ArrayList<>();
			for (int i = 0; i < rangeCount; i++) {
				readRangeRecord(rf, glyphIds);
			}

		} else {
			throw new UnsupportedOperationException(String.format("Invalid coverage format: %d", coverageFormat));
		}

		return Collections.unmodifiableList(glyphIds);
	}

	private static void readRangeRecord(RandomAccessFileOrArray rf, List<Integer> glyphIds) throws IOException {
		int startGlyphId = rf.readShort();
		int endGlyphId = rf.readShort();
		@SuppressWarnings("unused")
        int startCoverageIndex = rf.readShort();
		for (int glyphId = startGlyphId; glyphId <= endGlyphId; glyphId++) {
			glyphIds.add(glyphId);
		}
	}
    
    public static GposValueRecord ReadGposValueRecord(RandomAccessFileOrArray rf, int mask) throws IOException {
        GposValueRecord vr = new GposValueRecord();
        if ((mask & 0x0001) != 0) {
            vr.XPlacement = rf.readShort();
        }
        if ((mask & 0x0002) != 0) {
            vr.YPlacement = rf.readShort();
        }
        if ((mask & 0x0004) != 0) {
            vr.XAdvance = rf.readShort();
        }
        if ((mask & 0x0008) != 0) {
            vr.YAdvance = rf.readShort();
        }
        if ((mask & 0x0010) != 0) {
            rf.skip(2);
        }
        if ((mask & 0x0020) != 0) {
            rf.skip(2);
        }
        if ((mask & 0x0040) != 0) {
            rf.skip(2);
        }
        if ((mask & 0x0080) != 0) {
            rf.skip(2);
        }
        return vr;
    }
    
    public static GposAnchor ReadGposAnchor(RandomAccessFileOrArray rf, int location) throws IOException {
        if (location == 0) {
            return null;
        }
        rf.seek(location);
        rf.readUnsignedShort(); //skip format
        GposAnchor t = new GposAnchor();
        t.XCoordinate = rf.readShort();
        t.YCoordinate = rf.readShort();
        return t;
    }
    
    public static List<OtfMarkRecord> ReadMarkArray(RandomAccessFileOrArray rf, int location) throws IOException {
        rf.seek(location);
        int markCount = rf.readUnsignedShort();
        int[] classes = new int[markCount];
        int[] locations = new int[markCount];
        for (int k = 0; k < markCount; ++k) {
            classes[k] = rf.readUnsignedShort();
            locations[k] = rf.readUnsignedShort();
        }
        List<OtfMarkRecord> marks = new ArrayList<OtfMarkRecord>();
        for (int k = 0; k < markCount; ++k) {
            OtfMarkRecord rec = new OtfMarkRecord();
            rec.MarkClass = classes[k];
            rec.Anchor = ReadGposAnchor(rf, locations[k]);
            marks.add(rec);
        }
        return marks;
    }
    
    public static SubsPosLookupRecord[] readSubsPosLookupRecord(OpenTypeFontTableReader openReader, int substCount) throws IOException {
        SubsPosLookupRecord[] substPosLookUpRecords = new SubsPosLookupRecord[substCount];
        for (int i = 0; i < substCount; ++i) {
            SubsPosLookupRecord slr = new SubsPosLookupRecord();
            slr.sequenceIndex = openReader.rf.readUnsignedShort();
            slr.lookupListIndex = openReader.rf.readUnsignedShort();
            substPosLookUpRecords[i] = slr;
        }
        return substPosLookUpRecords;
    }
}
