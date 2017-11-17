package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.PdfName;
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
            if (PdfName.TD.equals(kidKey.getAccessibleElement().getRole()) || PdfName.TH.equals(kidKey.getAccessibleElement().getRole())) {
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
        tbodyTag = new TaggingDummyElement(createTBody ? PdfName.TBody : null);

        for (TaggingHintKey nonCellKid : nonCellKids) {
            PdfName kidRole = nonCellKid.getAccessibleElement().getRole();
            if (!PdfName.THead.equals(kidRole) && !PdfName.TFoot.equals(kidRole)) {
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }
        for (TaggingHintKey nonCellKid : nonCellKids) {
            PdfName kidRole = nonCellKid.getAccessibleElement().getRole();
            if (PdfName.THead.equals(kidRole)) {
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }
        taggingHelper.addKidsHint(tableHintKey, Collections.<TaggingHintKey>singletonList(LayoutTaggingHelper.getOrCreateHintKey(tbodyTag)), -1);
        for (TaggingHintKey nonCellKid : nonCellKids) {
            PdfName kidRole = nonCellKid.getAccessibleElement().getRole();
            if (PdfName.TFoot.equals(kidRole)) {
                taggingHelper.moveKidHint(nonCellKid, tableHintKey);
            }
        }

        for (TreeMap<Integer, TaggingHintKey> rowTags : tableTags.values()) {
            TaggingDummyElement row = new TaggingDummyElement(PdfName.TR);
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
