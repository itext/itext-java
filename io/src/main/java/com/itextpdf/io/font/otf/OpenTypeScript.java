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

public class OpenTypeScript {

    public static final String DEFAULT_SCRIPT = "DFLT";
    
    private OpenTypeFontTableReader openTypeReader;
    private List<ScriptRecord> records;
    
    public OpenTypeScript(OpenTypeFontTableReader openTypeReader, int locationScriptTable) throws java.io.IOException {
        this.openTypeReader = openTypeReader;
        records = new ArrayList<>();
        openTypeReader.rf.seek(locationScriptTable);
        TagAndLocation[] tagsLocs = openTypeReader.readTagAndLocations(locationScriptTable);
        for (TagAndLocation tagLoc : tagsLocs) {
            readScriptRecord(tagLoc);
        }        
    }
    
    public List<ScriptRecord> getScriptRecords() {
        return records;
    }
    
    public LanguageRecord getLanguageRecord(String[] scripts, String language) {
        ScriptRecord scriptFound = null;
        ScriptRecord scriptDefault = null;
        for (ScriptRecord sr : records) {
            if (DEFAULT_SCRIPT.equals(sr.getTag())) {
                scriptDefault = sr;
                break;
            }
        }
        for (String script : scripts) {
            for (ScriptRecord sr : records) {
                if (sr.getTag().equals(script)) {
                    scriptFound = sr;
                    break;
                }
                if (DEFAULT_SCRIPT.equals(script)) {
                    scriptDefault = sr;
                }
            }
            if (scriptFound != null) {
                break;
            }
        }
        if (scriptFound == null) {
            scriptFound = scriptDefault;
        }
        if (scriptFound == null) {
            return null;
        }
        LanguageRecord lang = null;
        for (LanguageRecord lr : scriptFound.getLanguages()) {
            if (lr.getTag().equals(language)) {
                lang = lr;
                break;
            }
        }
        if (lang == null) {
            lang = scriptFound.getDefaultLanguage();
        }
        return lang;
    }
    
    private void readScriptRecord(TagAndLocation tagLoc) throws java.io.IOException {
        openTypeReader.rf.seek(tagLoc.getLocation());
        int locationDefaultLanguage = openTypeReader.rf.readUnsignedShort();
        if (locationDefaultLanguage > 0) {
            locationDefaultLanguage += tagLoc.getLocation();
        }
        TagAndLocation[] tagsLocs = openTypeReader.readTagAndLocations(tagLoc.getLocation());
        ScriptRecord srec = new ScriptRecord();
        srec.setTag(tagLoc.getTag());
        srec.setLanguages(new LanguageRecord[tagsLocs.length]);
        for (int k = 0; k < tagsLocs.length; ++k) {
            LanguageRecord[] languages = srec.getLanguages();
            languages[k] = readLanguageRecord(tagsLocs[k]);
            srec.setLanguages(languages);
        }
        if (locationDefaultLanguage > 0) {
            TagAndLocation t = new TagAndLocation();
            t.setTag("");
            t.setLocation(locationDefaultLanguage);
            srec.setDefaultLanguage(readLanguageRecord(t));
        }
        records.add(srec);
    }
    
    private LanguageRecord readLanguageRecord(TagAndLocation tagLoc) throws java.io.IOException {
        LanguageRecord rec = new LanguageRecord();
        //skip lookup order
        openTypeReader.rf.seek(tagLoc.getLocation() + 2L);
        rec.setFeatureRequired(openTypeReader.rf.readUnsignedShort());
        int count = openTypeReader.rf.readUnsignedShort();
        rec.setFeatures(openTypeReader.readUShortArray(count));
        rec.setTag(tagLoc.getTag());
        return rec;
    }
}
