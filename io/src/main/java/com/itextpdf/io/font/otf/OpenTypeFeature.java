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
            // +2 don't use FeatureParams
            openTypeReader.rf.seek(tagLoc.getLocation() + 2L);
            int lookupCount = openTypeReader.rf.readUnsignedShort();
            FeatureRecord rec = new FeatureRecord();
            rec.setTag(tagLoc.getTag());
            rec.setLookups(openTypeReader.readUShortArray(lookupCount));
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
