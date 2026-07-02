/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
