/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
