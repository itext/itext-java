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
