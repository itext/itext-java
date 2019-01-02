/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class TableTaggingRule implements ITaggingRule {

    public boolean onTagFinish(LayoutTaggingHelper taggingHelper, TaggingHintKey tableHintKey) {
        List<TaggingHintKey> kidKeys = taggingHelper.getAccessibleKidsHint(tableHintKey);

        Map<Integer, TreeMap<Integer, TaggingHintKey>> tableTags = new TreeMap<>();
        List<TaggingHintKey> tableCellTagsUnindexed = new ArrayList<>();
        List<TaggingHintKey> nonCellKids = new ArrayList<>();
        for (TaggingHintKey kidKey : kidKeys) {
            if (StandardRoles.TD.equals(kidKey.getAccessibleElement().getAccessibilityProperties().getRole())
                    || StandardRoles.TH.equals(kidKey.getAccessibleElement().getAccessibilityProperties().getRole())) {
                if (kidKey.getAccessibleElement() instanceof Cell) {
                    Cell cell = (Cell) kidKey.getAccessibleElement();
                    int rowInd = cell.getRow();
                    int colInd = cell.getCol();
                    TreeMap<Integer, TaggingHintKey> rowTags = tableTags.get(rowInd);
                    if (rowTags == null) {
                        rowTags = new TreeMap<>();
                        tableTags.put(rowInd, rowTags);
                    }
                    rowTags.put(colInd, kidKey);
                } else {
                    tableCellTagsUnindexed.add(kidKey);
                }

            } else {
                nonCellKids.add(kidKey);
            }
        }

        boolean createTBody = true;
        if (tableHintKey.getAccessibleElement() instanceof Table) {
            Table modelElement = (Table) tableHintKey.getAccessibleElement();
            createTBody = modelElement.getHeader() != null && !modelElement.isSkipFirstHeader()
                    || modelElement.getFooter() != null && !modelElement.isSkipLastFooter();
        }
        TaggingDummyElement tbodyTag = null;
        tbodyTag = new TaggingDummyElement(createTBody ? StandardRoles.TBODY : null);

        for (TaggingHintKey nonCellKid : nonCellKids) {
            String kidRole = nonCellKid.getAccessibleElement().getAccessibilityProperties().getRole();
            if (!StandardRoles.THEAD.equals(kidRole) && !StandardRoles.TFOOT.equals(kidRole)) {
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }
        for (TaggingHintKey nonCellKid : nonCellKids) {
            String kidRole = nonCellKid.getAccessibleElement().getAccessibilityProperties().getRole();
            if (StandardRoles.THEAD.equals(kidRole)) {
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }
        taggingHelper.addKidsHint(tableHintKey, Collections.<TaggingHintKey>singletonList(LayoutTaggingHelper.getOrCreateHintKey(tbodyTag)), -1);
        for (TaggingHintKey nonCellKid : nonCellKids) {
            String kidRole = nonCellKid.getAccessibleElement().getAccessibilityProperties().getRole();
            if (StandardRoles.TFOOT.equals(kidRole)) {
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }

        for (TreeMap<Integer, TaggingHintKey> rowTags : tableTags.values()) {
            TaggingDummyElement row = new TaggingDummyElement(StandardRoles.TR);
            TaggingHintKey rowTagHint = LayoutTaggingHelper.getOrCreateHintKey(row);
            for (TaggingHintKey cellTagHint : rowTags.values()) {
                taggingHelper.moveKidHint(cellTagHint, rowTagHint);
            }
            if (tableCellTagsUnindexed != null) {
                for (TaggingHintKey cellTagHint : tableCellTagsUnindexed) {
                    taggingHelper.moveKidHint(cellTagHint, rowTagHint);
                }
                tableCellTagsUnindexed = null;
            }
            taggingHelper.addKidsHint(tbodyTag, Collections.<TaggingDummyElement>singletonList(row), -1);
        }

        return true;
    }
}
