package com.itextpdf.basics.font.otf;

import java.util.ArrayList;
import java.util.List;

public class OpenTypeFeature {
    private OpenTypeFontTableReader openTypeReader;
    private List<FeatureRecord> records;
    
    public OpenTypeFeature(OpenTypeFontTableReader openTypeReader, int locationFeatureTable) throws java.io.IOException {
        this.openTypeReader = openTypeReader;
        records = new ArrayList<>();
        openTypeReader.rf.seek(locationFeatureTable);
        TagAndLocation[] tagsLocs = openTypeReader.readTagAndLocations(locationFeatureTable);
        for (TagAndLocation tagLoc : tagsLocs) {
            openTypeReader.rf.seek(tagLoc.location + 2); //+2 don't use FeatureParams
            int lookupCount = openTypeReader.rf.readUnsignedShort();
            FeatureRecord rec = new FeatureRecord();
            rec.tag = tagLoc.tag;
            rec.lookups = openTypeReader.readUShortArray(lookupCount);
            records.add(rec);
        }
    }
    
    public List<FeatureRecord> getRecords() {
        return records;
    }
    
    public FeatureRecord getRecord(int idx) {
        if (idx < 0 || idx >= records.size())
            return null;
        return records.get(idx);
    }
}
