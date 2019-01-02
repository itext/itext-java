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
package com.itextpdf.io.font.otf.lookuptype5;

import com.itextpdf.io.font.otf.ContextualSubTable;
import com.itextpdf.io.font.otf.ContextualSubstRule;
import com.itextpdf.io.font.otf.OpenTypeFontTableReader;
import com.itextpdf.io.font.otf.OtfClass;
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Contextual Substitution Subtable: Class-based context glyph substitution
 */
public class SubTableLookup5Format2 extends ContextualSubTable {
    private static final long serialVersionUID = -2184080481143798249L;
    private Set<Integer> substCoverageGlyphIds;
    private List<List<ContextualSubstRule>> subClassSets;
    private OtfClass classDefinition;

    public SubTableLookup5Format2(OpenTypeFontTableReader openReader, int lookupFlag, Set<Integer> substCoverageGlyphIds, OtfClass classDefinition) {
        super(openReader, lookupFlag);
        this.substCoverageGlyphIds = substCoverageGlyphIds;

        this.classDefinition = classDefinition;
    }

    public void setSubClassSets(List<List<ContextualSubstRule>> subClassSets) {
        this.subClassSets = subClassSets;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId) {
        if (substCoverageGlyphIds.contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            int gClass = classDefinition.getOtfClass(startId);
            return subClassSets.get(gClass);
        }
        return Collections.<ContextualSubstRule>emptyList();
    }

    public static class SubstRuleFormat2 extends ContextualSubstRule {
        private static final long serialVersionUID = 652574134066355802L;
        // inputClassIds array omits the first class in the sequence,
        // the first class is defined by corresponding index of subClassSet array
        private int[] inputClassIds;
        private SubstLookupRecord[] substLookupRecords;
        private OtfClass classDefinition;

        public SubstRuleFormat2(SubTableLookup5Format2 subTable, int[] inputClassIds, SubstLookupRecord[] substLookupRecords) {
            this.inputClassIds = inputClassIds;
            this.substLookupRecords = substLookupRecords;
            this.classDefinition = subTable.classDefinition;
        }

        @Override
        public int getContextLength() {
            return inputClassIds.length + 1;
        }

        @Override
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return classDefinition.getOtfClass(glyphId) == inputClassIds[atIdx - 1];
        }
    }
}
