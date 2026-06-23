package com.itextpdf.layout.tagging;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;

class FootnoteTaggingRule implements ITaggingRule{

    public FootnoteTaggingRule() {
        //default constructor
    }

    @Override
    public boolean onTagFinish(LayoutTaggingHelper taggingHelper, TaggingHintKey taggingHintKey) {
        if (taggingHintKey.getAccessibleElement() instanceof FootnoteAnchor) {
            // get to footnote child
            TaggingHintKey footnoteTag = null;
            for (TaggingHintKey child : taggingHelper.getKidsHint(taggingHintKey)) {
                if (child.getAccessibleElement() instanceof Footnote) {
                    footnoteTag = child;
                    break;
                }
            }
            if (footnoteTag == null) {
                return true;
            }
            //find paragraph parent
            TaggingHintKey pk = taggingHelper.getParentHint(taggingHintKey);
            while (pk != null && !(pk.getAccessibleElement() instanceof Paragraph)) {
                pk = taggingHelper.getParentHint(pk);
            }
            if (pk != null) {
                taggingHelper.moveKidHint(footnoteTag,pk);
            }
        }
        return true;
    }
}
