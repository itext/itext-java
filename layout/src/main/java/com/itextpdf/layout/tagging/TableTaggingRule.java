/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.CaptionSide;
import com.itextpdf.layout.properties.Property;

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
            final String kidRole = getKidRole(kidKey,taggingHelper);
            final boolean isCell = StandardRoles.TD.equals(kidRole) || StandardRoles.TH.equals(kidRole);
            if (isCell && kidKey.getAccessibleElement() instanceof Cell) {
                final Cell cell = (Cell) kidKey.getAccessibleElement();
                final int rowInd = cell.getRow();
                final int colInd = cell.getCol();
                TreeMap<Integer, TaggingHintKey> rowTags = tableTags.get(rowInd);
                if (rowTags == null) {
                    rowTags = new TreeMap<>();
                    tableTags.put(rowInd, rowTags);
                }
                rowTags.put(colInd, kidKey);
            } else if (isCell) {
                tableCellTagsUnindexed.add(kidKey);
            } else {
                nonCellKids.add(kidKey);
            }
        }

        TaggingDummyElement tbodyTag = getTbodyTag(tableHintKey);

        for (TaggingHintKey nonCellKid : nonCellKids) {
            String kidRole = getKidRole(nonCellKid,taggingHelper);
            if (!StandardRoles.THEAD.equals(kidRole) && !StandardRoles.TFOOT.equals(kidRole)
                    && !StandardRoles.CAPTION.equals(kidRole)) {
                // In usual cases it isn't expected that this for loop will work, but it is possible to
                // create custom tag hierarchy by specifying role, and put any child to tableHintKey
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }
        for (TaggingHintKey nonCellKid : nonCellKids) {
            if (StandardRoles.THEAD.equals(getKidRole(nonCellKid,taggingHelper))) {
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }
        taggingHelper.addKidsHint(tableHintKey,
                Collections.<TaggingHintKey>singletonList(LayoutTaggingHelper.getOrCreateHintKey(tbodyTag)), -1);
        for (TaggingHintKey nonCellKid : nonCellKids) {
            if (StandardRoles.TFOOT.equals(getKidRole(nonCellKid,taggingHelper))) {
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

        for (TaggingHintKey nonCellKid : nonCellKids) {
            if (StandardRoles.CAPTION.equals(getKidRole(nonCellKid,taggingHelper))) {
                moveCaption(taggingHelper, nonCellKid, tableHintKey);
            }
        }

        return true;
    }

    private static String getKidRole(TaggingHintKey kidKey, LayoutTaggingHelper helper) {
        return helper
                .getPdfDocument()
                .getTagStructureContext()
                .resolveMappingToStandardOrDomainSpecificRole(kidKey.getAccessibilityProperties().getRole(),null)
                .getRole();
    }

    /**
     * Creates a dummy element with {@link StandardRoles#TBODY} role if needed.
     * Otherwise, returns a dummy element with a null role.
     *
     * @param tableHintKey the hint key of the table.
     *
     * @return a dummy element with {@link StandardRoles#TBODY} role if needed.
     */
    private static TaggingDummyElement getTbodyTag(TaggingHintKey tableHintKey) {
        boolean createTBody = true;
        if (tableHintKey.getAccessibleElement() instanceof Table) {
            Table modelElement = (Table) tableHintKey.getAccessibleElement();
            createTBody = modelElement.getHeader() != null && !modelElement.isSkipFirstHeader()
                    || modelElement.getFooter() != null && !modelElement.isSkipLastFooter();
        }
        return new TaggingDummyElement(createTBody ? StandardRoles.TBODY : null);
    }

    private static void moveCaption(LayoutTaggingHelper taggingHelper, TaggingHintKey caption,
            TaggingHintKey tableHintKey) {
        if (!(tableHintKey.getAccessibleElement() instanceof Table)) {
            return;
        }
        Table tableElem = (Table) tableHintKey.getAccessibleElement();
        Div captionDiv = tableElem.getCaption();
        if (captionDiv == null) {
            return;
        }
        CaptionSide captionSide;
        if (captionDiv.<CaptionSide>getProperty(Property.CAPTION_SIDE) == null) {
            captionSide = CaptionSide.TOP;
        } else {
            captionSide = (CaptionSide) captionDiv.<CaptionSide>getProperty(Property.CAPTION_SIDE);
        }
        if (CaptionSide.TOP.equals(captionSide)) {
            taggingHelper.moveKidHint(caption, tableHintKey, 0);
        } else {
            taggingHelper.moveKidHint(caption, tableHintKey);
        }
    }

}
