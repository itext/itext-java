/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.io.font.otf.SubstLookupRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Contextual Substitution Subtable: Coverage-based context glyph substitution
 */
public class SubTableLookup5Format3 extends ContextualSubTable {
    private static final long serialVersionUID = -9142690964201749548L;
    ContextualSubstRule substitutionRule;

    public SubTableLookup5Format3(OpenTypeFontTableReader openReader, int lookupFlag, SubstRuleFormat3 rule) {
        super(openReader, lookupFlag);
        this.substitutionRule = rule;
    }

    @Override
    protected List<ContextualSubstRule> getSetOfRulesForStartGlyph(int startId) {
        SubstRuleFormat3 ruleFormat3 = (SubstRuleFormat3) this.substitutionRule;
        if (ruleFormat3.coverages.get(0).contains(startId) && !openReader.isSkip(startId, lookupFlag)) {
            return Collections.singletonList(this.substitutionRule);
        }
        return Collections.<ContextualSubstRule>emptyList();
    }

    public static class SubstRuleFormat3 extends ContextualSubstRule {
        private static final long serialVersionUID = -1840126702536353850L;
        List<Set<Integer>> coverages;
        SubstLookupRecord[] substLookupRecords;

        public SubstRuleFormat3(List<Set<Integer>> coverages, SubstLookupRecord[] substLookupRecords) {
            this.coverages = coverages;
            this.substLookupRecords = substLookupRecords;
        }

        @Override
        public int getContextLength() {
            return coverages.size();
        }

        @Override
        public SubstLookupRecord[] getSubstLookupRecords() {
            return substLookupRecords;
        }

        @Override
        public boolean isGlyphMatchesInput(int glyphId, int atIdx) {
            return coverages.get(atIdx).contains(glyphId);
        }
    }

}
