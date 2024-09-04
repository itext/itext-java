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

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class OpenTypeFontTableReader {

    protected final RandomAccessFileOrArray rf;
	protected final int tableLocation;

    protected List<OpenTableLookup> lookupList;
    protected OpenTypeScript scriptsType;
    protected OpenTypeFeature featuresType;
    private final Map<Integer, Glyph> indexGlyphMap;
    private final OpenTypeGdefTableReader gdef;

    private final int unitsPerEm;

	protected OpenTypeFontTableReader(RandomAccessFileOrArray rf, int tableLocation, OpenTypeGdefTableReader gdef,
                                   Map<Integer, Glyph> indexGlyphMap, int unitsPerEm) {
		this.rf = rf;
		this.tableLocation = tableLocation;
        this.indexGlyphMap = indexGlyphMap;
        this.gdef = gdef;
        this.unitsPerEm = unitsPerEm;
	}

    public Glyph getGlyph(int index) {
        return indexGlyphMap.get(index);
    }

    public OpenTableLookup getLookupTable(int idx) {
        if (idx < 0 || idx >= lookupList.size()) {
            return null;
        }
        return lookupList.get(idx);
    }

    public List<ScriptRecord> getScriptRecords() {
        return scriptsType.getScriptRecords();
    }

    public List<FeatureRecord> getFeatureRecords() {
        return featuresType.getRecords();
    }

    public List<FeatureRecord> getFeatures(String[] scripts, String language) {
        LanguageRecord rec = scriptsType.getLanguageRecord(scripts, language);
        if (rec == null) {
            return null;
        }
        List<FeatureRecord> ret = new ArrayList<FeatureRecord>();
        for (int f : rec.getFeatures()) {
            ret.add(featuresType.getRecord(f));
        }
        return ret;
    }

    public List<FeatureRecord> getSpecificFeatures(List<FeatureRecord> features, String[] specific) {
        if (specific == null) {
            return features;
        }
        Set<String> hs = new HashSet<String>();
        //noinspection ManualArrayToCollectionCopy
        for (String s : specific) {
            hs.add(s);
        }
        List<FeatureRecord> recs = new ArrayList<FeatureRecord>();
        for (FeatureRecord rec : features) {
            if (hs.contains(rec.getTag())) {
                recs.add(rec);
            }
        }
        return recs;
    }

    public FeatureRecord getRequiredFeature(String[] scripts, String language) {
        LanguageRecord rec = scriptsType.getLanguageRecord(scripts, language);
        if (rec == null)
            return null;
        return featuresType.getRecord(rec.getFeatureRequired());
    }

    public List<OpenTableLookup> getLookups(FeatureRecord[] features) {
        IntHashtable hash = new IntHashtable();
        for (FeatureRecord rec : features) {
            for (int idx : rec.getLookups()) {
                hash.put(idx, 1);
            }
        }
        List<OpenTableLookup> ret = new ArrayList<>();
        for (int idx : hash.toOrderedKeys()) {
            ret.add(lookupList.get(idx));
        }
        return ret;
    }

    public List<OpenTableLookup> getLookups(FeatureRecord feature) {
        List<OpenTableLookup> ret = new ArrayList<>(feature.getLookups().length);
        for (int idx : feature.getLookups()) {
            ret.add(lookupList.get(idx));
        }
        return ret;
    }

    public boolean isSkip(int glyph, int flag) {
        return gdef.isSkip(glyph, flag);
    }

    public int getGlyphClass(int glyphCode) {
        return gdef.getGlyphClassTable().getOtfClass(glyphCode);
    }

    public int getUnitsPerEm() {
        return unitsPerEm;
    }

    public LanguageRecord getLanguageRecord(String otfScriptTag) {
        return getLanguageRecord(otfScriptTag, null);
    }

    public LanguageRecord getLanguageRecord(String otfScriptTag, String langTag) {
        if (otfScriptTag == null) {
            return null;
        }
        for (final ScriptRecord record : getScriptRecords()) {
            if (!otfScriptTag.equals(record.getTag())) {
                continue;
            }
            if (langTag == null) {
                return record.getDefaultLanguage();
            }
            for (final LanguageRecord lang : record.getLanguages()) {
                if (langTag.equals(lang.getTag())) {
                    return lang;
                }
            }
        }
        return null;
    }

	protected abstract OpenTableLookup readLookupTable(int lookupType, int lookupFlag, int[] subTableLocations)
			throws java.io.IOException;

    protected final OtfClass readClassDefinition(int classLocation) {
        return OtfClass.create(rf, classLocation);
    }

    protected final int[] readUShortArray(int size, int location) throws java.io.IOException {
        return OtfReadCommon.readUShortArray(rf, size, location);
    }

    protected final int[] readUShortArray(int size) throws java.io.IOException {
        return OtfReadCommon.readUShortArray(rf, size);
    }

    protected void readCoverages(int[] locations, List<Set<Integer>> coverage) throws java.io.IOException {
        OtfReadCommon.readCoverages(rf, locations, coverage);
    }

	protected final List<Integer> readCoverageFormat(int coverageLocation)
			throws java.io.IOException {
        return OtfReadCommon.readCoverageFormat(rf, coverageLocation);
	}

    protected SubstLookupRecord[] readSubstLookupRecords(int substCount) throws java.io.IOException {
        return OtfReadCommon.readSubstLookupRecords(rf, substCount);
    }

    protected PosLookupRecord[] readPosLookupRecords(int substCount) throws java.io.IOException {
        return OtfReadCommon.readPosLookupRecords(rf, substCount);
    }

    protected TagAndLocation[] readTagAndLocations(int baseLocation) throws java.io.IOException {
        int count = rf.readUnsignedShort();
        TagAndLocation[] tagslLocs = new TagAndLocation[count];
        for (int k = 0; k < count; ++k) {
            TagAndLocation tl = new TagAndLocation();
            tl.setTag(rf.readString(4, "utf-8"));
            tl.setLocation(rf.readUnsignedShort() + baseLocation);
            tagslLocs[k] = tl;
        }
        return tagslLocs;
    }

    /**
     * This is the starting point of the class. A sub-class must call this
     * method to start getting call backs to the {@link #readLookupTable(int, int, int[])}
     * method.
     * @throws FontReadingException
     */
    final void startReadingTable() throws FontReadingException {
        try {
            rf.seek(tableLocation);
            /*int version =*/
            // version not used
            rf.readInt();
            int scriptListOffset = rf.readUnsignedShort();
            int featureListOffset = rf.readUnsignedShort();
            int lookupListOffset = rf.readUnsignedShort();
            // read the Script tables
            scriptsType = new OpenTypeScript(this, tableLocation + scriptListOffset);
            // read Feature table
            featuresType = new OpenTypeFeature(this, tableLocation + featureListOffset);
            // read LookUpList table
            readLookupListTable(tableLocation + lookupListOffset);
        } catch (java.io.IOException e) {
            throw new FontReadingException("Error reading font file", e);
        }
    }

    private void readLookupListTable(int lookupListTableLocation) throws java.io.IOException {
        lookupList = new ArrayList<>();
        rf.seek(lookupListTableLocation);
        int lookupCount = rf.readUnsignedShort();
        int[] lookupTableLocations = readUShortArray(lookupCount, lookupListTableLocation);
        // read LookUp tables
        for (int lookupLocation : lookupTableLocations) {

            // be tolerant to NULL offset in LookupList table
            if (lookupLocation == 0) {
                continue;
            }
            readLookupTable(lookupLocation);
        }
    }

    private void readLookupTable(int lookupTableLocation) throws java.io.IOException {
        rf.seek(lookupTableLocation);
        int lookupType = rf.readUnsignedShort();
        int lookupFlag = rf.readUnsignedShort();
        int subTableCount = rf.readUnsignedShort();
        int[] subTableLocations = readUShortArray(subTableCount, lookupTableLocation);
        lookupList.add(readLookupTable(lookupType, lookupFlag, subTableLocations));
    }
}
