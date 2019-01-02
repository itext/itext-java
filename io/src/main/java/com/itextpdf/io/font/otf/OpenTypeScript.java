/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font.otf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OpenTypeScript implements Serializable {

    private static final long serialVersionUID = 381398146861429491L;
    public final String DEFAULT_SCRIPT = "DFLT";
    
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
            if (DEFAULT_SCRIPT.equals(sr.tag)) {
                scriptDefault = sr;
                break;
            }
        }
        for (String script : scripts) {
            for (ScriptRecord sr : records) {
                if (sr.tag.equals(script)) {
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
        for (LanguageRecord lr : scriptFound.languages) {
            if (lr.tag.equals(language)) {
                lang = lr;
                break;
            }
        }
        if (lang == null) {
            lang = scriptFound.defaultLanguage;
        }
        return lang;
    }
    
    private void readScriptRecord(TagAndLocation tagLoc) throws java.io.IOException {
        openTypeReader.rf.seek(tagLoc.location);
        int locationDefaultLanguage = openTypeReader.rf.readUnsignedShort();
        if (locationDefaultLanguage > 0) {
            locationDefaultLanguage += tagLoc.location;
        }
        TagAndLocation[] tagsLocs = openTypeReader.readTagAndLocations(tagLoc.location);
        ScriptRecord srec = new ScriptRecord();
        srec.tag = tagLoc.tag;
        srec.languages = new LanguageRecord[tagsLocs.length];
        for (int k = 0; k < tagsLocs.length; ++k) {
            srec.languages[k] = readLanguageRecord(tagsLocs[k]);
        }
        if (locationDefaultLanguage > 0) {
            TagAndLocation t = new TagAndLocation();
            t.tag = "";
            t.location = locationDefaultLanguage;
            srec.defaultLanguage = readLanguageRecord(t);
        }
        records.add(srec);
    }
    
    private LanguageRecord readLanguageRecord(TagAndLocation tagLoc) throws java.io.IOException {
        LanguageRecord rec = new LanguageRecord();
        //skip lookup order
        openTypeReader.rf.seek(tagLoc.location + 2);
        rec.featureRequired = openTypeReader.rf.readUnsignedShort();
        int count = openTypeReader.rf.readUnsignedShort();
        rec.features = openTypeReader.readUShortArray(count);
        rec.tag = tagLoc.tag;
        return rec;
    }
}
