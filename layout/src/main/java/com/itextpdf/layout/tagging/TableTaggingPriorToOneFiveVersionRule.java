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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.WaitingTagsManager;
import java.util.HashSet;
import java.util.Set;

class TableTaggingPriorToOneFiveVersionRule implements ITaggingRule {
    private Set<TaggingHintKey> finishForbidden = new HashSet<>();

    @Override
    public boolean onTagFinish(LayoutTaggingHelper taggingHelper, TaggingHintKey taggingHintKey) {
        if (taggingHintKey.getAccessibleElement() != null) {
            String role = taggingHintKey.getAccessibleElement().getAccessibilityProperties().getRole();
            if (StandardRoles.THEAD.equals(role) || StandardRoles.TFOOT.equals(role)) {
                finishForbidden.add(taggingHintKey);
                return false;
            }
        }

        for (TaggingHintKey hint : taggingHelper.getAccessibleKidsHint(taggingHintKey)) {
            String role = hint.getAccessibleElement().getAccessibilityProperties().getRole();
            if (StandardRoles.TBODY.equals(role) || StandardRoles.THEAD.equals(role) || StandardRoles.TFOOT.equals(role)) {
                // THead and TFoot are not finished thanks to this rule logic, TBody not finished because it's dummy and Table itself not finished
                removeTagUnavailableInPriorToOneDotFivePdf(hint, taggingHelper);
            }
        }
        return true;
    }

    private void removeTagUnavailableInPriorToOneDotFivePdf(TaggingHintKey taggingHintKey, LayoutTaggingHelper taggingHelper) {
        taggingHelper.replaceKidHint(taggingHintKey, taggingHelper.getAccessibleKidsHint(taggingHintKey));
        PdfDocument pdfDocument = taggingHelper.getPdfDocument();
        WaitingTagsManager waitingTagsManager = pdfDocument.getTagStructureContext().getWaitingTagsManager();
        TagTreePointer tagPointer = new TagTreePointer(pdfDocument);
        if (waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, taggingHintKey)) {
            waitingTagsManager.removeWaitingState(taggingHintKey);
            tagPointer.removeTag();
        }
        if (finishForbidden.remove(taggingHintKey)) {
            taggingHintKey.setFinished();
        }
    }
}
